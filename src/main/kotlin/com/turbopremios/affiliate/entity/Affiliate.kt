package com.turbopremios.affiliate.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "affiliates")
class Affiliate(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    val userId: String,

    @Column(name = "code", nullable = false, unique = true, length = 20)
    val code: String,

    @Column(name = "referral_link", nullable = false, length = 500)
    val referralLink: String,

    @Column(name = "total_clicks", nullable = false)
    var totalClicks: Int = 0,

    @Column(name = "total_sales", nullable = false)
    var totalSales: Int = 0,

    @Column(name = "total_revenue", nullable = false)
    var totalRevenue: BigDecimal = BigDecimal.ZERO,

    @Column(name = "pending_commission", nullable = false)
    var pendingCommission: BigDecimal = BigDecimal.ZERO,

    @Column(name = "paid_commission", nullable = false)
    var paidCommission: BigDecimal = BigDecimal.ZERO,

    @Column(name = "conversion_rate", nullable = false)
    var conversionRate: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() { updatedAt = LocalDateTime.now() }

    fun recalculateConversionRate() {
        conversionRate = if (totalClicks > 0) {
            BigDecimal(totalSales.toDouble() / totalClicks * 100).setScale(2, java.math.RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
    }
}
