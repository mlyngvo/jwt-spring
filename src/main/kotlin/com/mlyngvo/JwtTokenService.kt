package com.mlyngvo

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*
import java.util.function.Function


@Service
@EnableConfigurationProperties
class JwtTokenService {

    private var pubKeyResource: ClassPathResource? = null
    private var prvKeyResource: ClassPathResource? = null

    fun setPubKey(res: ClassPathResource) {
        pubKeyResource = res
    }

    fun setPrvKey(res: ClassPathResource) {
        prvKeyResource = res
    }

    fun generate(
        userDetails: UserDetails,
        expDate: Date,
        claims: Map<String, Any> = emptyMap()
    ): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(Instant.now().toEpochMilli()))
            .expiration(expDate)
            .add(claims)
            .and()
            .signWith(loadPrivateKey(), Jwts.SIG.RS512)
            .compact()

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractEmail(token)
        return userDetails.username == email && !isExpired(token)
    }

    fun isExpired(token: String): Boolean =
        getAllClaims(token)
            .expiration
            .before(Date(Instant.now().toEpochMilli()))

    fun extractEmail(token: String): String? =
        getAllClaims(token)
            .subject

    fun extractExpiration(token: String): Date? =
        getAllClaims(token)
            .expiration

    private fun <K : Key?> loadKey(stream: InputStream, parser: Function<ByteArray, K>): K {
        BufferedReader(InputStreamReader(stream)).use { reader ->
            var line: String
            val content = StringBuilder()
            while ((reader.readLine().also { line = it }) != null) {
                if (!(line.contains("BEGIN") || line.contains("END"))) {
                    content.append(line).append("\n")
                }
            }
            val encoded = Base64.getDecoder().decode(content.toString())
            return parser.apply(encoded)
        }
    }

    private fun loadPrivateKey(): PrivateKey {
        val res = prvKeyResource
            ?: throw RuntimeException("Private key resources not set")
        val factory = KeyFactory.getInstance("RSA")
        return loadKey(res.inputStream) { bytes ->
            val spec = PKCS8EncodedKeySpec(bytes)
            return@loadKey factory.generatePrivate(spec)
        }
    }

    private fun loadPublicKey(): PublicKey {
        val res = pubKeyResource
            ?: throw RuntimeException("Public key resources not set")
        val factory = KeyFactory.getInstance("RSA")
        return loadKey(res.inputStream) { bytes ->
            val spec = X509EncodedKeySpec(bytes)
            return@loadKey factory.generatePublic(spec)
        }
    }

    private fun getAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(loadPublicKey())
            .decryptWith(loadPrivateKey())
            .build()
        return parser
            .parseSignedClaims(token)
            .payload
    }
}
