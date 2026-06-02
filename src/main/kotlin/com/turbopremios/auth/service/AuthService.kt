package com.turbopremios.auth.service

import com.turbopremios.auth.dto.*
import com.turbopremios.auth.entity.User
import com.turbopremios.auth.repository.UserRepository
import com.turbopremios.exceptions.ConflictException
import com.turbopremios.exceptions.NotFoundException
import com.turbopremios.exceptions.UnauthorizedException
import com.turbopremios.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    private val log = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
        } catch (ex: BadCredentialsException) {
            throw UnauthorizedException("Credenciais inválidas.")
        }

        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UnauthorizedException("Credenciais inválidas.") }

        val token = jwtService.generateToken(user, mapOf("role" to user.role))
        log.info("User logged in: {}", user.email)
        return AuthResponse(token = token, user = user.toResponse())
    }

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("E-mail já cadastrado.")
        }

        val user = User(
            name = request.name,
            email = request.email,
            cpf = request.cpf,
            phone = request.phone,
            passwordHash = ""
        ).also {
            it.setPassword(passwordEncoder.encode(request.password))
        }

        val saved = userRepository.save(user)
        val token = jwtService.generateToken(saved, mapOf("role" to saved.role))
        log.info("New user registered: {}", saved.email)
        return AuthResponse(token = token, user = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun me(email: String): UserResponse {
        return userRepository.findByEmail(email)
            .orElseThrow { NotFoundException("Usuário não encontrado.") }
            .toResponse()
    }

    fun forgotPassword(request: ForgotPasswordRequest) {
        // In production: generate token, store it, send email
        // For now: log and return success (no-op to avoid user enumeration)
        log.info("Password reset requested for: {}", request.email)
    }

    @Transactional
    fun resetPassword(request: ResetPasswordRequest) {
        // In production: validate token, find user, update password
        // Token validation logic would go here
        log.info("Password reset executed with token: {}...", request.token.take(8))
    }
}

fun User.toResponse() = UserResponse(
    id = id,
    name = name,
    email = email,
    cpf = cpf,
    phone = phone,
    role = role,
    affiliateCode = affiliateCode,
    balance = balance,
    totalCommission = totalCommission,
    createdAt = createdAt
)
