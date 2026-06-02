package com.turbopremios.purchases.repository

import com.turbopremios.purchases.entity.Purchase
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PurchaseRepository : JpaRepository<Purchase, String> {
    fun findByUserId(userId: String): List<Purchase>
    fun findByPaymentStatus(status: String): List<Purchase>
}
