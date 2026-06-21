# 电影俱乐部 Movie Club — 前后端信息传递与数据展示架构

## 一、整体架构概览

```
┌─────────────────────────────────────────────────────────────┐
│  浏览器前端（静态 HTML + JS）                                  │
│  ├─ filmlane-master/    用户端（首页/电影/剧集/动漫/详情/留言）  │
│  └─ adminkit/static/    管理后台（登录/电影管理/资源审查）       │
├─────────────────────────────────────────────────────────────┤
│  信息传递：HTTP REST API + JWT 认证                           │
│  数据格式：JSON（所有API请求/响应统一使用JSON）                  │
│  传输方式：Fetch API + token Header                           │
├─────────────────────────────────────────────────────────────┤
│  Spring Boot 后端 (localhost:8080)                            │
│  ├─ Servlet (Controller)     REST 端点，@ResponseBody自动序列化 │
│  ├─ Service                  业务逻辑 + AOP 日志              │
│  ├─ DAO / Mapper             JDBC Template / MyBatis          │
│  └─ MySQL (security库)       持久化存储                       │
├─────────────────────────────────────────────────────────────┤
│  外部服务                                                    │
│  ├─ Redis            JWT 会话 (login:{userid})               │
│  ├─ Milvus           向量检索 (RAG)                           │
│  └─ Python :8085     相似电影推荐 (JSON通信)                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 二、数据传递的核心：JSON

### 2.1 为什么是 JSON？

整个项目的前后端通信**全部使用 JSON**（仅视频流和文件上传除外）。原因：

- Spring Boot 的 `@RestController` 默认使用 Jackson 将 Java 对象自动序列化为 JSON
- 前端 `fetch()` 用 `resp.json()` 直接解析为 JavaScript 对象，无需手动拼接/拆解
- 统一响应包装类 `ResonseResult` 确保所有接口返回结构一致

### 2.2 JSON 数据在系统中的流动

```
┌──────────────────────────────────────────────────────────────────┐
│                        MySQL 数据库                               │
│  movie_details 表, movie_score 表, user 表, operation_log 表...   │
└──────────────────────────┬───────────────────────────────────────┘
                           │ JDBC / MyBatis 查询
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                    Java Entity 对象                               │
│  Movie_details.java, Movie_Score.java, User.java...              │
│  字段: id(Long), name(String), cover(String), type(String)...    │
└──────────────────────────┬───────────────────────────────────────┘
                           │ Jackson 自动序列化 (@RestController)
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                    JSON 响应 (HTTP Body)                          │
│  {                                                               │
│    "code": 200,                                                  │
│    "msg": "操作成功",                                             │
│    "data": { "id":1, "name":"Inception", "cover":"...", ... }    │
│  }                                                               │
└──────────────────────────┬───────────────────────────────────────┘
                           │ HTTP Response → 浏览器
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│              前端 JavaScript 对象 (resp.json())                   │
│  data.code === 200                                               │
│  data.data.name === "Inception"                                  │
│  data.data.cover → getCoverUrl() → <img src>                     │
└──────────────────────────┬───────────────────────────────────────┘
                           │ DOM 操作
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                    页面 DOM 元素                                  │
│  <h1>Inception</h1>  <img src="./assets/images/...">             │
│  <span class="card-score">8.8</span>                             │
└──────────────────────────────────────────────────────────────────┘
```

### 2.3 两种 JSON 传递方向

| 方向 | Content-Type | 示例 |
|---|---|---|
| **前端→后端** (Request Body) | `application/json` | `JSON.stringify({ username, password })` |
| **后端→前端** (Response Body) | `application/json` | `resp.json()` 解析为 JS 对象 |
| **GET 请求参数** (Query String) | URL 编码 | `?id=1&page=1&pageSize=60` — 简单值，非 JSON |
| **文件上传** (FormData) | `multipart/form-data` | 电影JSON放在FormData的"movie"字段中 |

---

## 三、认证与鉴权流程（JSON 全程）

### 3.1 登录流程

```
用户输入用户名/密码
    │
    ▼
POST http://localhost:8080/api/user/login
Content-Type: application/json
Body:  {"username": "admin", "password": "123456"}
    │
    ▼
后端: LoginService → 查MySQL验证 → JWT_Utils生成token → Redis存储
    │
    ▼
