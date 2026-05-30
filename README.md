# 观影 GYING — 电影分享与 AI 智能问答平台

一个集**电影/剧集/动漫资源分享**、**RAG 智能问答**、**用户资源提交与审核**于一体的全栈 Web 应用。

---

## 功能概览

### 影音浏览
- 电影、电视剧、动漫三大分类，各带完整筛选（类型/年代/地区/语言/排序）
- 海报墙展示 + 分页加载 + IMDb 评分显示
- 电影详情页：简介、海报、多平台评分、资源下载列表
- 首页置顶推荐（TOP3）+ 最新 12 部快捷入口
- 用户登录 / JWT 无状态认证
- 深色模式 / 响应式适配

### 资源提交与审核（新）
- 登录用户可提交电影资源（磁力/网盘），填写画质、大小、备注
- 管理员审核后台：待审列表 / 全部列表 / 通过（自动同步到资源表）/ 拒绝（填写理由）
- 所有操作均受 Spring Security 保护

### AI 智能问答（RAG）
- 基于知识库的问答：用户提问 → Embedding → Milvus 语义检索 → DeepSeek 生成回答
- 严格基于参考资料回答，不编造、可溯源，无匹配时返回「未找到相关文档」

### 操作审计
- AOP 切面自动拦截 Service 层所有方法
- 记录：操作用户、方法名、操作类型（INSERT/UPDATE/DELETE/SELECT）、参数、结果、耗时
- 支持审计回溯和安全分析

### 后台管理
- 管理员仪表盘（AdminKit 模板）
- 用户资源审核管理
- 通知页面、空白页面等管理模板

---

## 技术架构

| 层级 | 技术 |
|---|---|
| 后端框架 | Spring Boot 3.2.2 |
| 语言 | Java 21 |
| 数据库 | MySQL 8.0 + MyBatis + JDBC Template |
| 缓存 | Redis（Lettuce 连接池） |
| 安全 | Spring Security + JWT 无状态认证 + BCrypt |
| AI 集成 | LangChain4j 1.0.0-beta3（OpenAI 兼容接口） |
| Chat 模型 | DeepSeek V4 Pro |
| Embedding | 千问 text-embedding-v3（1024 维） |
| 向量库 | Milvus 2.4（SDK，COSINE 相似度，TOP_K=5） |
| 文档解析 | Apache Tika 2.9（PDF / Word / Markdown / HTML） |
| 容器 | Docker（Milvus + etcd + MinIO） |
| 前端 | 原生 HTML/CSS/JS + FilmLane 模板 + AdminKit 仪表盘 |
| 模板引擎 | Thymeleaf 3.1 |
| AOP | Spring AOP 操作日志 |

---

## 项目结构

