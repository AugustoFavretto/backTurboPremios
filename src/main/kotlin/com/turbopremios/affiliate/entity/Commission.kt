package com.turbopremios.affiliate.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "commissions")
class Commission(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "affiliate_id", nullable = false, length = 36)
    val affiliateId: String,

    @Column(name = "purchase_id", nullable = false, length = 36)
    val purchaseId: String,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @Column(name = "rate", nullable = false)
    val rate: BigDecimal = BigDecimal("10.00"),

    @Column(name = "status", nullable = false)
    var status: String = "pending",

    @Column(name = "buyer_name")
    val buyerName: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() { updatedAt = LocalDateTime.now() }
}
