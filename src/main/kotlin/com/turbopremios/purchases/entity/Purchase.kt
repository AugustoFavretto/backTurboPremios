package com.turbopremios.purchases.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "purchases")
class Purchase(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "campaign_id", nullable = false, length = 36)
    val campaignId: String,

    @Column(name = "user_id", length = 36)
    var userId: String? = null,

    @Column(name = "user_phone", length = 20)
    var userPhone: String? = null,

    @Column(name = "user_email")
    var userEmail: String? = null,

    @Column(name = "user_name")
    var userName: String? = null,

    @Column(name = "affiliate_code", length = 20)
    var affiliateCode: String? = null,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "total", nullable = false)
    val total: BigDecimal,

    @Column(name = "payment_method", nullable = false)
    val paymentMethod: String = "pix",

    @Column(name = "payment_status", nullable = false)
    var paymentStatus: String = "pending",

    @Column(name = "pix_code", columnDefinition = "TEXT")
    var pixCode: String? = null,

    @Column(name = "pix_qr_code", columnDefinition = "TEXT")
    var pixQrCode: String? = null,

    @Column(name = "gateway_payment_id")
    var gatewayPaymentId: String? = null,

    @Column(name = "pix_expires_at")
    var pixExpiresAt: LocalDateTime? = null,

    @Column(name = "paid_at")
    var paidAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() { updatedAt = LocalDateTime.now() }
}