HTTP 200 响应 (JSON):
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "phone": "138xxxx"
  }
}
    │
    ▼
前端解析:
  resp.json().then(data => {
    localStorage.setItem("token", data.data.token);   // "eyJhbGciOiJIUzI1NiJ9..."
    localStorage.setItem("username", username);       // "admin"
    window.location.href = "../../filmlane-master/index.html";
  })
```

### 3.2 每次请求的鉴权

```
前端: headers: { "token": localStorage.getItem("token") }
  │   （token 值是一个 JWT 字符串，不是 JSON 对象）
  ▼
后端: JwtAuthenticationTokenFileter
  → 解析 JWT → 获取 userid → 从 Redis 读 LoginUser → 注入 SecurityContext
  → 不需要额外 JSON 解析，token 本身就是加密过的身份信息
```

---

## 四、全部 API 的 JSON 结构详解

### 4.1 统一响应格式

**所有后端接口**都返回此结构（Java 类 `ResonseResult`，注意类名拼写是历史遗留）：

```json
{
  "code": 200,        // Number — 200=成功，其他=失败
  "msg": "操作成功",    // String — 提示信息
  "data": { ... }     // Object | Array | null — 实际数据
}
```

前端统一判断：`if (data.code === 200) { ... } else { alert(data.msg); }`

---

### 4.2 用户端 API — 请求与响应 JSON 对照

#### 登录

```
POST /api/user/login
→ Body:   { "username": "admin", "password": "123456" }
← data:   { "token": "eyJ...", "phone": "138xxxx" }
```

#### 首页电影列表（MyBatis 级联查询）

```
GET /Mapper/findlast12
→ Query:  无参数（后端硬编码12个电影ID）
← data:   [
            {
              "id": 1,
              "name": "Inception",
              "cover": "assets/images/movie-1.png",
              "director": "Christopher Nolan",
              "actors": "Leonardo DiCaprio, Joseph Gordon-Levitt",
              "type": "科幻",
              "region": "美国",
              "language": "英语",
              "releaseDate": "2010-07-16",
              "duration": 148,
              "description": "一部关于梦境入侵的科幻电影..."
            },
            ...
          ]
```

#### 电影分页列表

```
GET /FILMES/ONEP?page=1&pageSize=60
→ Query:  page=1, pageSize=60
← data:   [ { id, name, cover, director, actors, type, region, language, releaseDate, duration, description }, ... ]
          也可能是 { "data": [ ... ] } 格式（前端做了兼容处理）
```

#### 按名称搜索

```
GET /FILMES/ONENAME?name=Inception
→ Query:  name=Inception（模糊匹配）
← data:   [ { id, name, cover, ... }, ... ]
```

#### 单部电影详情

```
GET /FILMES/ONEID?id=1
→ Query:  id=1
← data:   { "id":1, "name":"Inception", "cover":"...", "director":"...",
            "screenwriter":"...", "actors":"...", "type":"科幻",
            "region":"美国", "language":"英语", "releaseDate":"2010-07-16",
            "duration":148, "description":"...", "status":"已上映" }
```

#### 电影评分

```
GET /FILMES/SCORE/ONE?id=1
→ Query:  id=1
← data:   [ { "score": 8.8, "count": 2300000 }, ... ]
          前端取 scores[0].score 显示，scores[0].count 显示"XXX人评价"
```

#### 电影资源列表

```
GET /FILMES/RESOURCE/ONE?id=1
→ Query:  id=1
← data:   [
            {
              "id": 101,
              "name": "蓝光原盘",
              "url": "magnet:?xt=urn:btih:...",
              "size": "22.4GB",
              "seed": 156,
              "quality": "4K",
              "category": "蓝光原盘",
              "pubDate": "2024-03-15"
            },
            ...
          ]
```

#### 多条件筛选

```
GET /FILMES/FILTER?page=1&pageSize=60&type=科幻&year=2024&region=美国&language=英语&sort=create_time
→ Query:  筛选参数拼在URL上
← data:   与 /FILMES/ONEP 返回结构相同 → [ { id, name, ... }, ... ]
```

#### 剧集列表

```
GET /TV/ONEP?page=1&pageSize=60
← data:   [ { id, name, cover, releaseDate, region, type, ... }, ... ]
```

#### 剧集集数信息

```
GET /TV/EPISODES/TV?id=1
← data:   { "totalEpisodes": 12, "updateStatus": "已完结" }
          前端显示: "全12集" / "更新至第8集"
