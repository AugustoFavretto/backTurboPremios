package com.turbopremios.auth.controller

import com.turbopremios.auth.dto.*
import com.turbopremios.auth.service.AuthService
import com.turbopremios.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação e gerenciamento de conta")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    @Operation(summary = "Login do usuário", description = "Autentica e retorna JWT + dados do usuário")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val result = authService.login(request)
        return ResponseEntity.ok(ApiResponse.success(result))
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastro de novo usuário")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val result = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result))
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário autenticado", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun me(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ApiResponse<UserResponse>> {
        val user = authService.me(userDetails.username)
        return ResponseEntity.ok(ApiResponse.success(user))
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", security = [SecurityRequirement(name = "Bearer Authentication")])
    fun logout(): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity.ok(ApiResponse.noContent("Logout realizado."))

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar recuperação de senha")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest): ResponseEntity<ApiResponse<Nothing>> {
        authService.forgotPassword(request)
        return ResponseEntity.ok(ApiResponse.noContent("E-mail de recuperação enviado."))
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Redefinir senha com token")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseEntity<ApiResponse<Nothing>> {
        authService.resetPassword(request)
        return ResponseEntity.ok(ApiResponse.noContent("Senha redefinida com sucesso."))
    }
}
