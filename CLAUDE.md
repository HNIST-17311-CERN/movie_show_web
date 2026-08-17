# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Start (requires MySQL, Redis running)
mvn spring-boot:run        # port 8080

# Run a single test class
mvn test -Dtest=ClassName

# Package
mvn package -DskipTests
```

Required env vars: `DEEPSEEK_API_KEY`, `QWEN_API_KEY`, plus optionally `DB_USERNAME` / `DB_PASSWORD` (default `root`/`ROOT`).

## Architecture

**Spring Boot 3.2.2 + Java 21** — movie sharing platform with admin dashboard, user messaging, resource submissions, video streaming, and image-based movie recognition.

| Layer | Package | Convention |
|---|---|---|
| Controller | `org.example.Servlet` | REST endpoints (`@RestController`), call Service. 18 controllers. |
| Interceptor | `org.example.Interceptor` | `LoginInterceptor` — validates login username/password against `^[a-zA-Z0-9]+$`. |
| Service | `org.example.Service` | Business logic. All methods intercepted by `OperationLogAspect`. |
| DAO | `org.example.DAO` | JDBC Template + hand-written SQL. Primary data access pattern (14 DAOs). Also includes `DocDAO` (richtext docs + file sync via jsoup HTML parsing) and `OperationLogDAO`. |
| Mapper | `org.example.Mapper` | MyBatis interfaces + XML in `resources/Mapper/`. Used for movie cascade & role queries. |
| Entity | `org.example.Entity` | POJOs. `ResonseResult<T>` (note typo) is the unified response wrapper. |
| Config | `org.example.Config` | SecurityConfig, RedisConfig (active); MilvusConfig, MilvusHealthCheck (commented out). |
| AI | `org.example.AI` | RAG pipeline — **all classes currently commented out** (see RAG section). |
| Tool | `org.example.Tool` | Utilities: JWT, Redis cache wrapper, file upload, empty Tika stub. |
| AOP | `org.example.AOP` | `OperationLogAspect` — intercepts all Service methods. |
| Filter | `org.example.Fileter` | `JwtAuthenticationTokenFileter` (JWT auth), `ContentCachingFilter` (request body caching). Note package typo: "Fileter" vs "Filter". |

**Main class:** `org.example.SpringbootApplication` (also `com.liujunming.Application` in test tree, scans same package).

**Database:** MySQL `security` database. Two data access styles coexist: `JdbcTemplate` in DAO classes (hand-written SQL, dominant) and MyBatis with XML mappers (MovieMapper, RoleMapper — for cascade queries and role lookups).

## API Endpoints

| Endpoint | Controller | Auth |
|---|---|---|
| `POST /api/user/login` | LoginController | Public |
| `POST /api/user/logout` | LoginController | token header |
| `GET /api/hello` | HelloController | token header |
| `GET /FILMES/ALL`, `/ONEP`, `/ONEID`, `/ONENAME`, `/FILTER` | MovieController | `movie:view` |
| `POST /FILMES/ADD`, `/UPDATE`, `/DELETE` | MovieController | `movie:manage` |
| `GET /FILMES/SCORE/ONE` | MovieController | `score:view` |
| `GET /FILMES/RESOURCE/ONE`, `POST .../ADD/UPDATE/DELETE` | MovieController | `resource:view` / `resource:manage` |
| `GET /ANIME/ALL`, `/ONEP`, `/ONEID`, `/ONENAME`, `/FILTER` | TV_Controller | Public (no @PreAuthorize) |
| `GET /TV/ALL`, `/ONEP`, `/ONEID`, `/ONENAME`, `/FILTER` | TV_Controller | Public |
| `GET|POST /ANIME/EPISODES/*`, `/TV/EPISODES/*` | TV_Controller | Public |
| `GET /PLAY/RESOURCES`, `/RESOURCES/ONE`, `POST .../ADD/UPDATE/DELETE` | PlayController | `resource:view` / `resource:manage` |
| `GET /PLAY/STREAM` | PlayController | `resource:view` (video streaming with Range support) |
| `GET /BIGMOVIE/TOP3`, `/ALL` | BigMovieController | Public |
| `GET /home/movie`, `/tv`, `/anime` + CRUD | HomeRecommendController | `movie:view` / `movie:manage` |
| `GET|POST /api/messages/*` | MessageController | Public read; `movie:manage` for audit |
| `POST /SUBMIT/RESOURCE`, `GET|POST /AUDIT/*` | ResourceSubmissionController | token header / `movie:manage` |
| `GET /api/stats/movies-monthly` | StatsController | Public |
| `GET /role/detail` | RoleController | `movie:view` |
| `GET /Mapper/findlast12`, `/findMovieWithScoreById`, `/findMovieWithResourcesById` | MapperController | `movie:view` |
| `GET /JDBC/find/all` | selectController | `user:manage` |
| `GET /api/logs/recent`, `/user` | OperationLogController | Public |
| `GET /api/search` | SearchController | Public |
| `POST /api/image-search` | ImageSearchController | Public (delegates to Python service on :8085) |
| `POST /api/files/upload`, `GET .../download/{dateDir}/{fileName}`, `GET .../list` | FileUploadController | Public (`.permitAll()`) |
| `POST /api/docs/save`, `GET /api/docs/{id}`, `GET .../list`, `DELETE .../{id}` | DocController | Public (`.permitAll()`) |
| `GET /api/users/export`, `POST .../import`, `GET .../export-excel`, `POST .../import-excel`, `POST .../preview` | UserImportExportController | Public (`.permitAll()`) |
| `GET /FILMES/animation/ONEP` | MovieController | `movie:view` |
| `GET /role/all` | RoleController | Public (`.permitAll()`) |

## Auth Flow

Stateless JWT: `JwtAuthenticationTokenFileter` (note the typo — "Fileter") extends `OncePerRequestFilter`. Every request except `/api/user/login` requires a `token` header. The filter parses the JWT to get `userid`, fetches `LoginUser` from Redis key `login:{userid}`, and sets it into `SecurityContextHolder`.

LoginService handles JWT issuance (7-day expiry) and Redis storage. `LoginInterceptor` validates login credentials against regex `^[a-zA-Z0-9]+$`. `SecurityConfig` disables CSRF, sets stateless sessions, uses `@EnableGlobalMethodSecurity(prePostEnabled = true)`. **Password encoder is commented out** — passwords are stored/comparsed in plaintext.

UserDetailsService: `UserDetailsService_NEW` loads user from `users` table, permissions via JOIN of `user_roles → role_permissions → permissions`.

## AOP Logging

`OperationLogAspect` intercepts **all** `org.example.Service.*.*(..)` methods via `@Around`. Auto-classifies operations as INSERT/UPDATE/DELETE/SELECT based on method name keywords, records username/method/params/result/duration to `operation_log` table. Runs in the `finally` block so failures are also captured.

## Search

`SearchService` → `SearchDAO` queries the `movie` table with dynamic `LIKE` patterns. `GET /api/search?q=keyword&mode=1-4&type=movie|tv|anime` returns results grouped by type with match counts.

## Image Search

`ImageSearchService` sends the uploaded image via multipart POST to `http://localhost:8085/movie/recognize` (Python service). Result is deserialized into `ImageSearchResult` and enriched with movie details from the local DB.

## RAG Pipeline (CURRENTLY COMMENTED OUT)

All classes in `org.example.AI` and Milvus configs are commented out:
- `EmbeddingService` — Qwen text-embedding-v3 (1024-dim) via LangChain4j
- `RetrievalService` — Milvus COSINE search, TopK=5, collection `rag_documents`
- `RAGService` — assembles system prompt with numbered references
- `LangChain4j` — DeepSeek V4 Pro chat via OpenAI-compatible API
- `RAGController` — `POST /api/rag/ask`
- `LangChain4jController` — `GET /AI/hello`
- `MilvusConfig` / `MilvusHealthCheck` — Milvus connection + collection/index setup

To re-enable: uncomment all files, ensure Docker Milvus is running, pre-load documents into `rag_documents`.

## Document Management

`DocController` + `DocDAO` manage rich-text documents (`richtext_docs` + `doc_files` tables). DocDAO uses jsoup to parse HTML content and extract file references (`<img src>`, `<a href>` matching `/api/files/download/`) to sync the `doc_files` association table.

## Notable Dependencies

| Dependency | Purpose |
|---|---|
| `jsoup` 1.17.2 | HTML parsing (DocDAO file extraction) |
| `poi-ooxml` 5.2.5 | Excel import/export (UserImportExportController) |
| `langchain4j-*` 1.0.0-beta3 | AI/RAG pipeline (currently commented out) |
| `milvus-sdk-java` 2.4.1 | Vector DB client (currently commented out) |
| `tika-*` 2.9.0 | Document parsing (declared but unused) |
| `jjwt-*` 0.11.5 | JWT creation and verification |

## Frontend

Three static resource directories under `src/main/resources/`:

| Directory | Purpose |
|---|---|
| `filmlane-master/` | User-facing movie browsing site. Pages: index, movie details, TV series, anime, online player, movie list, settings, messages, recommendations, WinXP easter egg. |
| `adminkit-web-ui-kit-dashboard-template/static/` | Admin dashboard (AdminKit template). Pages: index, sign-in, recommendations, message audit, resource audit, blank. |
| `Movie_Online/` | Empty directory (video files previously committed have been removed). |

Plus `live2d-example-master/` for Live2D widget.

Thymeleaf is declared as a dependency; some pages may use it.

## Key Caveats

- **Case-sensitive mapper path**: XML mappers are in `resources/Mapper/` (capital M) but `application.yml` declares `classpath:mapper/*.xml` (lowercase m). Works on Windows/macOS, breaks on Linux.
- **JWT token header** is named `token` (not `Authorization: Bearer`).
- **Password encoder is commented out** in `SecurityConfig` — passwords stored as plaintext in the database. Re-enable `BCryptPasswordEncoder` bean before production use.
- **JWT secret key is generated at startup** via `Keys.secretKeyFor(HS256)` — all tokens are invalidated on every restart. For production, use a fixed secret from config/env.
- **Class name typos**: `JwtAuthenticationTokenFileter` (Filter), `ResonseResult` (ResponseResult), package `Fileter` (Filter).
- **Duplicate Redis helpers**: `RedisCache` (used by auth) and `RedisService` (appears unused) — both wrap the same `RedisTemplate`.
- **Movie_Online/ is empty**: Video files previously committed have been removed.
- **Tika_Get_File** is an empty class (unimplemented stub).
- **Some endpoints lack `@PreAuthorize`**: `/ANIME/*`, `/TV/*`, `/BIGMOVIE/*`, `/api/stats/*`, `/api/logs/*`, `/api/image-search`, `/api/search`, `/AUDIT/*`, `/SUBMIT/*` are publicly accessible. `/api/files/**`, `/api/users/**`, `/api/docs/**`, `/role/all` are explicitly `.permitAll()` in SecurityConfig.
- **server.address is `::`**: configured for dual-stack (IPv4+IPv6) listening. External users can reach the API via IPv6 address directly (firewall permitting).
- **Hardcoded config**: Redis host/port, Python service URL (`localhost:8085`) are hardcoded rather than in `application.yml`.
- **test Application class** at `src/test/java/com/liujunming/Application.java` is a duplicate entry point scanning `org.example`.
- **MySQL driver**: uses `mysql-connector-j` 8.3.0 (not the older `mysql-connector-java`).
