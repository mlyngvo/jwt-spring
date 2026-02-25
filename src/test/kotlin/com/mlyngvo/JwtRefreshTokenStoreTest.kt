package com.mlyngvo

import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class JwtRefreshTokenStoreTest {

    @Mock private lateinit var repository: JwtRefreshTokenRepository

    private lateinit var store: JwtRefreshTokenStore

    @BeforeEach
    fun setUp() {
        store = JwtRefreshTokenStore(repository)
    }

    @Test
    fun `findEmailByToken returns email when entity exists`() {
        val entity = JwtRefreshTokenEntity(email = "user@example.com", token = "tok", expiredAt = Instant.now())
        `when`(repository.findByToken("tok")).thenReturn(entity)

        assertEquals("user@example.com", store.findEmailByToken("tok"))
    }

    @Test
    fun `findEmailByToken throws EntityNotFoundException when token is unknown`() {
        `when`(repository.findByToken("missing")).thenReturn(null)

        assertThrows<EntityNotFoundException> { store.findEmailByToken("missing") }
    }

    @Test
    fun `save persists entity with correct fields`() {
        val exp = Instant.now().plusSeconds(3600)

        store.save("tok", "user@example.com", exp)

        val captor = ArgumentCaptor.forClass(JwtRefreshTokenEntity::class.java)
        verify(repository).save(captor.capture())
        val saved = captor.value
        assertEquals("tok", saved.token)
        assertEquals("user@example.com", saved.email)
        assertEquals(exp, saved.expiredAt)
    }

    @Test
    fun `delete delegates to repository deleteByToken`() {
        store.delete("tok")

        verify(repository).deleteByToken("tok")
    }

    @Test
    fun `save with different tokens are stored independently`() {
        val exp = Instant.now().plusSeconds(3600)
        store.save("tok1", "a@example.com", exp)
        store.save("tok2", "b@example.com", exp)

        verify(repository, times(2)).save(any(JwtRefreshTokenEntity::class.java))
    }
}
