package com.turbopremios.campaigns.service

import com.turbopremios.campaigns.dto.CampaignResponse
import com.turbopremios.campaigns.entity.Campaign
import com.turbopremios.campaigns.repository.CampaignRepository
import com.turbopremios.common.PaginatedResponse
import com.turbopremios.exceptions.NotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CampaignService(private val campaignRepository: CampaignRepository) {

    @Transactional(readOnly = true)
    fun getCampaigns(page: Int, perPage: Int, status: String?): PaginatedResponse<CampaignResponse> {
        val pageable = PageRequest.of(page - 1, perPage, Sort.by(Sort.Direction.DESC, "createdAt"))
        val result = if (status != null) {
            campaignRepository.findByStatus(status, pageable)
        } else {
            campaignRepository.findAllBy(pageable)
        }
        return PaginatedResponse.of(
            data = result.content.map { it.toResponse() },
            total = result.totalElements,
            page = page,
            perPage = perPage
        )
    }

    @Transactional(readOnly = true)
    fun getFeaturedCampaigns(): List<CampaignResponse> =
        campaignRepository.findByFeaturedTrueAndStatus("active").map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getCampaignById(id: String): CampaignResponse =
        campaignRepository.findById(id)
            .orElseThrow { NotFoundException("Campanha não encontrada.") }
            .toResponse()

    @Transactional(readOnly = true)
    fun getCampaignEntityById(id: String): Campaign =
        campaignRepository.findById(id)
            .orElseThrow { NotFoundException("Campanha não encontrada.") }
}

fun Campaign.toResponse() = CampaignResponse(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    prizeValue = prizeValue,
    ticketPrice = ticketPrice,
    totalTickets = totalTickets,
    soldTickets = soldTickets,
    drawDate = drawDate,
    status = status,
    prize = prize,
    category = category,
    featured = featured
)
