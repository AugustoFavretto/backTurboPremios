package com.turbopremios.dashboard.controller

import com.turbopremios.auth.entity.User
import com.turbopremios.common.ApiResponse
import com.turbopremios.dashboard.dto.ActivityResponse
import com.turbopremios.dashboard.dto.DashboardStatsResponse
import com.turbopremios.dashboard.service.DashboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Estatísticas e atividades do usuário")
class DashboardController(private val dashboardService: DashboardService) {

    @GetMapping("/stats")
    @Operation(summary = "Estatísticas do usuário", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getStats(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<DashboardStatsResponse>> {
        val userId = (userDetails as User).id
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getStats(userId)))
    }

    @GetMapping("/activity")
    @Operation(summary = "Atividades recentes", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getActivity(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<List<ActivityResponse>>> {
        val userId = (userDetails as User).id
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getActivity(userId)))
    }
}
