package com.mlyngvo

import org.flywaydb.core.api.migration.Context
import org.flywaydb.core.api.migration.JavaMigration

class JwtRefreshTokenMigration(
    private val queries: Array<String>
) : JavaMigration {

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