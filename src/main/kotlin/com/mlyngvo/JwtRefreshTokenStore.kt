package com.mlyngvo

import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class JwtRefreshTokenStore(
    private val jwtRefreshTokenRepository: JwtRefreshTokenRepository
) {

    fun findEmailByToken(token: String) =
        jwtRefreshTokenRepository.findOneByToken(token)
            .map { it.email }
            ?:throw EntityNotFoundException()

    fun save(token: String, user: UserDetails) {
        jwtRefreshTokenRepository.save(JwtRefreshTokenEntity(
            token = token,
            email = user.username
        ))
    }
}