package com.mlyngvo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

sealed interface JwtRefreshTokenRepository : JpaRepository<JwtRefreshTokenEntity, Long> {

    fun findByToken(token: String): JwtRefreshTokenEntity?

    fun findByEmail(email: String): JwtRefreshTokenEntity?

    @Modifying
    fun deleteByToken(token: String)
}