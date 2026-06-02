package com.turbopremios.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.jwt")
class JwtProperties {
    lateinit var secret: String
    var expiration: Long = 86400000
}

@Component
@ConfigurationProperties(prefix = "app.pix")
class PixProperties {
    var gateway: String = "mock"
    var merchantName: String = "Turbo Premios"
    var merchantCity: String = "SAO PAULO"
    var pixKey: String = "turbo@premios.com.br"
}

@Component
@ConfigurationProperties(prefix = "app.affiliate")
class AffiliateProperties {
    var commissionRate: Double = 10.0
}

@Component
@ConfigurationProperties(prefix = "app.frontend")
class FrontendProperties {
    var baseUrl: String = "https://turbopremios.com.br"
}
