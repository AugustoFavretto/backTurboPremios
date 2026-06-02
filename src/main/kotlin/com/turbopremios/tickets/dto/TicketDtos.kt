package com.turbopremios.tickets.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TicketResponse(
    val id: String,
    val number: String,
    val campaignId: String,
    val campaignTitle: String?,
    val userId: String?,
    val userPhone: String?,
    val userEmail: String?,
    val purchasedAt: LocalDateTime,
    val status: String,
    val price: BigDecimal
)
