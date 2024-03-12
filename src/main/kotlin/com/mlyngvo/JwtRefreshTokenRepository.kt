package com.mlyngvo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

sealed interface JwtRefreshTokenRepository : JpaRepository<JwtRefreshTokenEntity, Long> {

    @Query("select t from JwtRefreshTokenEntity t where t.token = :token")
    fun findOneByToken(@Param("token") token: String): Optional<JwtRefreshTokenEntity>
}