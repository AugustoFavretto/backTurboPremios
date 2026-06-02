package com.turbopremios.purchases.controller

import com.turbopremios.common.ApiResponse
import com.turbopremios.purchases.dto.CreatePurchaseRequest
import com.turbopremios.purchases.dto.PurchaseResponse
import com.turbopremios.purchases.service.PurchaseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/purchases")
@Tag(name = "Purchases", description = "Gerenciamento de compras e pagamentos PIX")
class PurchaseController(private val purchaseService: PurchaseService) {

    @PostMapping
    @Operation(summary = "Criar compra e gerar PIX", description = "Cria uma compra de bilhetes e retorna código PIX para pagamento")
    fun createPurchase(
        @Valid @RequestBody request: CreatePurchaseRequest,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<ApiResponse<PurchaseResponse>> {
        val userId = userDetails?.username?.let { email ->
            // resolve userId from email - handled via custom principal
            (userDetails as? com.turbopremios.auth.entity.User)?.id
        }
        val result = purchaseService.createPurchase(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result))
    }

    @GetMapping
    @Operation(summary = "Listar compras do usuário autenticado", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getUserPurchases(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<PurchaseResponse>>> {
        val userId = (userDetails as com.turbopremios.auth.entity.User).id
        val purchases = purchaseService.getUserPurchases(userId)
        return ResponseEntity.ok(ApiResponse.success(purchases))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes de uma compra", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun getPurchase(
        @PathVariable id: String,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<PurchaseResponse>> {
        val userId = (userDetails as com.turbopremios.auth.entity.User).id
        val purchase = purchaseService.getPurchaseById(id, userId)
        return ResponseEntity.ok(ApiResponse.success(purchase))
    }
}
