package com.turbopremios.auth.dto

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDateTime

data class LoginRequest(
    @field:NotBlank(message = "E-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String,

    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    val password: String
)

data class RegisterRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    val name: String,

    @field:NotBlank(message = "E-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String,

    @field:NotBlank(message = "Telefone é obrigatório")
    @field:Size(min = 10, message = "Telefone deve ter no mínimo 10 dígitos")
    val phone: String,

    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    val password: String,

    @field:NotBlank(message = "CPF é obrigatório")
    @field:Email(message = "CPF inválido")
    val cpf: String,

    val affiliateCode: String? = null
)

data class ForgotPasswordRequest(
    @field:NotBlank(message = "E-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "Token é obrigatório")
    val token: String,

    @field:NotBlank(message = "Senha é obrigatória")
    @field:Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    val password: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val cpf: String,
    val phone: String?,
    val role: String,
    val affiliateCode: String?,
    val balance: BigDecimal,
    val totalCommission: BigDecimal,
    val createdAt: LocalDateTime
)

data class AuthResponse(
    val token: String,
    val user: UserResponse
)
