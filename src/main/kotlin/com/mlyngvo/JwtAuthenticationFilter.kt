package com.mlyngvo

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: JwtTokenService,
): OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader.hasBearerToken()) {
            val token = authHeader!!.extractToken()
            val email = tokenService.extractEmail(token)

            if (email != null && SecurityContextHolder.getContext().authentication == null) {
                val user = userDetailsService.loadUserByUsername(email)

                if (tokenService.isValid(token, user)) {
                    updateContext(user, request)
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun String?.hasBearerToken() =
        this != null && this.startsWith("Bearer ")

    private fun String.extractToken() =
        this.substringAfter("Bearer ")

    private fun updateContext(user: UserDetails, request: HttpServletRequest) {
        val token = UsernamePasswordAuthenticationToken(user, null, user.authorities)
        token.details = WebAuthenticationDetailsSource()
            .buildDetails(request)
        SecurityContextHolder.getContext().authentication = token
    }
}