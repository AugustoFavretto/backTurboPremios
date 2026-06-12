package com.turbopremios.purchases.controller

import com.turbopremios.common.ApiResponse
import com.turbopremios.config.assas.AsaasProperties
import com.turbopremios.purchases.service.PurchaseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Webhooks de confirmação de pagamento")
class PaymentWebhookController(private val purchaseService: PurchaseService, private val asaasProperties: AsaasProperties) {

    private val log = LoggerFactory.getLogger(PaymentWebhookController::class.java)

    @PostMapping("/webhook/pix")
    @Operation(summary = "Webhook de confirmação PIX (gateway externo)")
    fun pixWebhook(
        @RequestHeader("asaas-access-token", required = false)
        webhookToken: String?,
        @RequestBody payload: Map<String, Any>): ResponseEntity<ApiResponse<Nothing>> {
        log.info("PIX webhook received: {}", payload)

        if (webhookToken != asaasProperties.webhookToken) {

            log.warn(
                "Invalid webhook token received: {}",
                webhookToken
            )

            return ResponseEntity.ok().build()
        }

        val event = payload["event"]?.toString()

        if (event != "PAYMENT_RECEIVED") {
            return ResponseEntity.ok().build()
        }

        val payment =
            payload["payment"] as? Map<*, *>
                ?: return ResponseEntity.ok().build()

        val paymentId =
            payment["id"]?.toString()
                ?: return ResponseEntity.ok().build()

        purchaseService.confirmPayment(paymentId)

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
