package com.mlyngvo

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "jwt_refresh_token")
class JwtRefreshTokenEntity(
    @Id
    @Column(columnDefinition = "TEXT")
    var token: String,

    var email: String,

    var expiredAt: Instant,
) {
    companion object {
        val MIGRATION = JwtRefreshTokenMigration()
    }
}