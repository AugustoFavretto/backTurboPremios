package com.turbopremios.auth.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "password_reset_tokens")
class PasswordResetToken(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val token: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = false)
    val expiresAt: LocalDateTime,

    @Column(nullable = false)
    var used: Boolean = false
)