package com.turbopremios.affiliate.repository

import com.turbopremios.affiliate.entity.Affiliate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AffiliateRepository : JpaRepository<Affiliate, String> {
    fun findByUserId(userId: String): Optional<Affiliate>
    fun findByCode(code: String): Optional<Affiliate>
    fun existsByCode(code: String): Boolean
}
