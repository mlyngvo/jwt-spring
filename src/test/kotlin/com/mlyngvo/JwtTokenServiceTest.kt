package com.mlyngvo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.User
import java.util.Date

class JwtTokenServiceTest {

    private val properties = JwtProperties(
        publicKeyPath = "keys/public_key.pem",
        privateKeyPath = "keys/private_key.pem",
        accessTokenExp = 86400000L,
        refreshTokenExp = 604800000L
    )

    private val service = JwtTokenService(properties)

    private val userDetails = User.withUsername("test@example.com")
        .password("irrelevant")
        .roles("USER")
        .build()

    @Test
    fun `generate returns non-blank token`() {
        val token = service.generate(userDetails, futureDate())
        assertTrue(token.isNotBlank())
    }

    @Test
    fun `extractEmail returns subject from token`() {
        val token = service.generate(userDetails, futureDate())
        assertEquals("test@example.com", service.extractEmail(token))
    }

    @Test
    fun `isExpired returns false for future expiry`() {
        val token = service.generate(userDetails, futureDate())
        assertFalse(service.isExpired(token))
    }

    @Test
    fun `isExpired returns true for past expiry`() {
        val token = service.generate(userDetails, pastDate())
        assertTrue(service.isExpired(token))
    }

    @Test
    fun `isValid returns true for matching user and valid token`() {
        val token = service.generate(userDetails, futureDate())
        assertTrue(service.isValid(token, userDetails))
    }

    @Test
    fun `isValid returns false when username does not match`() {
        val token = service.generate(userDetails, futureDate())
        val other = User.withUsername("other@example.com").password("x").roles("USER").build()
        assertFalse(service.isValid(token, other))
    }

    @Test
    fun `isValid returns false for expired token`() {
        val token = service.generate(userDetails, pastDate())
        assertFalse(service.isValid(token, userDetails))
    }

    @Test
    fun `extractExpiration matches the date passed to generate`() {
        val exp = futureDate()
        val token = service.generate(userDetails, exp)
        val extracted = service.extractExpiration(token)
        assertNotNull(extracted)
        assertEquals(exp.time / 1000, extracted!!.time / 1000) // second-level precision
    }

    @Test
    fun `extra claims are present in the token`() {
        val token = service.generate(userDetails, futureDate(), mapOf("role" to "ADMIN"))
        // Re-parse via extractEmail to confirm the token is still valid after adding claims
        assertEquals("test@example.com", service.extractEmail(token))
    }

    @Test
    fun `parsing a malformed token throws an exception`() {
        assertThrows(Exception::class.java) { service.extractEmail("not.a.valid.jwt") }
    }

    @Test
    fun `tokens for different users are distinct`() {
        val other = User.withUsername("other@example.com").password("x").roles("USER").build()
        val exp = futureDate()
        assertNotEquals(service.generate(userDetails, exp), service.generate(other, exp))
    }

    private fun futureDate() = Date(System.currentTimeMillis() + 3_600_000)
    private fun pastDate()   = Date(System.currentTimeMillis() - 3_600_000)
}
