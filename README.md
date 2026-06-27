# Movie Club — 电影俱乐部

Spring Boot 3.2.2 + Java 21 电影分享平台，覆盖电影/剧集/动漫浏览、资源管理、视频流媒体、图片识电影、JWT 认证、操作审计。

## 快速开始

### 环境要求

- Java 21+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.9+

### 环境变量

```bash
DEEPSEEK_API_KEY=sk-...    # DeepSeek API 密钥 (RAG，暂未启用)
QWEN_API_KEY=sk-...        # 通义千问 Embedding (RAG，暂未启用)
DB_USERNAME=root           # MySQL 用户名 (默认 root)
DB_PASSWORD=ROOT           # MySQL 密码 (默认 ROOT)
```

### 启动

```bash
# 1. 确保 MySQL 和 Redis 已运行
# 2. 导入数据库 — 执行 datagrip.txt 建表
# 3. 启动
mvn spring-boot:run
# 访问 http://localhost:8080
```

## 项目结构

```
src/main/java/org/example/
├── Servlet/             # REST Controller (14个)
│   ├── LoginController           登录/登出
│   ├── SearchController          统一搜索
│   ├── MovieController           电影 CRUD + 资源 + 评分
│   ├── TV_Controller             动漫 + 剧集 + 分集
│   ├── PlayController            在线播放 + 视频流(Range)
│   ├── BigMovieController        精选推荐
│   ├── HomeRecommendController   首页推荐管理
│   ├── MessageController         留言 + 审核
│   ├── ResourceSubmissionController  资源投稿审核
│   ├── ImageSearchController     图片识电影
│   ├── StatsController           统计数据
│   ├── OperationLogController    操作日志
│   ├── RoleController            角色查询
│   ├── MapperController          MyBatis 联表查询
│   └── selectController          用户管理
├── Service/            # 业务层（全部 AOP 审计）
│   ├── SearchService             统一搜索
│   ├── LoginService              JWT 签发 + Redis 会话
│   ├── MovieService              电影 CRUD + 筛选 + 分页
│   ├── ImageSearchService        图片识电影 → Python :8085
│   └── ...
├── DAO/                # JdbcTemplate 数据访问 (12个)
│   ├── SearchDAO                 搜索 SQL (精准+适中)
│   ├── MovieDAO                  电影查询 + 筛选 + 分页 + 统计
│   ├── TV_DAO                    动漫/剧集查询 + 筛选
│   └── ...
├── Mapper/             # MyBatis (2个)
│   ├── MovieMapper              联表查询 (分数 + 资源)
│   └── RoleMapper               角色详情
├── Entity/             # 数据实体 (14个)
├── Config/             # 配置类
│   ├── SecurityConfig           Spring Security (JWT 无状态)
│   └── RedisConfig              Redis 连接池 + 序列化
├── AI/                 # RAG 管线 (已注释)
├── AOP/                # OperationLogAspect
├── Fileter/            # JWT 过滤器
├── Tool/               # JWT / Redis / 文件上传
└── SpringbootApplication.java

src/main/resources/
├── application.yml
├── filmlane-master/             用户端 (FilmLane 模板)
├── adminkit-web-ui-kit-dashboard-template/static/  管理端 (AdminKit)
└── Mapper/                      MyBatis XML
```

## 数据库

MySQL `security` 库，建表语句见 `datagrip.txt`。核心表：

| 表 | 说明 |
|---|---|
| `movie` | 影片主表（电影/剧集/动漫共用，靠 type 列区分） |
| `movie_resource` | 下载资源（type: 磁力/网盘） |
| `movie_score` | 评分 |
| `movie_episodes` | 剧集/动漫分集 |
| `play_source` | 在线播放源 |
| `users` | 用户 |
| `user_roles` / `role_permissions` / `permissions` | RBAC 权限 |
| `resource_submissions` | 用户投稿（status: pending/approved/rejected） |
| `messages` | 用户留言 |
| `operation_log` | AOP 操作审计 |
| `home_movie_recommend` / `home_tv_recommend` / `home_anime_recommend` | 首页推荐 |

## 认证流程

