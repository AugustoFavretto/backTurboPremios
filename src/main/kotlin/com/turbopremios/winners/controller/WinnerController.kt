package com.turbopremios.winners.controller

import com.turbopremios.common.ApiResponse
import com.turbopremios.winners.dto.WinnerResponse
import com.turbopremios.winners.service.WinnerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/winners")
@Tag(name = "Winners", description = "Ganhadores dos sorteios")
class WinnerController(private val winnerService: WinnerService) {

    @GetMapping
    @Operation(summary = "Listar todos os ganhadores")
    fun getAllWinners(): ResponseEntity<ApiResponse<List<WinnerResponse>>> =
        ResponseEntity.ok(ApiResponse.success(winnerService.getAllWinners()))

    @GetMapping("/recent")
    @Operation(summary = "Últimos 4 ganhadores (para homepage)")
    fun getRecentWinners(): ResponseEntity<ApiResponse<List<WinnerResponse>>> =
        ResponseEntity.ok(ApiResponse.success(winnerService.getRecentWinners()))
}
