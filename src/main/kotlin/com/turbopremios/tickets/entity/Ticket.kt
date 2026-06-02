package com.turbopremios.tickets.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tickets")
class Ticket(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "number", nullable = false, length = 5)
    val number: String,

    @Column(name = "campaign_id", nullable = false, length = 36)
    val campaignId: String,

    @Column(name = "purchase_id", nullable = false, length = 36)
    val purchaseId: String,

    @Column(name = "user_id", length = 36)
    var userId: String? = null,

    @Column(name = "user_phone", length = 20)
    var userPhone: String? = null,

    @Column(name = "user_email")
    var userEmail: String? = null,

    @Column(name = "status", nullable = false)
    var status: String = "active",

    @Column(name = "price", nullable = false)
    val price: BigDecimal,

    @Column(name = "purchased_at", nullable = false)
    val purchasedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
