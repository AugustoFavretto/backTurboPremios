package com.turbopremios.config

import com.mercadopago.MercadoPagoConfig
import com.turbopremios.integrations.pix.MercadoPagoGateway
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class MercadoPagoConfiguration(

    @Value("\${mercadopago.access-token}")
    private val accessToken: String
) {
    private val log = LoggerFactory.getLogger(MercadoPagoGateway::class.java)

    @PostConstruct
    fun init() {
        MercadoPagoConfig.setAccessToken(accessToken)
    }
}