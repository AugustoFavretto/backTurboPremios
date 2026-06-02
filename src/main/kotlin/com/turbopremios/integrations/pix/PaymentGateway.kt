package com.turbopremios.integrations.pix

import java.math.BigDecimal

data class PixPaymentRequest(
    val purchaseId: String,
    val amount: BigDecimal,
    val payerName: String,
    val payerEmail: String?,
    val payerCpf: String,
    val description: String
)

data class PixPaymentResult(
    val pixCode: String,
    val pixQrCodeBase64: String,
    val expiresAt: java.time.LocalDateTime,
    val gatewayPaymentId: String
)

interface PaymentGateway {
    fun generatePix(request: PixPaymentRequest): PixPaymentResult
    fun confirmPayment(gatewayPaymentId: String): Boolean
}
