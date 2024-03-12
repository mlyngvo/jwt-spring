package com.mlyngvo

import jakarta.persistence.*

@Entity
@Table(name = "jwt_refresh_token")
class JwtRefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    var email: String,

    @Column(columnDefinition = "TEXT") var token: String
) {
    companion object {
        val MIGRATION = JwtRefreshTokenMigration(arrayOf(
            "drop table if exists jwt_refresh_token",
            """
                create table jwt_refresh_token
                (
                    id bigint(9) unsigned not null auto_increment primary key,
                    token text not null,
                    email varchar(255) not null
                )
            """.trimIndent(),
            "create unique index I_jrt_token on jwt_refresh_token (token)"
        ))
    }
}