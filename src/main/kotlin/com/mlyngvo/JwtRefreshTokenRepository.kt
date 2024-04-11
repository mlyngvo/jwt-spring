package com.mlyngvo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

sealed interface JwtRefreshTokenRepository : JpaRepository<JwtRefreshTokenEntity, Long> {

    fun findByToken(token: String): JwtRefreshTokenEntity?

    fun findByEmail(email: String): JwtRefreshTokenEntity?

    @Modifying(flushAutomatically = true)
    fun deleteByToken(token: String)
}