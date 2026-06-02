package com.turbopremios.affiliate.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AffiliateProfileResponse(
    val id: String,
    val userId: String,
    val code: String,
    val referralLink: String,
    val totalClicks: Int,
    val totalSales: Int,
    val totalRevenue: BigDecimal,
    val pendingCommission: BigDecimal,
    val paidCommission: BigDecimal,
    val conversionRate: BigDecimal,
    val createdAt: LocalDateTime
)

data class MonthlyRevenueDto(
    val month: String,
    val revenue: BigDecimal,
    val sales: Int
)

data class AffiliateStatsResponse(
    val totalClicks: Int,
    val totalSales: Int,
    val totalRevenue: BigDecimal,
    val pendingCommission: BigDecimal,
    val conversionRate: BigDecimal,
    val monthlyRevenue: List<MonthlyRevenueDto>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommissionResponse(
    val id: String,
    val affiliateId: String,
    val purchaseId: String,
    val amount: BigDecimal,
    val rate: BigDecimal,
    val status: String,
    val createdAt: LocalDateTime,
    val buyerName: String?
)

data class WithdrawRequest(
    @field:NotNull(message = "Valor é obrigatório")
    @field:DecimalMin(value = "0.01", message = "Valor mínimo de saque é R$ 0,01")
    val amount: BigDecimal
)
