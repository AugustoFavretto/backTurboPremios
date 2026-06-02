package com.turbopremios.tickets.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TicketNumberGeneratorTest {

    private val generator = TicketNumberGenerator()

    @Test
    fun `should generate correct quantity of unique numbers`() {
        val numbers = generator.generateUniqueNumbers(10, emptySet())
        assertEquals(10, numbers.size)
        assertEquals(10, numbers.toSet().size)
    }

    @Test
    fun `should generate 5-digit padded numbers`() {
        val numbers = generator.generateUniqueNumbers(5, emptySet())
        numbers.forEach { number ->
            assertEquals(5, number.length, "Number $number should be 5 digits")
            assertTrue(number.matches(Regex("\\d{5}")), "Number $number should be all digits")
        }
    }

    @Test
    fun `should not generate numbers that already exist`() {
        val existing = setOf("00001", "00002", "00003")
        val numbers = generator.generateUniqueNumbers(5, existing)
        numbers.forEach { number ->
            assertFalse(number in existing, "Generated number $number should not be in existing set")
        }
    }

    @Test
    fun `should throw when not enough numbers available`() {
        val existing = (0..99998).map { it.toString().padStart(5, '0') }.toSet()
        assertThrows<IllegalArgumentException> {
            generator.generateUniqueNumbers(5, existing)
        }
    }
}
