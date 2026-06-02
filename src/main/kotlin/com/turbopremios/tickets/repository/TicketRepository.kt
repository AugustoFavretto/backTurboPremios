package com.turbopremios.tickets.repository

import com.turbopremios.tickets.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<Ticket, String> {
    fun findByUserId(userId: String): List<Ticket>
    fun findByUserPhone(phone: String): List<Ticket>
    fun findByUserEmail(email: String): List<Ticket>
    fun findByPurchaseId(purchaseId: String): List<Ticket>
    fun existsByNumberAndCampaignId(number: String, campaignId: String): Boolean
    fun findByCampaignId(campaignId: String): List<Ticket>

    @Query("SELECT t.number FROM Ticket t WHERE t.campaignId = :campaignId")
    fun findNumbersByCampaignId(campaignId: String): Set<String>

    fun countByUserIdAndStatus(userId: String, status: String): Long
    fun countByUserId(userId: String): Long

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t WHERE t.userId = :userId")
    fun sumPriceByUserId(userId: String): java.math.BigDecimal?

    @Query("SELECT DISTINCT t.campaignId FROM Ticket t WHERE t.userId = :userId AND t.status = 'active'")
    fun findDistinctActiveCampaignIdsByUserId(userId: String): List<String>
}
