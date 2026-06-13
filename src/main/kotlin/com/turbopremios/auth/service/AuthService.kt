package com.turbopremios.auth.service

import com.turbopremios.auth.dto.*
import com.turbopremios.auth.entity.PasswordResetToken
import com.turbopremios.auth.entity.User
import com.turbopremios.auth.repository.PasswordResetTokenRepository
import com.turbopremios.auth.repository.UserRepository
import com.turbopremios.email.service.EmailService
import com.turbopremios.exceptions.BadRequestException
import com.turbopremios.exceptions.ConflictException
import com.turbopremios.exceptions.NotFoundException
import com.turbopremios.exceptions.UnauthorizedException
import com.turbopremios.extensions.onlyDigits
import com.turbopremios.security.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val emailService: EmailService
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
        if (userRepository.existsByCpf(request.cpf)) {
            throw ConflictException("CPF já cadastrado.")
        }

        val user = User(
            name = request.name,
            email = request.email,
            cpf = request.cpf.onlyDigits(),
            phone = request.phone.onlyDigits(),
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

    @Transactional
    fun updateProfile(
        currentEmail: String,
        request: UpdateProfileRequest,
    ): UserResponse {

        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UnauthorizedException("Credenciais inválidas.") }


        if (
            request.email != currentEmail &&
            userRepository.existsByEmail(request.email)
        ) {
            throw BadRequestException("Este e-mail já está em uso")
        }

        if (
            request.phone != user.phone &&
            userRepository.existsByPhone(request.phone)
        ) {
            throw BadRequestException("Este telefone já está em uso")
        }

        user.name = request.name
        user.email = request.email
        user.phone = request.phone

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun forgotPassword(request: ForgotPasswordRequest) {
        log.info("Password reset requested for: {}", request.email)
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UnauthorizedException(
                    "Se existir uma conta associada a esse e-mail,\n" +
                            "você receberá instruções.") }

        passwordResetTokenRepository.deleteByUser(user)

        val token = PasswordResetToken(
            token = generateToken(),
            user = user,
            expiresAt = LocalDateTime.now().plusHours(1)
        )

        passwordResetTokenRepository.save(token)

        emailService.sendPasswordResetEmail(
            user.email,
            token.token
        )
    }

    @Transactional
    fun resetPassword(request: ResetPasswordRequest) {
        // In production: validate token, find user, update password
        // Token validation logic would go here
        log.info("Password reset executed with token: {}...", request.token.take(8))

        val resetToken =
            passwordResetTokenRepository.findByToken(request.token)
                ?: throw BadRequestException("Token inválido")

        if (resetToken.used) {
            throw BadRequestException("Token já utilizado")
        }

        if (resetToken.expiresAt.isBefore(LocalDateTime.now())) {
            throw BadRequestException("Token expirado")
        }

        val user = resetToken.user

        user.password =
            passwordEncoder.encode(request.password)

        userRepository.save(user)

        resetToken.used = true

        passwordResetTokenRepository.save(resetToken)
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

private fun generateToken(): String {
    return UUID.randomUUID().toString()
}
