package com.mlyngvo

import jakarta.persistence.EntityNotFoundException
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
@EntityScan
@EnableJpaRepositories
class JwtRefreshTokenStore(
    private val jwtRefreshTokenRepository: JwtRefreshTokenRepository
) {

    fun findEmailByToken(token: String): String =
        jwtRefreshTokenRepository.findByToken(token)
            ?.email
            ?: throw EntityNotFoundException()

    fun save(token: String, email: String) {
        val entity = jwtRefreshTokenRepository.findByEmail(email)
            ?.let {
                it.token = token
                it
            }
            ?: JwtRefreshTokenEntity(
                token = token,
                email = email
            )
        jwtRefreshTokenRepository.save(entity)
    }
}