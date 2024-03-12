package com.mlyngvo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

sealed interface JwtRefreshTokenRepository : JpaRepository<JwtRefreshTokenEntity, String> {

    @Query("select t from JwtRefreshTokenEntity t where t.token = :token")
    fun findByToken(@Param("token") token: String): JwtRefreshTokenEntity?

    @Query("select t from JwtRefreshTokenEntity t where t.email = :email")
    fun findByEmail(@Param("email") email: String): JwtRefreshTokenEntity?
}