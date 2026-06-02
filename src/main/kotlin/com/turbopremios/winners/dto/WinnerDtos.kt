package com.turbopremios.winners.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate

@JsonInclude(JsonInclude.Include.NON_NULL)
data class WinnerResponse(
    val id: String,
    val name: String,
    val prize: String,
    val campaignTitle: String,
    val date: LocalDate,
    val photoUrl: String?,
    val ticketNumber: String
)
