package com.turbopremios.auth.entity

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(name = "id", length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "cpf", nullable = false, unique = true)
    var cpf: String,

    @Column(name = "phone")
    var phone: String? = null,

    @Column(name = "asaas_customer_id")
    var asaasCustomerId: String? = null,

    @Column(name = "password_hash", nullable = false)
    private var passwordHash: String,

    @Column(name = "role", nullable = false)
    var role: String = "user",

    @Column(name = "affiliate_code", unique = true)
    var affiliateCode: String? = null,

    @Column(name = "balance", nullable = false)
    var balance: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_commission", nullable = false)
    var totalCommission: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${role.uppercase()}"))

    override fun getPassword(): String = passwordHash

    fun setPassword(hash: String) { passwordHash = hash }

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    @PreUpdate
    fun onUpdate() { updatedAt = LocalDateTime.now() }
}
