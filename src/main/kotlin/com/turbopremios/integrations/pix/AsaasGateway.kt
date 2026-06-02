package com.turbopremios.integrations.pix
import com.turbopremios.config.assas.AsaasProperties
import com.turbopremios.purchases.dto.AsaasCustomerRequest
import com.turbopremios.purchases.dto.AsaasCustomerResponse
import com.turbopremios.purchases.dto.AsaasPaymentDetailsResponse
import com.turbopremios.purchases.dto.AsaasPaymentRequest
import com.turbopremios.purchases.dto.AsaasPaymentResponse
import com.turbopremios.purchases.dto.AsaasPixQrCodeResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDate
import java.time.LocalDateTime

@Component
@ConditionalOnProperty(
    name = ["app.pix.gateway"],
    havingValue = "asaas"
)
class AsaasGateway(
    private val properties: AsaasProperties
) : PaymentGateway {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client = RestClient.builder()
        .baseUrl(properties.apiUrl)
        .defaultHeader("access_token", properties.apiKey)
        .build()

    override fun generatePix(
        request: PixPaymentRequest
    ): PixPaymentResult {

        log.info(
            "Generating PIX via Asaas. Purchase={} Amount={}",
            request.purchaseId,
            request.amount
        )

        val customer = createCustomer(request)

        val payment = createPixPayment(
            customerId = customer.id,
            request = request
        )

        val qrCode = getPixQrCode(
            payment.id
        )

        return PixPaymentResult(
            gatewayPaymentId = payment.id,
            pixCode = qrCode.payload,
            pixQrCodeBase64 = qrCode.encodedImage,
            expiresAt = LocalDateTime.now().plusDays(1)
        )
    }

    override fun confirmPayment(
        gatewayPaymentId: String
    ): Boolean {

        val payment = client.get()
            .uri("/payments/$gatewayPaymentId")
            .retrieve()
            .body(AsaasPaymentDetailsResponse::class.java)
            ?: return false

        return payment.status == "RECEIVED" ||
                payment.status == "CONFIRMED"
    }

    private fun createCustomer(
        request: PixPaymentRequest
    ): AsaasCustomerResponse {

        return client.post()
            .uri("/customers")
            .body(
                AsaasCustomerRequest(
                    name = request.payerName,
                    cpfCnpj = request.payerCpf,
                    email = request.payerEmail
                )
            )
            .retrieve()
            .body(AsaasCustomerResponse::class.java)
            ?: throw RuntimeException(
                "Erro ao criar cliente no Asaas"
            )
    }

    private fun createPixPayment(
        customerId: String,
        request: PixPaymentRequest
    ): AsaasPaymentResponse {

        return client.post()
            .uri("/payments")
            .body(
                AsaasPaymentRequest(
                    customer = customerId,
                    billingType = "PIX",
                    value = request.amount,
                    dueDate = LocalDate.now().plusDays(1).toString(),
                    description = request.description
                )
            )
            .retrieve()
            .body(AsaasPaymentResponse::class.java)
            ?: throw RuntimeException(
                "Erro ao criar cobrança PIX"
            )
    }

    private fun getPixQrCode(
        paymentId: String
    ): AsaasPixQrCodeResponse {

        return client.get()
            .uri("/payments/$paymentId/pixQrCode")
            .retrieve()
            .body(AsaasPixQrCodeResponse::class.java)
            ?: throw RuntimeException(
                "Erro ao obter QRCode PIX"
            )
    }
}