```
客户端                              服务端
  │  POST /api/user/login            │
  │  {username, password}            │
  │ ───────────────────────────────> │ → AuthenticationManager 认证
  │                                  │ → JWT_Utils 生成 7 天 token
  │                                  │ → Redis 存 login:{userId}
  │  {code:200, data:{token}}        │
  │ <─────────────────────────────── │
  │                                  │
  │  GET /api/xxx                    │
  │  Header: token: <jwt>            │
  │ ───────────────────────────────> │ → JwtAuthenticationTokenFileter
  │                                  │   → 解析 JWT → Redis 查用户
  │                                  │   → 注入 SecurityContextHolder
  │                                  │ → @PreAuthorize 权限校验
```

- 除 `/api/user/login` 外所有接口需要 `token` Header
- JWT 密钥启动时随机生成，重启后所有 token 失效
- **密码编码器已注释，密码明文存储**

## API 接口

### 搜索

```
GET /api/search?q=&mode=&type=
```

| 参数 | 说明 |
|---|---|
| `q` | 关键词，搜电影名/导演/主演 |
| `mode` | 2=适中(非连续LIKE) / 3=精准(完全匹配) |
| `type` | all / movie / tv / anime |

### 电影

| 方法 | 路径 | 权限 |
|---|---|---|
| GET | `/FILMES/ALL` | `movie:view` |
| GET | `/FILMES/ONEP?page=&pageSize=` | `movie:view` |
| GET | `/FILMES/ONEID?id=` | `movie:view` |
| GET | `/FILMES/ONENAME?name=` | `movie:view` |
| GET | `/FILMES/FILTER?type=&year=&region=&language=&sort=` | `movie:view` |
| POST | `/FILMES/ADD` `/UPDATE` `/DELETE` | `movie:manage` |
| GET | `/FILMES/SCORE/ONE?id=` | `score:view` |
| GET | `/FILMES/RESOURCE/ONE?id=` | `resource:view` |

### 剧集 / 动漫

| 方法 | 路径 | 权限 |
|---|---|---|
| GET | `/ANIME/ALL` `/ONEP` `/ONEID` `/ONENAME` `/FILTER` | 公开 |
| GET | `/TV/ALL` `/ONEP` `/ONEID` `/ONENAME` `/FILTER` | 公开 |
| GET/POST | `/{ANIME,TV}/EPISODES/*` | 公开 |

### 播放

| 方法 | 路径 | 权限 |
|---|---|---|
| GET | `/PLAY/RESOURCES` `/PLAY/RESOURCES/ONE` | `resource:view` |
| GET | `/PLAY/STREAM?id=` | `resource:view` (支持 Range) |

### 首页推荐

| 方法 | 路径 | 权限 |
|---|---|---|
| GET | `/home/{movie,tv,anime}?limit=` | `movie:view` |
| POST | `/home/{movie,tv,anime}/{add,delete,reorder}` | `movie:manage` |

### 留言

| 方法 | 路径 | 权限 |
|---|---|---|
| GET | `/api/messages` | 公开 |
| POST | `/api/messages/submit` | token |
| GET/POST | `/api/messages/{pending,approve,reject,delete}` | `movie:manage` |

### 资源投稿

| 方法 | 路径 | 权限 |
|---|---|---|
| POST | `/SUBMIT/RESOURCE` | token |
| GET | `/AUDIT/PENDING` `/AUDIT/ALL` | `movie:manage` |
| POST | `/AUDIT/APPROVE` `/AUDIT/REJECT` | `movie:manage` |