```
src/main/java/org/example/
├── AI/                         # AI 模块（Chat + Embedding + RAG）
│   ├── LangChain4j.java          # DeepSeek Chat 调用（OpenAI 兼容接口）
│   ├── EmbeddingService.java     # 千问 Embedding（1024 维向量）
│   ├── RetrievalService.java     # Milvus 向量检索（COSINE，TopK=5）
│   ├── RAGService.java           # RAG 主链路（检索 + Prompt 组装）
│   ├── RAGController.java        # POST /api/rag/ask
│   └── LangChain4jController.java # GET /AI/hello（AI 连通性测试）
├── Config/                     # 配置类
│   ├── SecurityConfig.java       # Spring Security：JWT + 无状态 + 接口授权
│   ├── MilvusConfig.java         # Milvus 客户端 Bean
│   ├── MilvusHealthCheck.java    # Milvus 健康检查
│   └── RedisConfig.java          # Redis 序列化 + 连接池
├── DAO/                        # 数据访问层
│   ├── MovieDAO.java             # 电影 CRUD + 筛选 + 分页 + 搜索
│   ├── TV_DAO.java               # 动漫/电视剧查询 + 筛选
│   ├── Movie_ScoreDAO.java       # 电影评分查询
│   ├── Movie_ResourceDAO.java    # 电影资源（磁力/网盘链接）
│   ├── MediaEpisodesDAO.java     # 剧集信息（总集数/更新状态）
│   ├── BigMovieDAO.java          # 首页置顶海报
│   ├── UserDAO.java              # 用户查询
│   ├── OperationLogDAO.java      # 操作日志持久化
│   └── ResourceSubmissionDAO.java # 用户资源提交审核 CRUD
├── Entity/                     # 实体类
│   ├── Movie_details.java        # 电影基本信息
│   ├── MovieCascadeDetails.java  # 电影级联详情（评分/资源）
│   ├── Movie_Score.java          # 评分
│   ├── Movie_Resource.java       # 下载资源
│   ├── MediaEpisodes.java        # 剧集信息
│   ├── Resource_Submission.java  # 用户资源提交（含审核状态）
│   ├── User.java / LoginUser.java # 用户 + Spring Security 适配
│   ├── OperationLog.java         # 操作日志
│   ├── Chunk.java                # 文本分块
│   └── ResonseResult.java        # 统一响应体
├── Service/                    # 业务层
│   ├── MovieService.java         # 电影搜索/筛选/CRUD（Redis 缓存）
│   ├── TV_Service.java           # 动漫/电视剧业务
│   ├── Movie_Score_Service.java  # 评分业务
│   ├── Movie_Resource_Service.java  # 资源管理
│   ├── Movie_Cover_URL_Service.java # 封面 URL 处理
│   ├── Score_Update_Service.java    # 评分更新
│   ├── BigMovieService.java      # 首页推荐
│   ├── MediaEpisodesService.java # 剧集信息服务
│   ├── LoginService.java         # 用户认证（JWT 签发）
│   ├── UserDetailsService_NEW.java # Spring Security 用户加载
│   ├── RedisService.java         # Redis 缓存读写
│   └── ResourceSubmissionService.java # 资源提交 + 审核 + 同步
├── Servlet/                    # 控制器（REST API）
│   ├── MovieController.java      # /FILMES/*（电影 CRUD + 筛选）
│   ├── TV_Controller.java        # /ANIME/* /TV/*（动漫/电视剧）
│   ├── BigMovieController.java   # /BIGMOVIE/TOP3
│   ├── LoginController.java      # /api/user/login /logout
│   ├── MapperController.java     # /Mapper/*（首页快捷接口）
│   ├── selectController.java     # 通用查询
│   ├── HelloController.java      # 健康检查
│   └── ResourceSubmissionController.java # /SUBMIT/* /AUDIT/*（资源审核）
├── Fileter/                    # 过滤器
│   └── JwtAuthenticationTokenFileter.java # JWT Token 解析 + Security 上下文注入
├── Tool/                       # 工具类
│   ├── JWT_Utils.java            # JWT 签发 / 解析 / 验证
│   ├── RedisCache.java           # Redis 缓存工具
│   ├── FileUploadUtil.java       # 文件上传
│   └── Tika_Get_File.java        # Tika 文档内容提取
├── AOP/                        # 切面
│   └── OperationLogAspect.java   # Service 层操作日志自动记录
└── Mapper/                     # MyBatis Mapper 接口
    └── MovieMapper.java          # 电影级联查询（评分 + 资源）

src/main/resources/
├── application.yml               # 主配置（MySQL / Redis / MyBatis）
├── mapper/                       # MyBatis XML 映射文件
├── filmlane-master/              # 用户前端（FilmLane 模板）
│   ├── index.html                # 首页
│   ├── All_movie.html            # 电影列表页
│   ├── Net_movie.html            # 网盘电影页
│   ├── TV_series.html            # 电视剧页
│   ├── movie-details.html        # 电影详情页
│   ├── Setting.html              # 设置页
│   ├── notifications.html        # 通知页
│   └── WinXP.html                # WinXP 怀旧主题页
├── adminkit-web-ui-kit-dashboard-template/ # 管理员后台
│   └── static/
│       ├── index.html            # 仪表盘首页
│       ├── pages-audit.html      # 资源审核页
│       ├── pages-blank.html      # 空白模板页
│       ├── pages-sign-in.html    # 登录页
│       ├── pages-update.html     # 更新页
│       └── live2d-example-master/ # Live2D 看板娘
└── live2d-example-master/        # Live2D 示例
```

---

## 快速启动

### 环境要求
- JDK 21+
- Maven 3.8+
- Docker Desktop
- MySQL 8.0+
- Redis

### 1. 数据库

在 MySQL 中创建 `security` 数据库，然后导入表结构：

