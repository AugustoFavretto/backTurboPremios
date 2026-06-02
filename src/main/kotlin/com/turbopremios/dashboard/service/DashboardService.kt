package com.turbopremios.dashboard.service

import com.turbopremios.campaigns.repository.CampaignRepository
import com.turbopremios.dashboard.dto.ActivityResponse
import com.turbopremios.dashboard.dto.DashboardStatsResponse
import com.turbopremios.purchases.repository.PurchaseRepository
import com.turbopremios.tickets.repository.TicketRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Service
class DashboardService(
    private val ticketRepository: TicketRepository,
    private val campaignRepository: CampaignRepository,
    private val purchaseRepository: PurchaseRepository
) {

    @Transactional(readOnly = true)
    fun getStats(userId: String): DashboardStatsResponse {
        val totalTickets = ticketRepository.countByUserId(userId)
        val activeTickets = ticketRepository.countByUserIdAndStatus(userId, "active")
        val totalSpent = ticketRepository.sumPriceByUserId(userId) ?: BigDecimal.ZERO

        val activeCampaignIds = ticketRepository.findDistinctActiveCampaignIdsByUserId(userId)
        val activeCampaigns = activeCampaignIds.size

        val nextDraw = if (activeCampaignIds.isNotEmpty()) {
            campaignRepository.findAllById(activeCampaignIds)
                .filter { it.status == "active" }
                .minOfOrNull { it.drawDate }
        } else null

        return DashboardStatsResponse(
            totalTickets = totalTickets,
            activeTickets = activeTickets,
            totalSpent = totalSpent,
            totalWon = BigDecimal.ZERO,
            activeCampaigns = activeCampaigns,
            nextDraw = nextDraw
        )
    }

    @Transactional(readOnly = true)
    fun getActivity(userId: String): List<ActivityResponse> {
        val purchases = purchaseRepository.findByUserId(userId)
            .sortedByDescending { it.createdAt }
            .take(10)

        return purchases.map { purchase ->
            ActivityResponse(
                id = UUID.randomUUID().toString(),
                type = "purchase",
                description = "Comprou ${purchase.quantity} bilhete(s)",
                amount = purchase.total,
                createdAt = purchase.createdAt
            )
        }
    }
}
