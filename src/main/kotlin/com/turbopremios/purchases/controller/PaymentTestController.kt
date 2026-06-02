package com.turbopremios.purchases.controller

import com.mercadopago.client.common.IdentificationRequest
import com.mercadopago.client.payment.PaymentClient
import com.mercadopago.client.payment.PaymentCreateRequest
import com.mercadopago.client.payment.PaymentPayerRequest
import com.mercadopago.exceptions.MPApiException
import com.turbopremios.integrations.pix.MercadoPagoGateway
import com.turbopremios.integrations.pix.PaymentGateway
import com.turbopremios.integrations.pix.PixPaymentRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.UUID

@RestController
@RequestMapping("/test")
class PaymentTestController(
    private val paymentGateway: PaymentGateway
) {
    private val log = LoggerFactory.getLogger(MercadoPagoGateway::class.java)

    @GetMapping("/pix")
    fun testPix(): Any {
        val paymentRequest = PaymentCreateRequest.builder()
            .transactionAmount(BigDecimal("1.00"))
            .description("Teste")
            .paymentMethodId("pix")
            .payer(
                PaymentPayerRequest.builder()
                    .email("teste@teste.com")
                    .build()
            )
            .build()
        val client = PaymentClient()

        try {
            val payment = client.create(paymentRequest)
        }catch (ex: MPApiException) {
            println("STATUS = ${ex.statusCode}")
            println("CONTENT = ${ex.apiResponse.content}")
            println("CAUSE = ${ex.apiResponse.content}")
            log.error("Status: {}", ex.statusCode)

            if (ex.apiResponse != null) {
                log.error("Response: {}", ex.apiResponse.content)
            }

            throw ex
        }


        return Any()
    }
}