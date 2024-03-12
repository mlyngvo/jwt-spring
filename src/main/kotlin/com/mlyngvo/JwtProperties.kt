package com.mlyngvo

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties (
    val key: String,
    val accessTokenExp: Long,
    val refreshTokenExp: Long
)