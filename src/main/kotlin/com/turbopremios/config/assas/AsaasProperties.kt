package com.turbopremios.config.assas

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "asaas")
data class AsaasProperties(
    var apiUrl: String = "",
    var apiKey: String = "",
    var webhookToken: String = ""
)