# 观影 GYING — 电影分享与 AI 智能问答平台

一个集**电影/剧集/动漫资源分享**与 **RAG 智能问答**于一体的全栈 Web 应用。

---

## 功能概览

### 影音浏览
- 电影、电视剧、动漫三大分类，各带完整筛选（类型/年代/地区/语言/排序）
- 海报墙展示 + 分页加载 + IMDb 评分显示
- 电影详情页：简介、海报、多平台评分、资源下载列表
- 用户登录 / JWT 认证 / 收藏 / 观看历史
- 深色模式 / 响应式适配

### AI 智能问答（RAG）
- 文档上传 → Tika 解析 → 智能分块 → 向量化 → 存入 Milvus
- 用户提问 → Embedding → Milvus 语义检索 → DeepSeek 生成回答
- 基于你的电影资料库回答，不编造、可溯源

### 核心 AI 技术栈
| 模块 | 技术 |
|---|---|
| Chat 大模型 | DeepSeek V4 Pro（LangChain4j） |
| Embedding 向量化 | 千问 text-embedding-v3（LangChain4j） |
| 向量数据库 | Milvus（Docker 部署，IVF_FLAT + COSINE） |
| 文档解析 | Apache Tika（PDF / Word / Markdown / HTML） |
| 文本分块 | 500 字 + 100 字重叠滑动窗口 |
| 检索链路 | 语义向量搜索 → Prompt 组装 → 生成回答 |

---

## 技术架构

| 层级 | 技术 |
|---|---|
| 后端框架 | Spring Boot 3.2.2 |
| 语言 | Java 21 |
| 数据库 | MySQL + MyBatis |
| 缓存 | Redis（Lettuce 连接池） |
| 安全 | Spring Security + JWT 无状态认证 |
| AI 集成 | LangChain4j 1.0.0-beta3 |
| 向量库 | Milvus 2.4（SDK） |
| 文档处理 | Apache Tika 2.9 |
| 容器 | Docker（Milvus + etcd + MinIO） |
| 前端 | 原生 HTML/CSS/JS，不依赖前端框架 |

---

## 项目结构

```
src/main/java/org/example/
├── AI/                  # AI 模块（Chat + Embedding + RAG）
│   ├── LangChain4jService.java     # DeepSeek Chat 调用
│   ├── LangChain4jController.java  # /AI/hello
│   ├── EmbeddingService.java       # 千问 Embedding
│   ├── DocumentParserService.java  # Tika 文档解析
│   ├── ChunkingService.java        # 文本分块
│   ├── IngestionService.java       # 文档入库流水线
│   ├── RetrievalService.java       # Milvus 向量检索
│   ├── RAGService.java             # RAG 主链路
│   └── RAGController.java          # /api/rag/ask
├── Config/              # 配置（Security / Redis / Milvus）
├── DAO/                 # 数据访问层
│   ├── MovieDAO.java              # 电影 CRUD
│   ├── TV_DAO.java                # 动漫/电视剧查询
│   ├── Movie_ScoreDAO.java        # 评分
│   ├── Movie_ResourceDAO.java     # 资源下载
│   ├── MediaEpisodesDAO.java      # 剧集信息（集数/更新状态）
│   └── BigMovieDAO.java           # 首页置顶海报
├── Entity/              # 实体类
├── Service/             # 业务层
├── Servlet/             # 控制器（REST API）
│   ├── MovieController.java       # /FILMES/*
│   ├── TV_Controller.java         # /ANIME/* /TV/*
│   ├── BigMovieController.java    # /BIGMOVIE/*
│   ├── LoginController.java       # /api/user/*
│   └── MapperController.java      # 首页快捷接口
├── Fileter/             # JWT 认证过滤器
├── Tool/                # 工具类（JWT / Redis / 文件上传）
└── AOP/                 # 操作日志切面
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

```sql
-- 导入 movie / movie_score / movie_resource / media_episodes / big_movie 表
source datagrip.txt;
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

用 IntelliJ 打开 `filmlane-master/index.html`，或直接访问：
```
http://localhost:63342/security/springboot-demo/filmlane-master/index.html
```

---

## API 概览

### 电影
| method | path | 说明 |
|---|---|---|
| GET | `/FILMES/ONEP?page=1&pageSize=60` | 电影分页 |
| GET | `/FILMES/ONEID?id=1` | 电影详情 |
| GET | `/FILMES/FILTER?type=科幻&year=2024` | 筛选 |
| GET | `/FILMES/SCORE/ONE?id=1` | 评分查询 |
| GET | `/FILMES/RESOURCE/ONE?id=1` | 资源查询 |

### 动漫 / 电视剧
| method | path | 说明 |
|---|---|---|
| GET | `/ANIME/ONEP` `/ANIME/FILTER` `/ANIME/ONEID` | 动漫 |
| GET | `/TV/ONEP` `/TV/FILTER` `/TV/ONEID` | 电视剧 |
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
| GET | `/AI/hello` | AI 接口测试 |
| POST | `/api/rag/ask` | RAG 问答 `{"question":"..."}` |

### 用户
| method | path | 说明 |
|---|---|---|
| POST | `/api/user/login` | 登录 |

---

## RAG 工作流程

```
┌─────────────────────────────────────────────────────┐
│  文档入库（Ingestion）                               │
│  PDF/Word/Markdown → Tika解析 → 分块 → Embedding → Milvus │
├─────────────────────────────────────────────────────┤
│  智能问答（RAG）                                     │
│  用户提问 → Embedding → Milvus搜索 → Prompt组装 → DeepSeek生成 │
└─────────────────────────────────────────────────────┘
```

需要先在 Milvus 中有数据才能问答，否则返回「未找到相关文档，请先上传资料」。

---

## 致谢

- [LangChain4j](https://github.com/langchain4j/langchain4j)
- [Milvus](https://milvus.io/)
- [Apache Tika](https://tika.apache.org/)
- [FilmLane](https://github.com/codewithsadee/filmlane) — 前端模板来源
