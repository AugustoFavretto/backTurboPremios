package com.turbopremios.campaigns.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class CampaignResponse(
    val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val prizeValue: BigDecimal,
    val ticketPrice: BigDecimal,
    val totalTickets: Int,
    val soldTickets: Int,
    val drawDate: LocalDateTime,
    val status: String,
    val prize: String,
    val category: String,
    val featured: Boolean
)
