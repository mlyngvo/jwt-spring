# Automated Testing Strategy & Quality Gates

This document establishes the testing architecture, framework toolchain, and CI quality gates for the `mlyngvo/jwt-spring` repository.

---

## 1. Testing Philosophy & Pyramid

Our testing strategy follows a pragmatic pyramid tailored to a Spring Boot security library: **fast, hermetic unit tests** for token cryptography, validation, and filter behavior, paired with **focused Spring Boot integration tests** for auto-configuration, security filter wiring, and persistence/migrations.

```
                  ┌──────────────────────────────────────────┐
                  │       Spring Boot Integration Tests      │   (ApplicationContextRunner,
                  │  (Auto-Config, MockMvc, JPA + Flyway)    │    H2 / Testcontainers)
                  ├──────────────────────────────────────────┴──────────────────┐
                  │                        Unit Tests                           │   (JwtTokenService,
                  │            (Fast, Deterministic, 100% Hermetic)             │    Key loaders, Filter)
                  └─────────────────────────────────────────────────────────────┘
```

### Coverage & Scope Matrix

| Layer | Unit Tests | Integration Tests | Test Frameworks |
| :--- | :--- | :--- | :--- |
| **Token & Crypto (`JwtTokenService`, Key Loaders)** | **High (>90%)**: Token generation, claims, RS512 verification, expiry/clock skew, tampered/malformed tokens, invalid keys. | Key loading from classpath/resources via Spring `ResourceLoader`. | JUnit 5<br>`kotlin.test` / AssertJ<br>Fixed `Clock` |
| **Security Filter (`JwtAuthenticationFilter`)** | Missing header, non-Bearer header, valid/invalid/expired tokens, pre-authenticated context preservation. | End-to-end HTTP request filtering through Spring Security filter chain (`MockMvc`). | JUnit 5<br>Spring Security Test<br>`MockMvc` |
| **Refresh Token Store & Migrations** | Store delegation, expiry checks, rotation/revocation semantics. | Flyway migration execution + Spring Data JPA CRUD & uniqueness/expiry queries. | `@DataJpaTest` / `@SpringBootTest`<br>H2 (and/or Testcontainers) |
| **Auto-Configuration (`JwtAutoConfiguration`, `JwtProperties`)** | Property validation and defaults. | Conditional bean registration, user bean overrides (`@ConditionalOnMissingBean`), missing-property failure modes. | `ApplicationContextRunner` |

---

## 2. Test Toolchain

* **JUnit 5 (`useJUnitPlatform()`):** Standard test runner for unit and integration suites.
* **Deterministic Time (`java.time.Clock`):** Use fixed or mutable test clocks for all token issuance and expiration tests — never use `Thread.sleep` to test token expiry.
* **Spring Boot `ApplicationContextRunner`:** Fast, isolated verification of auto-configuration conditions, property binding, and user-provided bean overrides without booting a full web server.
* **Spring Security Test & `MockMvc`:** Verifies `Authorization: Bearer <token>` extraction, `SecurityContext` population, and unauthorized handling across HTTP endpoints.
* **JPA + Flyway Persistence Tests:** Verifies schema migrations apply cleanly and `JwtRefreshTokenRepository` / `JwtRefreshTokenStore` operations behave correctly against a real SQL engine.

---

## 3. Test Execution Commands

### Local CLI Execution

```bash
# Run all unit and integration tests
./gradlew test

# Run full verification suite (linters + static analysis + tests)
./gradlew check
```

---

## 4. CI Pull Request Gates

All GitHub Actions workflows enforce the following mandatory quality gates before any PR can merge:

1. **100% Test Pass Rate:** Every unit and integration test in `./gradlew test` must pass.
2. **Zero Security Regressions:** Token signature verification, expiration enforcement, and filter rejection tests must maintain 100% pass rate.
3. **Zero Linting / Static Analysis Violations:** `ktlintCheck` and `detekt` must pass cleanly.
4. **No Flaky Tests:** Any test exhibiting non-deterministic failure in CI (e.g., timing/clock races) must be diagnosed and resolved immediately.
