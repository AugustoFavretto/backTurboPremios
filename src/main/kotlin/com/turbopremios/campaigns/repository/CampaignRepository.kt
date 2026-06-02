package com.turbopremios.campaigns.repository

import com.turbopremios.campaigns.entity.Campaign
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CampaignRepository : JpaRepository<Campaign, String> {
    fun findByStatus(status: String, pageable: Pageable): Page<Campaign>
    fun findByFeaturedTrueAndStatus(status: String): List<Campaign>
    fun countByStatus(status: String): Long
    fun findAllBy(pageable: Pageable): Page<Campaign>
}
