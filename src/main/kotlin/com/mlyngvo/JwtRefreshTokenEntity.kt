package com.mlyngvo

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "jwt_refresh_token")
class JwtRefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    var email: String,

    @Column(columnDefinition = "TEXT") var token: String,

    var expiredAt: Instant,
) {
    companion object {
        val MIGRATION = JwtRefreshTokenMigration()
    }
}