package com.turbopremios.purchases.dto

import java.math.BigDecimal

data class AsaasCustomerRequest(
    val name: String,
    val cpfCnpj: String,
    val email: String?
)

data class AsaasCustomerResponse(
    val id: String
)

data class AsaasPaymentRequest(
    val customer: String,
    val billingType: String,
    val value: BigDecimal,
    val dueDate: String,
    val description: String
)

data class AsaasPaymentResponse(
    val id: String
)

data class AsaasPixQrCodeResponse(
    val encodedImage: String,
    val payload: String
)

data class AsaasPaymentDetailsResponse(
    val status: String
)