package com.turbopremios.affiliate.service

import com.turbopremios.affiliate.dto.*
import com.turbopremios.affiliate.entity.Affiliate
import com.turbopremios.affiliate.entity.WithdrawRequest
import com.turbopremios.affiliate.repository.AffiliateRepository
import com.turbopremios.affiliate.repository.CommissionRepository
import com.turbopremios.affiliate.repository.WithdrawRequestRepository
import com.turbopremios.auth.repository.UserRepository
import com.turbopremios.config.FrontendProperties
import com.turbopremios.exceptions.BadRequestException
import com.turbopremios.exceptions.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Service
class AffiliateService(
    private val affiliateRepository: AffiliateRepository,
    private val commissionRepository: CommissionRepository,
    private val withdrawRequestRepository: WithdrawRequestRepository,
    private val userRepository: UserRepository,
    private val frontendProperties: FrontendProperties
) {
    private val log = LoggerFactory.getLogger(AffiliateService::class.java)

    @Transactional
    fun getProfile(userId: String): AffiliateProfileResponse {
        val affiliate = affiliateRepository.findByUserId(userId)
            .orElseGet { createAffiliateForUser(userId) }
        return affiliate.toProfileResponse()
    }

    @Transactional(readOnly = true)
    fun getStats(userId: String): AffiliateStatsResponse {
        val affiliate = affiliateRepository.findByUserId(userId)
            .orElseGet { createAffiliateForUser(userId) }

        val monthlyRevenue = buildMonthlyRevenue(affiliate)

        return AffiliateStatsResponse(
            totalClicks = affiliate.totalClicks,
            totalSales = affiliate.totalSales,
            totalRevenue = affiliate.totalRevenue,
            pendingCommission = affiliate.pendingCommission,
            conversionRate = affiliate.conversionRate,
            monthlyRevenue = monthlyRevenue
        )
    }

    @Transactional(readOnly = true)
    fun getCommissions(userId: String): List<CommissionResponse> {
        val affiliate = affiliateRepository.findByUserId(userId)
            .orElseGet { createAffiliateForUser(userId) }
        return commissionRepository.findByAffiliateId(affiliate.id)
            .map { it.toResponse() }
    }

    @Transactional
    fun requestWithdraw(userId: String, amount: BigDecimal): String {
        val affiliate = affiliateRepository.findByUserId(userId)
            .orElseThrow { NotFoundException("Perfil de afiliado não encontrado.") }

        if (amount > affiliate.pendingCommission) {
            throw BadRequestException("Saldo insuficiente para saque.")
        }

        val withdrawReq = WithdrawRequest(
            affiliateId = affiliate.id,
            amount = amount
        )
        withdrawRequestRepository.save(withdrawReq)

        affiliate.pendingCommission = affiliate.pendingCommission.subtract(amount)
        affiliate.paidCommission = affiliate.paidCommission.add(amount)
        affiliateRepository.save(affiliate)

        val commissions = commissionRepository.findByAffiliateIdAndStatus(affiliate.id, "approved")
        var remaining = amount
        for (commission in commissions) {
            if (remaining <= BigDecimal.ZERO) break
            commission.status = "paid"
            remaining = remaining.subtract(commission.amount)
        }
        commissionRepository.saveAll(commissions)

        log.info("Withdraw requested: {} for affiliate: {}", amount, affiliate.code)
        return "Saque solicitado com sucesso! Será processado em até 24h."
    }

    @Transactional
    fun trackClick(affiliateCode: String) {
        affiliateRepository.findByCode(affiliateCode).ifPresent { affiliate ->
            affiliate.totalClicks += 1
            affiliate.recalculateConversionRate()
            affiliateRepository.save(affiliate)
        }
    }

    @Transactional
    private fun createAffiliateForUser(userId: String): Affiliate {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("Usuário não encontrado.") }

        val code = generateAffiliateCode(user.name)
        val affiliate = Affiliate(
            userId = userId,
            code = code,
            referralLink = "${frontendProperties.baseUrl}/ref/$code"
        )

        user.affiliateCode = code
        user.role = "affiliate"
        userRepository.save(user)

        return affiliateRepository.save(affiliate)
    }

    private fun generateAffiliateCode(name: String): String {
        val baseName = name.split(" ").first().uppercase()
            .take(8)
            .replace(Regex("[^A-Z]"), "")
        var code = "${baseName}10"
        var suffix = 10
        while (affiliateRepository.existsByCode(code)) {
            suffix++
            code = "${baseName}${suffix}"
        }
        return code
    }

    private fun buildMonthlyRevenue(
        affiliate: Affiliate
    ): List<MonthlyRevenueDto> {

        val today = LocalDate.now()

        return (6 downTo 0).map { monthsBack ->
            val month = today.minusMonths(monthsBack.toLong())

            val commissions =
                commissionRepository.findByAffiliateIdAndMonth(
                    affiliate.id,
                    month.monthValue,
                    month.year
                )

            MonthlyRevenueDto(
                month = month.month.getDisplayName(
                    TextStyle.SHORT,
                    Locale("pt", "BR")
                ),
                revenue = commissions.sumOf { it.amount },
                sales = commissions.size
            )
        }
    }
}

fun Affiliate.toProfileResponse() = AffiliateProfileResponse(
    id = id,
    userId = userId,
    code = code,
    referralLink = referralLink,
    totalClicks = totalClicks,
    totalSales = totalSales,
    totalRevenue = totalRevenue,
    pendingCommission = pendingCommission,
    paidCommission = paidCommission,
    conversionRate = conversionRate,
    createdAt = createdAt
)

fun com.turbopremios.affiliate.entity.Commission.toResponse() = CommissionResponse(
    id = id,
    affiliateId = affiliateId,
    purchaseId = purchaseId,
    amount = amount,
    rate = rate,
    status = status,
    createdAt = createdAt,
    buyerName = buyerName
)
