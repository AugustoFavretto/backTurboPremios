package com.turbopremios.integrations.pix

import com.mercadopago.client.common.IdentificationRequest
import com.mercadopago.client.payment.PaymentClient
import com.mercadopago.client.payment.PaymentCreateRequest
import com.mercadopago.client.payment.PaymentPayerRequest
import com.mercadopago.exceptions.MPApiException
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
@ConditionalOnProperty(
    name = ["app.pix.gateway"],
    havingValue = "mercadopago"
)
class MercadoPagoGateway : PaymentGateway {

    private val log = LoggerFactory.getLogger(MercadoPagoGateway::class.java)

    override fun generatePix(
        request: PixPaymentRequest
    ): PixPaymentResult {

        try {

            log.info(
                "MercadoPagoGateway: generating PIX for purchase {} amount {}",
                request.purchaseId,
                request.amount
            )

            val client = PaymentClient()

            val identification = IdentificationRequest.builder()
                .type("CPF")
                .number(request.payerCpf)
                .build()

            val payer = PaymentPayerRequest.builder()
                .email(request.payerEmail)
                .firstName(request.payerName)
                .identification(identification)
                .build()

            val paymentRequest = PaymentCreateRequest.builder()
                .transactionAmount(request.amount)
                .description(request.description)
                .paymentMethodId("pix")
                .payer(payer)
                .build()

            val payment = client.create(paymentRequest)

            val transactionData = payment
                .pointOfInteraction
                ?.transactionData
                ?: throw RuntimeException("Erro ao gerar PIX")

            return PixPaymentResult(
                gatewayPaymentId = payment.id.toString(),
                pixCode = transactionData.qrCode,
                pixQrCodeBase64 = "data:image/png;base64,${transactionData.qrCodeBase64}",
                expiresAt = LocalDateTime.now().plusMinutes(30)
            )

        } catch (ex: MPApiException) {
            log.error("MercadoPago error body: {}", ex.apiResponse.content)
            log.error("MercadoPago error status: {}", ex.statusCode)
            log.error("MercadoPago error body: {}", ex.apiResponse.content)

            throw ex
        }
    }
    override fun confirmPayment(
        gatewayPaymentId: String
    ): Boolean {

        log.info(
            "MercadoPagoGateway: confirming payment {}",
            gatewayPaymentId
        )

        val client = PaymentClient()

        val payment = client.get(
            gatewayPaymentId.toLong()
        )

        return payment.status == "approved"
    }
}