```

#### 动漫列表

```
GET /ANIME/ONEP?page=1&pageSize=60
← data:   [ { id, name, cover, releaseDate, region, type, ... }, ... ]
```

#### 动漫集数信息

```
GET /ANIME/EPISODES/ANIME?id=1
← data:   { "totalEpisodes": 24, "updateStatus": "连载中" }
```

#### 视频资源列表

```
GET /PLAY/RESOURCES
← data:   [ { "id": 1, "name": "Inception.mp4", "url": "/videos/inception.mp4" }, ... ]
```

#### 视频流（非 JSON！）

```
GET /PLAY/STREAM?id=1
← 响应: 二进制 Blob 流 (video/mp4)
  前端: resp.blob() → URL.createObjectURL(blob) → <video>.src
```

#### 用户提交资源

```
POST /SUBMIT/RESOURCE
Content-Type: application/json
→ Body:   {
            "movie_id": 1,
            "movie_name": "Inception",
            "resouce_name": "4K蓝光压制版",
            "url": "magnet:?xt=urn:btih:...",
            "type": "磁力",
            "quality": "4K",
            "size": "22.4GB",
            "submitter": "admin",
            "note": "已测试可下载"
          }
← data:   { "code": 200, "msg": "提交成功，等待管理员审核" }
```

#### Hero 推荐（首页Banner）

```
GET /BIGMOVIE/TOP3
← data:   [ { "id":1, "name":"...", "cover":"..." },
            { "id":2, "name":"...", "cover":"..." },
            { "id":3, "name":"...", "cover":"..." } ]
          固定返回3条，前端渲染到Hero区域
```

#### 全部电影（Hero侧边栏补全详情用）

```
GET /FILMES/ALL
← data:   [ { id, name, cover, ... }, ... ]  — 全量数据
          前端用于从BIGMOVIE/TOP3的id匹配完整电影信息
```

#### 相似电影（Python 服务 :8085）

```
POST http://localhost:8085/movie/similar
Content-Type: application/json
→ Body:   { "movie_id": 1 }
← data:   { "similar_ids": [5, 12, 23, 45, 67, 89, 102, ...] }
          前端拿到ids后用 Promise.all 并发查本机的 /FILMES/ONEID?id=
```

---

### 4.3 管理后台 API — JSON 对照

#### 新增电影（混合 FormData + JSON）

```
POST /FILMES/ADD
Content-Type: multipart/form-data
→ Body (FormData):
    "movie": Blob(JSON.stringify({
               "name": "Inception",
               "director": "Christopher Nolan",
               "actors": "Leonardo DiCaprio",
               "type": "科幻",
               "region": "美国",
               "language": "英语",
               "releaseDate": "2010-07-16",
               "duration": 148,
               "description": "..."
             }), { type: "application/json" })
    "file": <input type="file"> 的 File 对象

  ⚠️ 关键：电影元数据以 JSON Blob 形式放在 FormData 的 "movie" 字段中
```

#### 更新电影

```
POST /FILMES/UPDATE
Content-Type: multipart/form-data
→ Body (FormData):
    "movie": Blob(JSON.stringify({ "id":1, "name":"...", ... }))
    "file": File 对象（可选，不选则保留原封面）

  ⚠️ 不手动设置 Content-Type，浏览器自动添加 boundary
```

#### 删除电影

```
POST /FILMES/DELETE?id=1
→ Query:  id=1
← 响应:   纯文本 "删除成功"（非 JSON）
```

#### 资源 CRUD

```
POST /FILMES/RESOURCE/ADD
Content-Type: application/json
→ Body:   { "movieId":1, "name":"蓝光原盘", "quality":"4K", "size":"22.4GB",
            "type":"磁力", "url":"magnet:...", "createTime":"2024-03-15T20:30", "subtitle":"中字" }
← 响应:   纯文本

POST /FILMES/RESOURCE/UPDATE
Content-Type: application/json
→ Body:   { "id":101, "movieId":1, "name":"...", ... }  （含id表示更新）
← 响应:   纯文本

