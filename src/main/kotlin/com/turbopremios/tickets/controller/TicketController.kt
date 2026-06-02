package com.turbopremios.tickets.controller

import com.turbopremios.auth.entity.User
import com.turbopremios.common.ApiResponse
import com.turbopremios.tickets.dto.TicketResponse
import com.turbopremios.tickets.service.TicketService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tickets")
@Tag(name = "Tickets", description = "Gerenciamento de bilhetes")
class TicketController(private val ticketService: TicketService) {

    @GetMapping
    @Operation(summary = "Listar bilhetes do usuário autenticado", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getUserTickets(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<TicketResponse>>> {
        val userId = (userDetails as User).id
        val tickets = ticketService.getUserTickets(userId)
        return ResponseEntity.ok(ApiResponse.success(tickets))
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Buscar bilhetes por telefone (compras anônimas)")
    fun getTicketsByPhone(@PathVariable phone: String): ResponseEntity<ApiResponse<List<TicketResponse>>> {
        val tickets = ticketService.getTicketsByPhone(phone)
        return ResponseEntity.ok(ApiResponse.success(tickets))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes de um bilhete", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getTicket(
        @PathVariable id: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<TicketResponse>> {
        val userId = (userDetails as User).id
        val ticket = ticketService.getTicketById(id, userId)
        return ResponseEntity.ok(ApiResponse.success(ticket))
    }
}
