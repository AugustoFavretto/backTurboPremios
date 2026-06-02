package com.turbopremios.campaigns.service

import com.turbopremios.campaigns.entity.Campaign
import com.turbopremios.campaigns.repository.CampaignRepository
import com.turbopremios.exceptions.NotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

class CampaignServiceTest {

    private val campaignRepository = mockk<CampaignRepository>()
    private val campaignService = CampaignService(campaignRepository)

    @Test
    fun `getCampaigns should return paginated response`() {
        val campaigns = listOf(createCampaign("cmp_001"), createCampaign("cmp_002"))
        every { campaignRepository.findAllBy(any<Pageable>()) } returns PageImpl(campaigns)

        val result = campaignService.getCampaigns(1, 6, null)

        assertEquals(2, result.total)
        assertEquals(1, result.page)
        assertEquals(2, result.data.size)
    }

    @Test
    fun `getCampaignById should return campaign when found`() {
        val campaign = createCampaign("cmp_001")
        every { campaignRepository.findById("cmp_001") } returns Optional.of(campaign)

        val result = campaignService.getCampaignById("cmp_001")

        assertEquals("cmp_001", result.id)
        assertEquals("Test Campaign", result.title)
    }

    @Test
    fun `getCampaignById should throw NotFoundException when not found`() {
        every { campaignRepository.findById("missing") } returns Optional.empty()

        assertThrows<NotFoundException> {
            campaignService.getCampaignById("missing")
        }
    }

    @Test
    fun `getFeaturedCampaigns should return only featured active campaigns`() {
        val campaigns = listOf(createCampaign("cmp_001"), createCampaign("cmp_002"))
        every { campaignRepository.findByFeaturedTrueAndStatus("active") } returns campaigns

        val result = campaignService.getFeaturedCampaigns()

        assertEquals(2, result.size)
    }

    private fun createCampaign(id: String) = Campaign(
        id = id,
        title = "Test Campaign",
        prizeValue = BigDecimal("9999.00"),
        ticketPrice = BigDecimal("5.00"),
        totalTickets = 10000,
        drawDate = LocalDateTime.now().plusDays(30),
        prize = "iPhone 16",
        category = "Eletrônicos",
        status = "active",
        featured = true
    )
}
