package com.turbopremios.winners.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "winners")
class Winner(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "campaign_id", nullable = false, length = 36)
    val campaignId: String,

    @Column(name = "ticket_id", nullable = false, length = 36)
    val ticketId: String,

    @Column(name = "user_id", length = 36)
    val userId: String? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "prize", nullable = false)
    val prize: String,

    @Column(name = "campaign_title", nullable = false)
    val campaignTitle: String,

    @Column(name = "ticket_number", nullable = false, length = 5)
    val ticketNumber: String,

    @Column(name = "photo_url", length = 500)
    val photoUrl: String? = null,

    @Column(name = "draw_date", nullable = false)
    val drawDate: LocalDate,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
