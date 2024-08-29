package com.mlyngvo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties (
    val publicKeyPath: String,
    val privateKeyPath: String,
    val accessTokenExp: Long,
    val refreshTokenExp: Long,
)
