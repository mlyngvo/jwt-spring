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

    fun findEmailByToken(token: String) =
        jwtRefreshTokenRepository.findOneByToken(token)
            .map { it.email }
            .orElseThrow { EntityNotFoundException() }

    fun save(token: String, user: UserDetails) {
        jwtRefreshTokenRepository.save(JwtRefreshTokenEntity(
            token = token,
            email = user.username
        ))
    }
}