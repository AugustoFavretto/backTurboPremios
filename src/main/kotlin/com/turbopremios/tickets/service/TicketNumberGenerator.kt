package com.turbopremios.tickets.service

import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class TicketNumberGenerator {

    private val secureRandom = SecureRandom()

    fun generateUniqueNumbers(quantity: Int, existingNumbers: Set<String>): List<String> {
        val available = (0..99999)
            .map { it.toString().padStart(5, '0') }
            .filter { it !in existingNumbers }
            .shuffled(secureRandom)

        require(available.size >= quantity) {
            "Bilhetes insuficientes disponíveis na campanha. Disponíveis: ${available.size}, Solicitados: $quantity"
        }

        return available.take(quantity)
    }
}
