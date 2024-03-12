package com.mlyngvo

import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.api.migration.JavaMigration

class JwtRefreshTokenMigration : JavaMigration {

    private val queries = arrayOf(
        "drop table if exists jwt_refresh_token",
        """
                create table jwt_refresh_token
                (
                    token text not null primary key,
                    email varchar(255) not null,
                    expired_at datetime not null
                )
            """.trimIndent(),
        "create unique index I_jrt_token on jwt_refresh_token (token)",
    )

    override fun getVersion() =
        null

    override fun getDescription() =
        "Migration class of JwtRefreshTokenMigration."

    override fun getChecksum() =
        (this::javaClass.name + queries.contentDeepHashCode()).hashCode()

    override fun canExecuteInTransaction() =
        true

    override fun migrate(context: Context) {
        for (query in queries) {
            val statement = context.connection.prepareStatement(query)
            statement.execute()
        }
    }

}