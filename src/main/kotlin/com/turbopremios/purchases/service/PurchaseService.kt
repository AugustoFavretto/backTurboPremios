package com.turbopremios.purchases.service

import com.turbopremios.affiliate.entity.Commission
import com.turbopremios.affiliate.repository.AffiliateRepository
import com.turbopremios.affiliate.repository.CommissionRepository
import com.turbopremios.auth.repository.UserRepository
import com.turbopremios.campaigns.repository.CampaignRepository
import com.turbopremios.exceptions.BadRequestException
import com.turbopremios.exceptions.NotFoundException
import com.turbopremios.integrations.pix.PaymentGateway
import com.turbopremios.integrations.pix.PixPaymentRequest
import com.turbopremios.purchases.dto.CreatePurchaseRequest
import com.turbopremios.purchases.dto.PurchaseResponse
import com.turbopremios.purchases.entity.Purchase
import com.turbopremios.purchases.repository.PurchaseRepository
import com.turbopremios.tickets.dto.TicketResponse
import com.turbopremios.tickets.entity.Ticket
import com.turbopremios.tickets.repository.TicketRepository
import com.turbopremios.tickets.service.TicketNumberGenerator
import com.turbopremios.tickets.service.toResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class PurchaseService(
    private val purchaseRepository: PurchaseRepository,
    private val campaignRepository: CampaignRepository,
    private val ticketRepository: TicketRepository,
    private val ticketNumberGenerator: TicketNumberGenerator,
    private val affiliateRepository: AffiliateRepository,
    private val commissionRepository: CommissionRepository,
    private val paymentGateway: PaymentGateway,
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(PurchaseService::class.java)

    @Transactional
    fun createPurchase(
        request: CreatePurchaseRequest,
        userId: String?
    ): PurchaseResponse {

        val campaign = campaignRepository.findById(request.campaignId)
            .orElseThrow { NotFoundException("Campanha não encontrada.") }

        if (campaign.status != "active") {
            throw BadRequestException("Esta campanha não está ativa.")
        }

        val availableTickets = campaign.totalTickets - campaign.soldTickets

        if (request.quantity > availableTickets) {
            throw BadRequestException(
                "Bilhetes insuficientes. Disponíveis: $availableTickets"
            )
        }
        val userAffiliate = userRepository.findByAffiliateCode(request.affiliateCode.orEmpty())
        val userEmail = userRepository.getByEmail(request.userEmail.orEmpty())
//        if (userAffiliate?.cpf == request.userCpf || userEmail?.cpf == request.userCpf) {
//            throw BadRequestException("Operação inválida!")
//        }

        val total = campaign.ticketPrice.multiply(
            BigDecimal(request.quantity)
        )

        // 1. cria purchase
        val purchase = Purchase(
            campaignId = campaign.id,
            userId = userId,
            userPhone = request.userPhone,
            userEmail = request.userEmail,
            userName = request.userName,
            affiliateCode = if (userAffiliate == null) "" else request.affiliateCode,
            quantity = request.quantity,
            total = total
        )

        // 2. salva primeiro
        val savedPurchase = purchaseRepository.save(purchase)

        // 3. gera pix
        val pixResult = paymentGateway.generatePix(
            userId = userId,
            PixPaymentRequest(
                purchaseId = savedPurchase.id,
                amount = total,
                payerName = request.userName ?: "Cliente",
                payerEmail = request.userEmail,
                description = "Turbo Prêmios - ${campaign.title}",
                payerCpf = request.userCpf.orEmpty()
            )
        )

        // 4. atualiza dados do pagamento
        savedPurchase.gatewayPaymentId = pixResult.gatewayPaymentId
        savedPurchase.pixCode = pixResult.pixCode
        savedPurchase.pixQrCode = pixResult.pixQrCodeBase64
        savedPurchase.pixExpiresAt = pixResult.expiresAt

        // 5. salva novamente
        val updatedPurchase = purchaseRepository.save(savedPurchase)

        log.info(
            "Purchase created: {} for campaign: {}",
            updatedPurchase.id,
            campaign.id
        )

        return PurchaseResponse(
            id = updatedPurchase.id,
            campaignId = updatedPurchase.campaignId,
            tickets = emptyList(),
            total = updatedPurchase.total,
            paymentMethod = updatedPurchase.paymentMethod,
            paymentStatus = updatedPurchase.paymentStatus,
            createdAt = updatedPurchase.createdAt,
            pixCode = updatedPurchase.pixCode,
            pixQrCode = updatedPurchase.pixQrCode,
            pixExpiresAt = updatedPurchase.pixExpiresAt
        )
    }

    @Transactional
    fun confirmPayment(purchaseId: String) {
        val purchase = purchaseRepository
            .findByGatewayPaymentId(purchaseId)
            ?: throw NotFoundException(
                "Compra não encontrada."
            )

        if (purchase.paymentStatus == "paid") {
            log.warn("Purchase {} already paid, skipping", purchaseId)
            return
        }

        purchase.paymentStatus = "paid"
        purchase.paidAt = java.time.LocalDateTime.now()
        purchaseRepository.save(purchase)

        val campaign = campaignRepository.findById(purchase.campaignId)
            .orElseThrow { NotFoundException("Campanha não encontrada.") }

        val existingNumbers = ticketRepository.findNumbersByCampaignId(campaign.id)
        val newNumbers = ticketNumberGenerator.generateUniqueNumbers(purchase.quantity, existingNumbers)

        val tickets = newNumbers.map { number ->
            Ticket(
                number = number,
                campaignId = campaign.id,
                purchaseId = purchase.id,
                userId = purchase.userId,
                userPhone = purchase.userPhone,
                userEmail = purchase.userEmail,
                price = campaign.ticketPrice
            )
        }

        ticketRepository.saveAll(tickets)

        campaign.soldTickets += purchase.quantity
        campaignRepository.save(campaign)

        if (!purchase.affiliateCode.isNullOrBlank()) {
            processAffiliateCommission(purchase)
        }

        log.info("Payment confirmed for purchase {}: {} tickets generated", purchaseId, tickets.size)
    }

    private fun processAffiliateCommission(purchase: Purchase) {
        val affiliate = affiliateRepository.findByCode(purchase.affiliateCode!!)
        if (affiliate.isEmpty) {
            log.warn("Affiliate not found for code: {}", purchase.affiliateCode)
            return
        }

        val aff = affiliate.get()
        val commissionAmount = purchase.total
            .multiply(BigDecimal("10.00"))
            .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)

        val commission = Commission(
            affiliateId = aff.id,
            purchaseId = purchase.id,
            amount = commissionAmount,
            rate = BigDecimal("10.00"),
            status = "approved",
            buyerName = purchase.userName
        )
        commissionRepository.save(commission)

        aff.pendingCommission = aff.pendingCommission.add(commissionAmount)
        aff.totalSales += 1
        aff.totalRevenue = aff.totalRevenue.add(purchase.total)
        aff.recalculateConversionRate()
        affiliateRepository.save(aff)

        log.info("Commission created: {} for affiliate: {}", commissionAmount, aff.code)
    }

    @Transactional(readOnly = true)
    fun getPurchaseById(id: String, userId: String): PurchaseResponse {
        val purchase = purchaseRepository.findById(id)
            .orElseThrow { NotFoundException("Compra não encontrada.") }

        if (purchase.userId != null && purchase.userId != userId) {
            throw com.turbopremios.exceptions.ForbiddenException("Acesso negado.")
        }

        val tickets = ticketRepository.findByPurchaseId(purchase.id)
        return purchase.toResponse(tickets.map { it.toResponse(null) })
    }

    @Transactional(readOnly = true)
    fun getUserPurchases(userId: String): List<PurchaseResponse> {
        return purchaseRepository.findByUserId(userId).map { purchase ->
            val tickets = ticketRepository.findByPurchaseId(purchase.id)
            purchase.toResponse(tickets.map { it.toResponse(null) })
        }
    }
}

fun Purchase.toResponse(tickets: List<TicketResponse> = emptyList()) = PurchaseResponse(
    id = id,
    campaignId = campaignId,
    tickets = tickets,
    total = total,
    paymentMethod = paymentMethod,
    paymentStatus = paymentStatus,
    createdAt = createdAt,
    pixCode = pixCode,
    pixQrCode = pixQrCode,
    pixExpiresAt = pixExpiresAt,
    paidAt = paidAt
)

