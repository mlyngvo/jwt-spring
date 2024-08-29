package com.mlyngvo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties (
    val accessTokenExp: Long,
    val refreshTokenExp: Long,
    val publicKeyPath: String,
    val privateKeyPath: String,
)
