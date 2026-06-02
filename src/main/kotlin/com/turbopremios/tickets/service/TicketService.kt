package com.turbopremios.tickets.service

import com.turbopremios.campaigns.repository.CampaignRepository
import com.turbopremios.exceptions.ForbiddenException
import com.turbopremios.exceptions.NotFoundException
import com.turbopremios.tickets.dto.TicketResponse
import com.turbopremios.tickets.repository.TicketRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val campaignRepository: CampaignRepository
) {

    @Transactional(readOnly = true)
    fun getUserTickets(userId: String): List<TicketResponse> {
        val tickets = ticketRepository.findByUserId(userId)
        val campaignTitles = getCampaignTitles(tickets.map { it.campaignId }.distinct())
        return tickets.map { it.toResponse(campaignTitles[it.campaignId]) }
    }

    @Transactional(readOnly = true)
    fun getTicketById(id: String, userId: String): TicketResponse {
        val ticket = ticketRepository.findById(id)
            .orElseThrow { NotFoundException("Bilhete não encontrado.") }

        if (ticket.userId != null && ticket.userId != userId) {
            throw ForbiddenException("Acesso negado.")
        }

        val campaign = campaignRepository.findById(ticket.campaignId).orElse(null)
        return ticket.toResponse(campaign?.title)
    }

    @Transactional(readOnly = true)
    fun getTicketsByPhone(phone: String): List<TicketResponse> {
        val tickets = ticketRepository.findByUserPhone(phone)
        val campaignTitles = getCampaignTitles(tickets.map { it.campaignId }.distinct())
        return tickets.map { it.toResponse(campaignTitles[it.campaignId]) }
    }

    private fun getCampaignTitles(campaignIds: List<String>): Map<String, String> {
        if (campaignIds.isEmpty()) return emptyMap()
        return campaignRepository.findAllById(campaignIds)
            .associate { it.id to it.title }
    }
}
