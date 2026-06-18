package com.turbopremios.integrations.pix
import com.turbopremios.auth.repository.UserRepository
import com.turbopremios.config.assas.AsaasProperties
import com.turbopremios.exceptions.NotFoundException
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
    private val properties: AsaasProperties,
    private val userRepository: UserRepository
) : PaymentGateway {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client = RestClient.builder()
        .baseUrl(properties.apiUrl)
        .defaultHeader("access_token", properties.apiKey)
        .build()

    override fun generatePix(userId: String?, request: PixPaymentRequest): PixPaymentResult {

        log.info("ASAAS URL: {}", properties.apiUrl)
        log.info("ASAAS KEY PRESENT: {}", properties.apiKey.isNotBlank())

        log.info(
            "Generating PIX via Asaas. Purchase={} Amount={}",
            request.purchaseId,
            request.amount
        )


        val customerId = if (userId != null) {

            val user = userRepository.findById(userId)
                .orElseThrow {
                    RuntimeException("Usuário não encontrado")
                }

            if (!user.asaasCustomerId.isNullOrBlank()) {

                user.asaasCustomerId!!

            } else {

                val customer = createCustomer(request)

                user.asaasCustomerId = customer.id

                userRepository.save(user)

                customer.id
            }

        } else {
            createCustomer(request).id
        }


        val payment = createPixPayment(
            customerId = customerId,
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

        try {
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
                ?: throw RuntimeException("Erro ao criar cliente no Asaas")

        } catch (e: Exception) {
            log.error("ERRO ASAAS CREATE CUSTOMER", e)
            throw e
        }
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