package com.turbopremios.auth.repository

import com.turbopremios.auth.entity.PasswordResetToken
import com.turbopremios.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, UUID> {

    fun findByToken(token: String): PasswordResetToken?

    fun deleteByUser(user: User)
}