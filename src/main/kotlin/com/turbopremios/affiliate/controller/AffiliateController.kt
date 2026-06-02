package com.turbopremios.affiliate.controller

import com.turbopremios.affiliate.dto.*
import com.turbopremios.affiliate.service.AffiliateService
import com.turbopremios.auth.entity.User
import com.turbopremios.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/affiliate")
@Tag(name = "Affiliate", description = "Painel de afiliados")
class AffiliateController(private val affiliateService: AffiliateService) {

    @GetMapping("/profile")
    @Operation(summary = "Perfil do afiliado", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getProfile(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<AffiliateProfileResponse>> {
        val userId = (userDetails as User).id
        return ResponseEntity.ok(ApiResponse.success(affiliateService.getProfile(userId)))
    }

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas do afiliado", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getStats(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<AffiliateStatsResponse>> {
        val userId = (userDetails as User).id
        return ResponseEntity.ok(ApiResponse.success(affiliateService.getStats(userId)))
    }

    @GetMapping("/commissions")
    @Operation(summary = "Listar comissões", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getCommissions(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<List<CommissionResponse>>> {
        val userId = (userDetails as User).id
        return ResponseEntity.ok(ApiResponse.success(affiliateService.getCommissions(userId)))
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Solicitar saque de comissões", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun requestWithdraw(
        @Valid @RequestBody request: WithdrawRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Map<String, String>>> {
        val userId = (userDetails as User).id
        val message = affiliateService.requestWithdraw(userId, request.amount)
        return ResponseEntity.ok(ApiResponse.success(mapOf("message" to message)))
    }

    @PostMapping("/click/{code}")
    @Operation(summary = "Registrar clique em link de afiliado")
    fun trackClick(@PathVariable code: String): ResponseEntity<ApiResponse<Nothing>> {
        affiliateService.trackClick(code)
        return ResponseEntity.ok(ApiResponse.noContent())
    }
}
