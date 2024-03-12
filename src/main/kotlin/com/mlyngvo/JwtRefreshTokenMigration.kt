package com.mlyngvo

import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.api.migration.JavaMigration

class JwtRefreshTokenMigration : JavaMigration {

    override fun getVersion() =
        null

    override fun getDescription() =
        "Migration class of JwtRefreshTokenMigration."

    override fun getChecksum() =
        hashCode()

    override fun canExecuteInTransaction() =
        true

    override fun migrate(context: Context) {
        val query = "drop table if exists jwt_refresh_token;\n" +
                "create table jwt_refresh_token\n" +
                "(\n" +
                "    id bigint(9) unsigned not null primary key,\n" +
                "    token text not null,\n" +
                "    email varchar(255) not null\n" +
                ");\n" +
                "create unique index I_jrt_token on jwt_refresh_token (token);"
        val statement = context.connection.prepareStatement(query)
        statement.execute()
    }

}