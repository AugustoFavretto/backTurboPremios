package com.turbopremios.dashboard.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDateTime

data class DashboardStatsResponse(
    val totalTickets: Long,
    val activeTickets: Long,
    val totalSpent: BigDecimal,
    val totalWon: BigDecimal,
    val activeCampaigns: Int,
    val nextDraw: LocalDateTime?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ActivityResponse(
    val id: String,
    val type: String,
    val description: String,
    val amount: BigDecimal?,
    val createdAt: LocalDateTime
)
