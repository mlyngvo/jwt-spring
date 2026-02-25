# jwt-spring

A Spring Boot auto-configuration library that adds JWT-based authentication to any Spring Boot application.

[![CI](https://github.com/mlyngvo/jwt-spring/actions/workflows/ci.yml/badge.svg)](https://github.com/mlyngvo/jwt-spring/actions/workflows/ci.yml)

## Features

- RS512-signed JWT access tokens via JJWT
- Automatic `Bearer` token extraction and validation through a servlet filter
- Refresh token persistence backed by JPA + Flyway (schema migration included)
- Zero-config Spring Boot auto-configuration — just add the dependency and supply your keys

## Requirements

- Java 17+
- Spring Boot 3.x
- A relational database (MySQL / MariaDB in production, H2 supported for tests)

## Installation

The library is distributed via [JitPack](https://jitpack.io).

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.mlyngvo:jwt-spring:<version>")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.mlyngvo</groupId>
    <artifactId>jwt-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

## Configuration

Add the following to your `application.yml`:

```yaml
jwt:
  public-key-path: "keys/public_key.pem"   # classpath-relative path
  private-key-path: "keys/private_key.pem"  # classpath-relative path
  access-token-exp: 86400000                # milliseconds (1 day)
  refresh-token-exp: 604800000              # milliseconds (7 days)
```

### Generating RSA keys

```bash
# Generate a 4096-bit private key in PKCS#8 format
openssl genrsa 4096 | openssl pkcs8 -topk8 -nocrypt -out src/main/resources/keys/private_key.pem

# Extract the public key
openssl rsa -in src/main/resources/keys/private_key.pem -pubout -out src/main/resources/keys/public_key.pem
```

## Usage

### Issuing tokens

Inject `JwtTokenService` and call `generate`:

```kotlin
@RestController
class AuthController(
    private val tokenService: JwtTokenService,
    private val jwtProperties: JwtProperties,
) {

    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginRequest): TokenResponse {
        // authenticate user ...
        val expiry = Date(System.currentTimeMillis() + jwtProperties.accessTokenExp)
        val accessToken = tokenService.generate(userDetails, expiry)
        return TokenResponse(accessToken)
    }
}
```

### Storing and rotating refresh tokens

Inject `JwtRefreshTokenStore` to persist and look up refresh tokens:

```kotlin
// Save a new refresh token
val refreshExpiry = Instant.now().plusMillis(jwtProperties.refreshTokenExp)
refreshTokenStore.save(refreshToken, user.email, refreshExpiry)

// Resolve the owner of a refresh token
val email = refreshTokenStore.findEmailByToken(refreshToken)

// Invalidate a refresh token on logout or rotation
refreshTokenStore.delete(refreshToken)
```

### Security filter

`JwtAuthenticationFilter` is registered automatically. It reads the `Authorization: Bearer <token>` header on every request, validates the token with the configured public key, and populates the Spring Security context.

No additional configuration is required beyond providing a `UserDetailsService` bean in your application.

## Components

| Class | Description |
|---|---|
| `JwtTokenService` | Generates and validates RS512-signed JWTs |
| `JwtAuthenticationFilter` | Servlet filter that extracts and validates Bearer tokens |
| `JwtRefreshTokenStore` | CRUD facade for persisted refresh tokens |
| `JwtRefreshTokenRepository` | Spring Data JPA repository for `JwtRefreshTokenEntity` |
| `JwtRefreshTokenMigration` | Flyway Java migration that creates the `jwt_refresh_token` table |
| `JwtProperties` | `@ConfigurationProperties` binding for the `jwt.*` namespace |

## License

MIT
