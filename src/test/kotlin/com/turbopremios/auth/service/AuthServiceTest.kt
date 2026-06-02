package com.turbopremios.auth.service

import com.turbopremios.auth.dto.LoginRequest
import com.turbopremios.auth.dto.RegisterRequest
import com.turbopremios.auth.entity.User
import com.turbopremios.auth.repository.UserRepository
import com.turbopremios.exceptions.ConflictException
import com.turbopremios.exceptions.UnauthorizedException
import com.turbopremios.security.JwtService
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

class AuthServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtService = mockk<JwtService>()
    private val authenticationManager = mockk<AuthenticationManager>()

    private val authService = AuthService(userRepository, passwordEncoder, jwtService, authenticationManager)

    @Test
    fun `login should return auth response when credentials are valid`() {
        val user = createUser()
        every { authenticationManager.authenticate(any()) } returns mockk()
        every { userRepository.findByEmail("test@test.com") } returns Optional.of(user)
        every { jwtService.generateToken(user, any()) } returns "jwt-token"

        val result = authService.login(LoginRequest("test@test.com", "password123"))

        assertEquals("jwt-token", result.token)
        assertEquals("test@test.com", result.user.email)
    }

    @Test
    fun `login should throw UnauthorizedException when credentials are invalid`() {
        every { authenticationManager.authenticate(any()) } throws BadCredentialsException("bad")

        assertThrows<UnauthorizedException> {
            authService.login(LoginRequest("test@test.com", "wrongpass"))
        }
    }

    @Test
    fun `register should create user and return auth response`() {
        val request = RegisterRequest(
            name = "Test User",
            email = "new@test.com",
            phone = "11999999999",
            password = "password123",
            cpf = "1234"
        )
        every { userRepository.existsByEmail("new@test.com") } returns false
        every { passwordEncoder.encode("password123") } returns "hashed-password"
        every { userRepository.save(any()) } answers { firstArg() }
        every { jwtService.generateToken(any(), any()) } returns "new-token"

        val result = authService.register(request)

        assertEquals("new-token", result.token)
        assertEquals("new@test.com", result.user.email)
        assertEquals("1234", result.user.cpf)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `register should throw ConflictException when email already exists`() {
        every { userRepository.existsByEmail("existing@test.com") } returns true

        assertThrows<ConflictException> {
            authService.register(
                RegisterRequest("Name", "existing@test.com", "11999999999", "password123","1234")
            )
        }
    }

    private fun createUser() = User(
        id = "usr_001",
        name = "Test User",
        email = "test@test.com",
        phone = "11999999999",
        passwordHash = "hashed",
        cpf = "1234"
    )
}
