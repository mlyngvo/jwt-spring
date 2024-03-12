package com.mlyngvo

import jakarta.persistence.EntityNotFoundException
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Instant

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

    fun save(token: String, email: String, expiredAt: Instant) {
        jwtRefreshTokenRepository.save(JwtRefreshTokenEntity(
            token = token,
            email = email,
            expiredAt = expiredAt
        ))
    }

    fun delete(token: String) {
        jwtRefreshTokenRepository.deleteById(token)
    }
}