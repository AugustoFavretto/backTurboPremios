package com.turbopremios.purchases.controller

import com.turbopremios.common.ApiResponse
import com.turbopremios.purchases.service.PurchaseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Webhooks de confirmação de pagamento")
class PaymentWebhookController(private val purchaseService: PurchaseService) {

    private val log = LoggerFactory.getLogger(PaymentWebhookController::class.java)

    @PostMapping("/webhook/pix")
    @Operation(summary = "Webhook de confirmação PIX (gateway externo)")
    fun pixWebhook(@RequestBody payload: Map<String, Any>): ResponseEntity<ApiResponse<Nothing>> {
        log.info("PIX webhook received: {}", payload)
        println(payload)
        return ResponseEntity.ok().build()
        val purchaseId = payload["purchaseId"]?.toString() ?: payload["externalId"]?.toString()
        if (purchaseId != null) {
            purchaseService.confirmPayment(purchaseId)
        }
        return ResponseEntity.ok(ApiResponse.noContent("OK"))
    }

    @PostMapping("/webhook/confirm/{purchaseId}")
    @Operation(summary = "Confirmar pagamento manualmente (dev/test)")
    fun confirmManually(@PathVariable purchaseId: String): ResponseEntity<ApiResponse<Nothing>> {
        log.info("Manual payment confirmation for purchase: {}", purchaseId)
        purchaseService.confirmPayment(purchaseId)
        return ResponseEntity.ok(ApiResponse.noContent("Pagamento confirmado."))
    }
}
