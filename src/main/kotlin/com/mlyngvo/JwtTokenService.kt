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
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.util.*


@Service
@EnableConfigurationProperties
class JwtTokenService(
    jwtProperties: JwtProperties
) {

    private val pubKeyPath = jwtProperties.publicKeyPath
    private val prvKeyPath = jwtProperties.privateKeyPath

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

    private fun loadKey(stream: InputStream): ByteArray {
        val reader = BufferedReader(InputStreamReader(stream))
        val content = StringBuilder()
        while (true) {
            val line: String? = reader.readLine()
            if (line == null) break
            else if (!(line.contains("BEGIN") || line.contains("END"))) {
                content.append(line).append("\n")
            }
        }
        return Base64.getMimeDecoder().decode(content.toString())
    }

    private fun loadPrivateKey(): PrivateKey {
        val res = ClassPathResource(prvKeyPath)
        val factory = KeyFactory.getInstance("RSA")
        val spec = PKCS8EncodedKeySpec(loadKey(res.inputStream))
        return factory.generatePrivate(spec)
    }

    private fun loadPublicKey(): PublicKey {
        val res = ClassPathResource(pubKeyPath)
        val factory = KeyFactory.getInstance("RSA")
        val spec = X509EncodedKeySpec(loadKey(res.inputStream))
        return factory.generatePublic(spec)
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
