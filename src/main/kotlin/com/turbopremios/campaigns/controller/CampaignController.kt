package com.turbopremios.campaigns.controller

import com.turbopremios.campaigns.dto.CampaignResponse
import com.turbopremios.campaigns.service.CampaignService
import com.turbopremios.common.ApiResponse
import com.turbopremios.common.PaginatedResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/campaigns")
@Tag(name = "Campaigns", description = "Gerenciamento de campanhas de rifas")
class CampaignController(private val campaignService: CampaignService) {

    @GetMapping
    @Operation(summary = "Listar campanhas", description = "Retorna lista paginada de campanhas com filtro opcional por status")
    fun getCampaigns(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") perPage: Int,
        @RequestParam(required = false) status: String?
    ): ResponseEntity<PaginatedResponse<CampaignResponse>> {
        val result = campaignService.getCampaigns(page, perPage, status)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/featured")
    @Operation(summary = "Campanhas em destaque", description = "Retorna campanhas ativas marcadas como destaque")
    fun getFeaturedCampaigns(): ResponseEntity<ApiResponse<List<CampaignResponse>>> {
        val campaigns = campaignService.getFeaturedCampaigns()
        return ResponseEntity.ok(ApiResponse.success(campaigns))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes de campanha")
    fun getCampaign(@PathVariable id: String): ResponseEntity<ApiResponse<CampaignResponse>> {
        val campaign = campaignService.getCampaignById(id)
        return ResponseEntity.ok(ApiResponse.success(campaign))
    }
}