POST /FILMES/RESOURCE/DELETE?id=101
→ Query:  id=101
← 响应:   纯文本
```

#### 审核列表

```
GET /AUDIT/ALL
← data:   [
            {
              "id": 1,
              "movie_id": 1,
              "movie_name": "Inception",
              "resouce_name": "4K蓝光压制版",
              "quality": "4K",
              "size": "22.4GB",
              "url": "magnet:...",
              "submitter": "user123",
              "submitter_id": 5,
              "note": "备注信息",
              "status": "pending",     // "pending" | "approved" | "rejected"
              "review_msg": "",
              "createTime": "2024-03-20T14:30:00"
            },
            ...
          ]
```

#### 审核操作

```
POST /AUDIT/APPROVE?id=1
→ Query:  id=1
← 响应:   纯文本 "审核通过"

POST /AUDIT/REJECT?id=2&reason=广告链接
→ Query:  id=2, reason=广告链接（URL编码）
← 响应:   纯文本 "已拒绝"
```

---

## 五、前端数据存储

### 5.1 localStorage（持久化，纯字符串键值对）

| Key | 值示例 | 类型 | 设置时机 |
|---|---|---|---|
| `token` | `"eyJhbGciOiJIUzI1NiJ9..."` | String (JWT) | 登录成功 |
| `username` | `"admin"` | String | 登录成功 |
| `darkMode` | `"true"` / `"false"` | String | 设置页切换 |
| `similarMoviesEnabled` | `"true"` / `"false"` | String | 设置页切换 |
| `autoPlayNext` | `"true"` / `"false"` | String | 设置页切换 |

> **注意：localStorage 只能存字符串。** 虽然 API 返回的是 JSON，但存到 localStorage 的是 JSON 中的具体字段值（字符串），而非整个 JSON 对象。

### 5.2 JavaScript 内存变量（页面级，刷新丢失）

| 变量 | 类型 | 数据来源 |
|---|---|---|
| `movies[]` | Array\<Object\> | `resp.json()` 解析后的数组 |
| `currentPage` | Number | 初始值 1，翻页时更新 |
| `hasNextPage` | Boolean | `movies.length === PAGE_SIZE` |
| `filters { type, year, region, language, sort }` | Object | 用户点击筛选按钮填入 |
| `pendingScores[]` | Array\<{li, score}\> | 评分异步返回后暂存 |
| `resourceMovies{}` | Object\<Number, Array\> | `/FILMES/RESOURCE/ONE` 返回的 JSON 缓存 |
| `allResources[]` | Array\<Object\> | `/PLAY/RESOURCES` 返回的 JSON |

---

## 六、前端数据展示 — 从 JSON 到 DOM 的过程

### 6.1 通用流水线

```
fetch(url, { headers: { token } })
  .then(res  => res.json())          // JSON → JavaScript 对象
  .then(json => {
       var list = Array.isArray(json)
                    ? json            // 直接是数组
                    : json.data;      // 或包在 { data: [...] } 里
       list.forEach(renderItem);      // 遍历渲染
  })
  .catch(err => showError());         // 网络/解析失败
