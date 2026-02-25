package com.mlyngvo

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService

@ExtendWith(MockitoExtension::class)
class JwtAuthenticationFilterTest {

    @Mock private lateinit var userDetailsService: UserDetailsService
    @Mock private lateinit var tokenService: JwtTokenService
    @Mock private lateinit var filterChain: FilterChain

    private lateinit var filter: JwtAuthenticationFilter

    private val userDetails = User.withUsername("user@example.com")
        .password("x").roles("USER").build()

    @BeforeEach
    fun setUp() {
        filter = JwtAuthenticationFilter(userDetailsService, tokenService)
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `no authorization header passes through without setting auth`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `non-bearer authorization header passes through without setting auth`() {
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Basic dXNlcjpwYXNz")
        }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `valid bearer token sets authentication in security context`() {
        `when`(tokenService.extractEmail("tok")).thenReturn("user@example.com")
        `when`(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails)
        `when`(tokenService.isValid("tok", userDetails)).thenReturn(true)

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer tok") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        assertNotNull(SecurityContextHolder.getContext().authentication)
        assertEquals("user@example.com",
            (SecurityContextHolder.getContext().authentication.principal as User).username)
    }

    @Test
    fun `token failing isValid passes through without setting auth`() {
        `when`(tokenService.extractEmail("tok")).thenReturn("user@example.com")
        `when`(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails)
        `when`(tokenService.isValid("tok", userDetails)).thenReturn(false)

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer tok") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `null subject in token passes through without setting auth`() {
        `when`(tokenService.extractEmail("tok")).thenReturn(null)

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer tok") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verify(userDetailsService, never()).loadUserByUsername(anyString())
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    fun `exception during token processing returns 401 and stops filter chain`() {
        `when`(tokenService.extractEmail(anyString())).thenThrow(RuntimeException("bad token"))

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer bad") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.status)
        verify(filterChain, never()).doFilter(any(), any())
    }

    @Test
    fun `already authenticated context skips user loading`() {
        val existing = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        SecurityContextHolder.getContext().authentication = existing

        `when`(tokenService.extractEmail("tok")).thenReturn("user@example.com")

        val request = MockHttpServletRequest().apply { addHeader("Authorization", "Bearer tok") }
        val response = MockHttpServletResponse()

        filter.doFilter(request, response, filterChain)

        verify(userDetailsService, never()).loadUserByUsername(anyString())
        verify(filterChain).doFilter(request, response)
    }
}
