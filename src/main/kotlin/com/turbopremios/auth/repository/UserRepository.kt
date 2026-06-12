package com.turbopremios.auth.repository

import com.turbopremios.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): Optional<User>
    fun getByEmail(email: String): User?
    fun findByAffiliateCode(code: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
}
