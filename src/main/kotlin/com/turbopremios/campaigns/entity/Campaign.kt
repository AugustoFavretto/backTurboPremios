package com.turbopremios.campaigns.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "campaigns")
class Campaign(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "image_url", length = 500)
    var imageUrl: String? = null,

    @Column(name = "prize_value", nullable = false)
    var prizeValue: BigDecimal,

    @Column(name = "ticket_price", nullable = false)
    var ticketPrice: BigDecimal,

    @Column(name = "total_tickets", nullable = false)
    var totalTickets: Int,

    @Column(name = "sold_tickets", nullable = false)
    var soldTickets: Int = 0,

    @Column(name = "draw_date", nullable = false)
    var drawDate: LocalDateTime,

    @Column(name = "status", nullable = false)
    var status: String = "upcoming",

    @Column(name = "prize", nullable = false)
    var prize: String,

    @Column(name = "category", nullable = false)
    var category: String,

    @Column(name = "featured", nullable = false)
    var featured: Boolean = false,

    @Column(name = "winner_ticket", length = 10)
    var winnerTicket: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() { updatedAt = LocalDateTime.now() }
}
