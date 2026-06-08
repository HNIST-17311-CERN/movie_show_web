# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Start (requires MySQL, Redis, Milvus running)
mvn spring-boot:run        # port 8080

# Run a single test class
mvn test -Dtest=ClassName

# Package
mvn package -DskipTests
```

Required env vars: `DEEPSEEK_API_KEY`, `QWEN_API_KEY`, plus optionally `DB_USERNAME` / `DB_PASSWORD` (default `root`/`ROOT`).

## Architecture

**Spring Boot 3.2.2 + Java 21** — movie sharing platform with AI RAG Q&A.

| Layer | Convention |
|---|---|
| Controller | `org.example.Servlet` — REST endpoints, call Service |
| Service | `org.example.Service` — business logic, all methods AOP-logged |
| DAO | `org.example.DAO` — JDBC Template queries (primary data access) |
| Mapper | `org.example.Mapper` — MyBatis annotated interfaces, XML in `resources/Mapper/` |
| Entity | `org.example.Entity` — POJOs, `ResonseResult` is the unified response wrapper |

**Main class:** `org.example.SpringbootApplication` (also `com.liujunming.Application` in test tree, scans same package).

**Database:** MySQL `security` database. Two data access styles coexist: `JdbcTemplate` in DAO classes (hand-written SQL, the dominant pattern) and MyBatis with XML mappers (used for movie cascade queries).

## Auth Flow

Stateless JWT: `JwtAuthenticationTokenFileter` (note the typo in class name — "Fileter", not "Filter") extends `OncePerRequestFilter`. Every request except `/api/user/login` requires a `token` header. The filter parses the JWT to get `userid`, fetches `LoginUser` from Redis key `login:{userid}`, and sets it into `SecurityContextHolder`. LoginService handles JWT issuance and Redis storage. `SecurityConfig` disables CSRF, sets stateless sessions, uses `@EnableGlobalMethodSecurity(prePostEnabled = true)`.

## AOP Logging

`OperationLogAspect` intercepts **all** `org.example.Service.*.*(..)` methods via `@Around`. It auto-classifies operations as INSERT/UPDATE/DELETE/SELECT based on method name keywords, records username/method/params/result/duration to `operation_log` table. Runs in the `finally` block so failures are also captured.

## RAG Pipeline

```
User question → EmbeddingService (Qwen text-embedding-v3, 1024-dim)
  → RetrievalService (Milvus COSINE, TopK=5)
  → RAGService assembles prompt with numbered references
  → DeepSeek V4 Pro (via LangChain4j OpenAI-compatible API)
```

Milvus requires Docker (`milvus + etcd + minio`). Embedded documents must be pre-loaded into the `rag_documents` collection.

## Frontend

Two static sites served as resources (Thymeleaf for some pages):
- `filmlane-master/` — user-facing movie browsing (FilmLane template)
- `adminkit-web-ui-kit-dashboard-template/` — admin dashboard (AdminKit)

## Key Caveats

- **Case-sensitive mapper path**: XML mappers are in `resources/Mapper/` (capital M) but `application.yml` declares `classpath:mapper/*.xml` (lowercase m). This works on Windows/macOS but would break on case-sensitive filesystems.
- **JWT token header** is named `token` (not `Authorization: Bearer`).
- **test Application class** at `src/test/java/com/liujunming/Application.java` is a duplicate entry point that scans `org.example` — used for testing from a different base package.
