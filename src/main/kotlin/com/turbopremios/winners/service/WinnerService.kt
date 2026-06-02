package com.turbopremios.winners.service

import com.turbopremios.winners.dto.WinnerResponse
import com.turbopremios.winners.entity.Winner
import com.turbopremios.winners.repository.WinnerRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WinnerService(private val winnerRepository: WinnerRepository) {

    @Transactional(readOnly = true)
    fun getAllWinners(): List<WinnerResponse> =
        winnerRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getRecentWinners(): List<WinnerResponse> =
        winnerRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 4))
            .map { it.toResponse() }
}

fun Winner.toResponse() = WinnerResponse(
    id = id,
    name = name,
    prize = prize,
    campaignTitle = campaignTitle,
    date = drawDate,
    photoUrl = photoUrl,
    ticketNumber = ticketNumber
)
