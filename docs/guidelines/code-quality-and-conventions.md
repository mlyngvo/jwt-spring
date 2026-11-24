# Code Quality, Conventions & Linting Standards

This document establishes the code quality, static analysis toolchain, Git hook automation, and architectural coding standards for the `mlyngvo/jwt-spring` repository.

---

## 1. Toolchain & Static Analysis Matrix

Our repository enforces formatting consistency, clean Kotlin idioms, and static analysis checks:

| Language | Primary Formatter | Static Analysis / Linter | Configuration File | Key Enforcements |
| :--- | :--- | :--- | :--- | :--- |
| **Kotlin (`src/main`, `src/test`, `*.kts`)** | **`ktlint`** | **`detekt`** | `.editorconfig`<br>`detekt.yml` | 120-char line limit, wildcards forbidden in imports, cyclomatic complexity limits, no empty catch blocks, explicit API clarity. |

---

## 2. Local Git Hooks & Automation Workflow

Code quality is enforced locally prior to committing, preventing broken or unformatted code from reaching the remote repository.

### Pre-Commit Hook Setup
When Git hooks are configured in the repository (`./gradlew installGitHooks`), commits should automatically verify:
1. **Staged Kotlin Files:** Runs `ktlintFormat` / `ktlintCheck` on staged `.kt` and `.kts` files.
2. **Commit Message Rules:** Validates that commit headers follow Conventional Commits without scopes and without forbidden trailers (e.g., no `Co-authored-by:` or AI signatures).

### CLI Convenience Commands
```bash
./gradlew ktlintCheck       # Verify Kotlin code formatting
./gradlew ktlintFormat      # Auto-format Kotlin source files
./gradlew detekt            # Run full Detekt static analysis
./gradlew check             # Run all linters and tests
```

### CI Pull Request Gates
Every pull request triggers a GitHub Actions workflow that executes:
* `./gradlew ktlintCheck detekt test` (or `./gradlew check`)

Pull requests with formatting or static analysis failures cannot be merged.

---

## 3. Core Architectural Conventions

### 3.1 Spring Boot Library & Auto-Configuration Boundaries
Because `jwt-spring` is a reusable Spring Boot starter/library consumed by host applications:
* **Override-Friendly Beans:** All auto-configured beans must use appropriate `@ConditionalOnMissingBean` / `@ConditionalOnProperty` annotations so host applications can replace or customize components without fighting the starter.
* **Minimal Public Surface:** Keep internal helpers `internal` or `private`. Only expose intentional, well-documented public contracts (properties, service interfaces, filter, store).
* **No Hidden Global State:** Avoid static mutable singletons or hard-coded system clocks (`Instant.now()` / `Date()`); inject dependencies (such as `Clock`) via constructor injection.
* **Clean Dependency Hygiene:** Only declare dependencies as `api` when their types appear in the library's public binary API; keep internal dependencies `implementation`.

### 3.2 Security & Cryptographic Hygiene
* **Explicit Algorithm Enforcement:** Never trust the `alg` header blindly; enforce the expected signing algorithm and key type on verification.
* **No Secret Leakage:** Never log raw JWTs, refresh tokens, private keys, or credentials at any log level.
* **Fail Closed:** Invalid, expired, malformed, or unverified tokens must never populate the `SecurityContext` and must fail cleanly without leaking sensitive verification details.

### 3.3 Typed Error Modeling & Exception Handling
* **Explicit Failure Modes:** Model expected validation or persistence failures clearly (sealed types or well-defined domain exceptions) rather than throwing generic `RuntimeException`.
* **Zero Silent Catch:** Catching generic `Throwable` or `Exception` and swallowing it without logging or mapping to an explicit failure is strictly forbidden.

### 3.4 Immutability & Data Modeling
* **Read-Only Defaults:** Default to `val`. Use `var` only when local mutation is strictly necessary and contained within a small function scope (or required by JPA entity constraints).
* **Immutable Collections:** Public interfaces must expose read-only collections (`List<T>`, `Set<T>`, `Map<K, V>`). Never expose `MutableList` or `ArrayList` across public boundaries.
* **Immutable Configuration & DTOs:** Prefer immutable data classes and constructor-bound `@ConfigurationProperties`.
* **Explicit States:** Model distinct token/auth outcomes as sealed class/interface hierarchies rather than ambiguous boolean flags or nullable tuples.

---

## 4. Code Formatting Quick Reference

### EditorConfig (`.editorconfig`)
The repository uses an `.editorconfig` file enforcing:
* Charset: `utf-8`
* End of line: `lf`
* Indent style: `space`
* Indent size: `4` (Kotlin, Java), `2` (YAML, JSON, Markdown)
* Trim trailing whitespace: `true`
* Insert final newline: `true`
* Max line length: `120`
