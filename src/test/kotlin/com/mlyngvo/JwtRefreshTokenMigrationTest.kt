package com.mlyngvo

import org.flywaydb.core.api.migration.Context
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.sql.Connection
import java.sql.PreparedStatement

@ExtendWith(MockitoExtension::class)
class JwtRefreshTokenMigrationTest {

    private val migration = JwtRefreshTokenMigration()

    @Mock private lateinit var context: Context
    @Mock private lateinit var connection: Connection
    @Mock private lateinit var statement: PreparedStatement

    @Test
    fun `getVersion returns null marking it as a repeatable migration`() {
        assertNull(migration.getVersion())
    }

    @Test
    fun `getDescription is non-blank`() {
        assertTrue(migration.getDescription().isNotBlank())
    }

    @Test
    fun `getChecksum is stable across invocations`() {
        assertEquals(migration.getChecksum(), migration.getChecksum())
    }

    @Test
    fun `canExecuteInTransaction returns true`() {
        assertTrue(migration.canExecuteInTransaction())
    }

    @Test
    fun `migrate executes all three SQL statements`() {
        `when`(context.connection).thenReturn(connection)
        `when`(connection.prepareStatement(anyString())).thenReturn(statement)

        migration.migrate(context)

        // drop table, create table, create index
        verify(connection, times(3)).prepareStatement(anyString())
        verify(statement, times(3)).execute()
    }
}
