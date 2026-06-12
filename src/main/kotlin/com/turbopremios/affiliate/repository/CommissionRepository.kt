package com.turbopremios.affiliate.repository

import com.turbopremios.affiliate.entity.Commission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface CommissionRepository : JpaRepository<Commission, String> {
    fun findByAffiliateId(affiliateId: String): List<Commission>

    @Query(" SELECT c FROM Commission c WHERE c.affiliateId = :affiliateId AND MONTH(c.createdAt) = :month AND YEAR(c.createdAt) = :year")
    fun findByAffiliateIdAndMonth(
        affiliateId: String,
        month: Int,
        year: Int
    ): List<Commission>

    fun findByAffiliateIdAndStatus(affiliateId: String, status: String): List<Commission>

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Commission c WHERE c.affiliateId = :affiliateId AND c.status = :status")
    fun sumAmountByAffiliateIdAndStatus(affiliateId: String, status: String): BigDecimal

    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Commission c WHERE c.affiliateId = :affiliateId AND c.status IN ('pending', 'approved')")
    fun sumPendingByAffiliateId(affiliateId: String): BigDecimal
}
