package com.turbopremios.purchases.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.turbopremios.tickets.dto.TicketResponse
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreatePurchaseRequest(
    @field:NotBlank(message = "campaignId é obrigatório")
    val campaignId: String,

    @field:Min(value = 1, message = "Quantidade mínima é 1")
    val quantity: Int,

    val userPhone: String? = null,
    val userEmail: String? = null,
    val userName: String? = null,
    val affiliateCode: String? = null,
    val userCpf: String? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PurchaseResponse(
    val id: String,
    val campaignId: String,
    val tickets: List<TicketResponse>,
    val total: BigDecimal,
    val paymentMethod: String,
    val paymentStatus: String,
    val createdAt: LocalDateTime,
    val pixCode: String? = null,
    val pixQrCode: String? = null,
    val pixExpiresAt: LocalDateTime? = null,
    val paidAt: LocalDateTime? = null
)
