package com.turbopremios.affiliate.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "withdraw_requests")
class WithdrawRequest(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "affiliate_id", nullable = false, length = 36)
    val affiliateId: String,

    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,

    @Column(name = "status", nullable = false)
    var status: String = "processing",

    @Column(name = "pix_key")
    val pixKey: String? = null,

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
