package com.turbopremios.tickets.service

import com.turbopremios.tickets.dto.TicketResponse
import com.turbopremios.tickets.entity.Ticket

fun Ticket.toResponse(campaignTitle: String? = null) = TicketResponse(
    id = id,
    number = number,
    campaignId = campaignId,
    campaignTitle = campaignTitle,
    userId = userId,
    userPhone = userPhone,
    userEmail = userEmail,
    purchasedAt = purchasedAt,
    status = status,
    price = price
)
