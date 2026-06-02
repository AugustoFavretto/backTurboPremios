package com.turbopremios.security

import com.turbopremios.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(private val jwtProperties: JwtProperties) {

    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateToken(userDetails: UserDetails, extraClaims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.expiration)
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact()
    }

    fun extractUsername(token: String): String? = runCatching {
        extractAllClaims(token).subject
    }.getOrNull()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean = runCatching {
        val username = extractUsername(token)
        username == userDetails.username && !isTokenExpired(token)
    }.getOrDefault(false)

    private fun isTokenExpired(token: String): Boolean =
        extractAllClaims(token).expiration.before(Date())

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