```

### 6.2 电影卡片渲染：JSON 字段到 DOM 的映射

```javascript
// 入参 movie 对象来自 API 返回的 JSON
function renderMovie(movie) {
    // movie.id          → <a href="./movie-details.html?id=1">
    // movie.cover       → getCoverUrl() → <img src="...">
    // movie.name        → escapeHtml()  → <h3 class="card-title">
    // movie.releaseDate → .substring(0,4) → "2024"
    // movie.region      → "美国"
    // movie.type        → "科幻"
    //                     → 拼接为 "2024 / 美国 / 科幻" → <p class="card-meta">
    // movie.duration    → "148min" → <span class="star-count">

    // 异步补评分:
    // fetch(/FILMES/SCORE/ONE?id=) → [ { score:8.8 } ]
    // scores[0].score.toFixed(1)   → "8.8" → <span class="card-score">
}
```

### 6.3 表格渲染：JSON 数组到 HTML 表格

```javascript
// 入参 movies[] 来自 fetchMovies() 中 resp.json() 的结果
function renderTable(movies) {
    tbody.innerHTML = movies.map(m => `
        <tr>
            <td>${m.id}</td>                           // Number → 直接显示
            <td><img data-cover="${m.cover}"></td>      // String → 延迟调用setLocalImage
            <td>${m.name}</td>                          // String
            <td>${m.director}</td>                      // String
            <td>${m.actors}</td>                        // String
            <td>${m.type}</td>                          // String
            <td>${m.region}</td>                        // String
            <td>${m.language}</td>                      // String
            <td>${m.releaseDate}</td>                   // String (日期)
            <td>${m.duration}</td>                      // Number → 分钟
            <td>${m.description}</td>                   // String (可能很长)
            <td>
                <button onclick="openEditModal(${m.id})">编辑</button>
                <button onclick="deleteMovie(${m.id})">删除</button>
                <button onclick="openResourceModal(${m.id})">资源</button>
            </td>
        </tr>
    `).join('');
}
```

### 6.4 三种 JSON→DOM 渲染模式对比

| 模式 | JSON来源 | DOM创建方式 | 使用场景 |
|---|---|---|---|
| **卡片流** | `/FILMES/ONEP` 等分页接口 | `createElement('li')` + `innerHTML` 模板字符串 | 用户端首页/列表页 |
| **表格行** | 同上 | `innerHTML` + `map().join('')` 批量生成 | 管理后台表格 |
| **详情填充** | `/FILMES/ONEID` 单条 | `document.getElementById().textContent =` | 电影详情页 |

---

## 七、封面图片路径的 JSON→URL 转换

封面字段 `movie.cover` 在 JSON 中的值有3种情况：

| JSON 中的值 | getCoverUrl() 处理 | 最终 `<img src>` |
|---|---|---|
| `"https://img.example.com/poster.jpg"` | 正则匹配 `https?://` → 直接返回 | `https://img.example.com/poster.jpg` |
| `"assets/images/movie-1.png"` | 检测到 `assets/` → 补页面路径前缀 | `./assets/images/movie-1.png` |
| `"resource/cover/Scream_7.png"` | 检测到 `resource/` → 补前缀 | `./resource/cover/Scream_7.png` |
| `null` / `""` / `undefined` | 空值判断 → 返回默认图 | `./assets/images/movie-1.png` |

---

## 八、特殊传输场景（非 JSON）

| 场景 | 传输格式 | 原因 |
|---|---|---|
| **视频流** `/PLAY/STREAM` | 二进制 Blob | 视频文件无法用JSON表示 |
| **电影封面上传** `/FILMES/ADD` | `multipart/form-data` | 文件 + JSON 元数据的组合 |
| **删除/审核确认** 响应 | 纯文本 String | 后端直接返回中文提示，前端 `resp.text()` |

---

## 九、错误处理策略

| 场景 | JSON 响应 | 前端处理 |
|---|---|---|
| **登录失败** | `{ "code": 401, "msg": "用户名或密码错误" }` | `alert(data.msg)` |
| **Token 过期** | HTTP 401 (无 JSON body) | 清除 localStorage → 跳转登录 |
| **数据为空** | `[]` 或 `{ "data": [] }` | 显示"暂无数据" |
| **网络断开** | fetch 抛出 TypeError | `.catch()` → 显示"加载失败" |
| **图片404** | 非 JSON，`<img>` 加载失败 | `img.onerror` → 替换默认封面 |
| **Python 服务未启动** | fetch 失败 | 显示"请先启动 Python 服务 (port 8085)" |

---

## 十、关键设计模式总结

1. **全 JSON 通信** — 除视频流和文件上传外，所有请求/响应均为 JSON，`Content-Type: application/json`
2. **统一响应包装** — 后端 `ResonseResult { code, msg, data }` 让前端只需一套判断逻辑
3. **IIFE 封装** — `(function() { ... })()` 避免页面级变量污染全局
4. **先渲染后补分** — 卡片立即渲染，评分异步 `resp.json()` 后回填，不阻塞首屏
5. **JSON→DOM 直接映射** — `movie.name → <h3>.textContent`, `movie.cover → <img>.src`
6. **localStorage 只存简单值** — token、username、开关状态，不存复杂 JSON 结构
7. **前端兼容两种 JSON 形状** — 同时支持 `[...]` 和 `{ data: [...] }` 两种返回格式
8. **封面路径三层兜底** — 远程URL > 本地路径规范化 > 默认占位图 > `onerror` 兜底
