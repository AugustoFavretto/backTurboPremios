package com.turbopremios.affiliate.repository

import com.turbopremios.affiliate.entity.WithdrawRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WithdrawRequestRepository : JpaRepository<WithdrawRequest, String> {
    fun findByAffiliateId(affiliateId: String): List<WithdrawRequest>
}