```sql
-- 核心业务表
source datagrip.txt;

-- 资源提交审核表
CREATE TABLE resource_submission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY   COMMENT '提交ID',
    movie_id      BIGINT        NOT NULL              COMMENT '关联电影ID',
    movie_name    VARCHAR(255)  NULL                  COMMENT '电影名称（冗余）',
    name          VARCHAR(255)  NOT NULL              COMMENT '资源名称',
    url           VARCHAR(1000) NOT NULL              COMMENT '下载地址',
    type          VARCHAR(20)   DEFAULT '磁力'        COMMENT '资源类型：磁力/网盘',
    quality       VARCHAR(50)                          COMMENT '画质（720P/1080P/4K/蓝光）',
    size          VARCHAR(50)                          COMMENT '文件大小',
    submitter     VARCHAR(100)                         COMMENT '提交者用户名',
    submitter_id  BIGINT        NULL                  COMMENT '提交者用户ID',
    status        VARCHAR(20)   DEFAULT 'pending'     COMMENT '审核状态：pending/approved/rejected',
    review_msg    VARCHAR(500)                         COMMENT '管理员拒绝理由',
    note          VARCHAR(500)                         COMMENT '提交者备注',
    create_time   DATETIME      DEFAULT NOW()          COMMENT '提交时间'
);

-- 操作日志表
CREATE TABLE operation_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(100),
    method        VARCHAR(255),
    operation     VARCHAR(20),
    params        TEXT,
    result        VARCHAR(20),
    error_msg     TEXT,
    execute_time  BIGINT,
    create_time   DATETIME DEFAULT NOW()
);
```

### 2. Milvus（向量数据库）

```bash
cd ~/milvus
docker compose up -d
# 确认三个容器都在运行
docker ps --filter "name=milvus"
```

### 3. 环境变量

```powershell
# Chat 模型（DeepSeek）
$env:DEEPSEEK_API_KEY="sk-..."

# Embedding 模型（千问）
$env:QWEN_API_KEY="sk-..."
```

### 4. 启动后端

```bash
cd security
mvn spring-boot:run
# 默认端口 8080
```

### 5. 访问前端

用 IntelliJ 打开 `src/main/resources/filmlane-master/index.html` 即可预览。

---

## API 概览

### 电影
| method | path | 说明 |
|---|---|---|
| GET | `/FILMES/ONEP?page=1&pageSize=60` | 电影分页 |
| GET | `/FILMES/ONEID?id=1` | 电影详情 |
| GET | `/FILMES/FILTER?type=科幻&year=2024` | 多条件筛选 |
| GET | `/FILMES/SCORE/ONE?id=1` | 电影评分 |
| GET | `/FILMES/RESOURCE/ONE?id=1` | 电影资源列表 |
| GET | `/FILMES/SCORE/search?keyword=xxx` | 搜索电影 |

### 动漫 / 电视剧
| method | path | 说明 |
|---|---|---|
| GET | `/ANIME/ONEP` `/ANIME/FILTER` `/ANIME/ONEID` | 动漫列表/筛选/详情 |
| GET | `/TV/ONEP` `/TV/FILTER` `/TV/ONEID` | 电视剧列表/筛选/详情 |
| GET | `/ANIME/EPISODES/ANIME?id=1` | 动画剧集信息 |
| GET | `/TV/EPISODES/TV?id=1` | 电视剧剧集信息 |

### 首页
| method | path | 说明 |
|---|---|---|
| GET | `/BIGMOVIE/TOP3` | 首页置顶 3 部推荐 |
| GET | `/Mapper/findlast12` | 首页最新 12 部电影 |

### AI / RAG
| method | path | 说明 |
|---|---|---|
| GET | `/AI/hello` | AI 连通性测试 |
| POST | `/api/rag/ask` | RAG 问答 `{"question":"..."}` |

### 用户
| method | path | 说明 |
|---|---|---|
| POST | `/api/user/login` | 登录 `{"username":"...","password":"..."}` |
| GET | `/api/user/logout` | 登出 |

### 资源提交与审核（新）
| method | path | 认证 | 说明 |
|---|---|---|---|
| POST | `/SUBMIT/RESOURCE` | 用户 | 提交电影资源（name/url/type/quality/size/note） |
| GET | `/AUDIT/PENDING` | 管理员 | 待审核列表 |
| GET | `/AUDIT/ALL` | 管理员 | 全部提交记录 |
| POST | `/AUDIT/APPROVE?id=N` | 管理员 | 通过并同步到资源表 |
| POST | `/AUDIT/REJECT?id=N&reason=...` | 管理员 | 拒绝并填写理由 |

> 除 `/api/user/login` 外，所有接口均需 Header 携带 `token`（JWT）认证。

---

## RAG 工作流程

```
用户提问 → Embedding(1024维) → Milvus COSINE相似度检索(TopK=5)
    → Prompt组装(System提示 + 引用编号) → DeepSeek V4 Pro生成回答
```

在 Milvus `rag_documents` 集合中需要预先存入向量化数据，否则返回「未找到相关文档，请先上传资料」。

---

## 致谢

- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [Milvus](https://milvus.io/)
- [Apache Tika](https://tika.apache.org/)
- [FilmLane](https://github.com/codewithsadee/filmlane) — 用户前端模板
- [AdminKit](https://adminkit.io/) — 管理员后台模板