### 其他

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/image-search` | 图片识电影 → Python :8085 |
| GET | `/api/stats/movies-monthly` | 月度电影统计 |
| GET | `/api/logs/recent?limit=` | 操作日志 |
| GET | `/BIGMOVIE/TOP3` `/BIGMOVIE/ALL` | 精选推荐 |
| GET | `/role/detail?name=` | 角色详情 |
| GET | `/JDBC/find/all` | 用户列表 (`user:manage`) |

## 前端页面

### 用户端 (`filmlane-master/`)

| 页面 | 功能 |
|---|---|
| `index.html` | 首页 — Hero 推荐 + 电影/剧集/动漫列表 |
| `search.html` | 统一搜索 — 关键词搜索 + 分类Tab + 适中/精准模式 |
| `All_movie.html` | 电影列表 — 类型/年代/地区/语言筛选 + 分页 |
| `TV_series.html` | 剧集列表 |
| `Net_movie.html` | 动漫列表 |
| `movie-details.html` | 详情页 — 简介+评分+资源(磁力/网盘 Tab)+相似推荐+资源投稿 |
| `Online.html` | 在线播放 |
| `JieSuo.html` | 视频解析 |
| `Setting.html` | 设置 |
| `messages.html` | 留言板 |
| `notifications.html` | 通知 |

### 管理端 (`adminkit-web-ui-kit-dashboard-template/static/`)

| 页面 | 功能 |
|---|---|
| `index.html` | 仪表盘 — 留言/统计/日志概览 |
| `pages-blank.html` | 电影管理 — 增删改查 |
| `pages-audit.html` | 资源审查 — 审核用户投稿 |
| `pages-recommend-manage.html` | 首页推荐管理 |
| `pages-recommend-order.html` | 推荐排序 |
| `pages-message-audit.html` | 留言审核 |
| `pages-message-manage.html` | 留言管理 |
| `pages-sign-in.html` | 登录页 |

## AOP 审计

`OperationLogAspect` 拦截所有 `org.example.Service.*.*(..)` 方法，自动记录：

- 操作用户、方法名、参数
- 操作类型（方法名含 add/insert → INSERT, delete → DELETE, update → UPDATE, get/find → SELECT）
- 结果（SUCCESS/FAIL）、错误信息、耗时(ms)
- 写入 `operation_log` 表，finally 块中执行确保失败也记录

## 图片识电影

用户上传电影截图 → `ImageSearchService` 转发 `http://localhost:8085/movie/recognize` (Python 服务) → 返回识别结果（匹配电影 + 相似电影） → 从本地 DB 补充详情数据。

## RAG 管线（已注释）

```
用户问题 → EmbeddingService(Qwen text-embedding-v3, 1024维)
  → RetrievalService(Milvus COSINE, TopK=5)
  → RAGService 组装 Prompt
  → DeepSeek V4 Pro (LangChain4j OpenAI 兼容接口)
```

AI/ 目录下全部类和 Milvus 配置均为注释状态，待 Milvus 部署和数据灌入后启用。

## 搜索实现

三种搜索模式，统一入口 `/api/search`：

| 模式 | 策略 | 示例 |
|---|---|---|
| 精准 (mode=3) | `WHERE name = ? OR director = ? OR actors = ?` | 完全匹配 |
| 适中 (mode=2) | `WHERE (name/director/actors) LIKE '%X%Y%Z%'` | 非连续字符按序匹配 |
| 模糊 (mode=1) | 向量语义搜索 (Milvus + Qwen) | 预留，待后端接入 |

## 已知问题

- JWT 密钥启动时随机生成，重启全部 token 失效
- 密码编码器已注释，密码明文存储
- MyBatis mapper XML 路径大小写不一致（`Mapper/` vs `mapper/`），Linux 部署会报错
- AI/RAG 管线全部注释，Milvus 未启用
- 视频文件提交在 `Movie_Online/` 目录
- `Tika_Get_File` 为空类
- 部分 API 硬编码 `localhost:8080`，部署需修改
- JWT 过滤器类名拼写 `Fileter`（应为 Filter）
- `ResonseResult` 拼写（应为 ResponseResult）

## 技术栈

| 组件 | 版本 |
|---|---|
| Spring Boot | 3.2.2 |
| Java | 21 |
| MySQL | 8.0+ (mysql-connector-j 8.3.0) |
| Redis | 7.0+ (Lettuce + 连接池) |
| MyBatis | 3.0.3 |
| Spring Security | JWT 无状态 |
| JWT | jjwt 0.11.5 |
| LangChain4j | 1.0.0-beta3 (已注释) |
| Milvus SDK | 2.4.1 (已注释) |
| Apache Tika | 2.9.0 (已注释) |
| Jsoup | 1.17.2 |
| Jackson | 2.16.0 |
| Thymeleaf | 3.1.3 |
| 前端模板 | FilmLane + AdminKit (Bootstrap 5) |

## 致谢

- [FilmLane](https://github.com/codewithsadee/filmlane) — 用户前端模板
- [AdminKit](https://adminkit.io/) — 管理后台模板
- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [Milvus](https://milvus.io/)
