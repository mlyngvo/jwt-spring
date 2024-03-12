package com.mlyngvo

import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.api.migration.JavaMigration

class JwtRefreshTokenMigration : JavaMigration {

    private val queries = arrayOf(
        "drop table if exists jwt_refresh_token",
        """
                create table jwt_refresh_token
                (
                    id bigint(9) unsigned not null auto_increment primary key,
                    token text not null,
                    email varchar(255) not null
                )
            """.trimIndent(),
        "create index I_jrt_token on jwt_refresh_token (token)",
        "create unique index I_jrt_email on jwt_refresh_token (email)",
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