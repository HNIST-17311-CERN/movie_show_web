# Ubuntu 服务器部署方案（电影俱乐部 Movie Club）

> 目标：将 Spring Boot 影视平台 + 图识电影 Python 服务部署到一台全新的 Ubuntu 服务器。
> 本文档为**需求分析与部署规划**，未执行任何命令。

---

## 1. 项目部署架构

```
                        ┌────────────────────────────────────────────┐
                        │            Ubuntu 服务器                    │
                        │                                            │
 浏览器 ──HTTP/HTTPS──► │  Nginx (:80/:443)                          │
                        │   ├─ /        → 静态前端 + API (:8080)     │
                        │   └─ /movie/  → Python 图识服务 (:8085)    │
                        │                                            │
                        │  Spring Boot 应用 (:8080)                  │
                        │   ├─ 静态资源: filmlane-master / adminkit  │
                        │   ├─ 上传目录: uploads/files/               │
                        │   └─ 调用 → http://localhost:8085/movie/*   │
                        │                                            │
                        │  MySQL (:3306) 库: security                │
                        │  Redis (:6379) 会话缓存 login:{userid}      │
                        │  Python 图识服务 (:8085)                    │
                        └────────────────────────────────────────────┘
```

**组件清单**

| 组件 | 端口 | 版本要求 | 说明 |
|---|---|---|---|
| Nginx | 80 / 443 | 任意 1.18+ | 反向代理 + 可选 HTTPS |
| Spring Boot 应用 | 8080 | Java 21 | 主应用，前端页面内嵌 |
| Python 图识服务 | 8085 | Python 3.10+ | `/movie/recognize` + `/movie/similar`，**独立项目，不在本仓库** |
| MySQL | 3306 | 8.0+ | 库名 `security` |
| Redis | 6379 | 7.0+ | 登录态缓存 |
| Maven | — | 3.6.3+ | 仅源码构建方式需要 |

> ⚠️ **关键前置确认**：图识电影 Python 服务（监听 8085，含 `/movie/recognize`、`/movie/similar`）
> 是**独立代码库**，不在当前仓库中。部署前需准备好该项目的源码或可运行产物，
> 否则图识电影功能不可用（不影响主应用其他功能）。

---

## 2. 部署前代码改造清单（必做）

部署到 Linux 前必须修复以下问题，否则应用无法启动或功能异常。

### 2.1 【必须】MyBatis mapper 路径大小写不匹配（会导致启动失败）

- 现状：`application.yml` 声明 `classpath:mapper/*.xml`（小写），
  实际目录是 `src/main/resources/Mapper/`（大写）。
- Windows/macOS 大小写不敏感不报错，**Linux 下会直接报 `Invalid bound statement` / 找不到 XML**。
- 修复二选一：
  - A. 把 `resources/Mapper/` 目录改名为 `resources/mapper/`
  - B. 把 `application.yml` 中改为 `classpath:Mapper/*.xml`

### 2.2 【必须】前端硬编码 `http://localhost:8080`

前端页面（`filmlane-master/*.html`、`adminkit/**/static/*.html`）约 **60 处** 硬编码了
`http://localhost:8080`，用户浏览器访问将无法生效。

- 建议：全局替换 `http://localhost:8080` → 空字符串（同源相对路径，前端由本应用静态服务，
  同源最稳）。涉及文件包括：
  - 用户端：`index.html`、`search.html`、`All_movie.html`、`TV_series.html`、
    `Net_movie.html`、`movie-details.html`、`Online.html`、`JieSuo.html`、
    `messages.html`、`recommend-manage.html`、`WinXP.html`
  - 管理端：`static/index.html`、`pages-*.html`（`pages-audit`、`pages-blank`、
    `pages-message-audit`、`pages-message-manage`、`pages-recommend-*`、`pages-roles`、
    `pages-sign-in`、`pages-users`）
  - 注意 `readme-images/` 下的演示页无需改

### 2.3 【必须】前端调用 Python 服务的地址

- `movie-details.html:512` 硬编码 `http://localhost:8085/movie/similar`（浏览器直连）。
- 修复：改为经 Nginx 反代路径（如 `/movie/similar`），或改成服务器公网域名/IP。

### 2.4 【建议】后端 Python 地址改配置化

- `ImageSearchService.java:21` 硬编码 `http://localhost:8085/movie/recognize`。
- 建议改为从 `application.yml` 读取（如 `app.python-service.url`），便于日后更换服务器。

### 2.5 【建议】JWT 密钥固定化

- 现状：`JWT_Utils` 启动时 `Keys.secretKeyFor(HS256)` 随机生成，**每次重启所有登录态失效**。
- 建议：改为从配置/环境变量读取固定密钥（如 `app.jwt.secret`），生产环境必备。

### 2.6 【建议】启用密码加密（安全加固）

- 现状：`SecurityConfig` 中 `PasswordEncoder` 已注释，**密码明文存储/比较**。
- 建议：启用 `BCryptPasswordEncoder`，并同步用 BCrypt 哈希重新初始化 `users` 表密码。
- 注意：此项会改变登录校验方式，需在部署验证时重点回归登录功能。

### 2.7 【建议】删除/迁移无用内容

- `Movie_Online/`（空目录，视频已移除）无需部署。
- RAG/AI 管线（`org.example.AI`）全部注释，不参与编译，无需 Milvus。
- `DEEPSEEK_API_KEY` / `QWEN_API_KEY` 当前未使用，可留空。

---

## 3. 服务器基础要求

| 项目 | 要求 |
|---|---|
| OS | Ubuntu 22.04 LTS / 24.04 LTS（推荐 24.04，自带 OpenJDK 21 / Redis 7 / MySQL 8） |
| 硬件 | 最低 2C4G；建议 4C8G（含图识服务，深度学习模型推理较吃内存） |
| 磁盘 | 30GB+，`uploads/files/` 按影片/附件量预留 |
| 公网端口 | 入站仅需 80 / 443（22 用于 SSH）；8080/8085/3306/6379 **不对公网开放** |

---

## 4. 服务器基础环境安装

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装基础工具
sudo apt install -y curl wget git vim ufw

# 安装 JDK 21（Ubuntu 24.04 自带；22.04 需 PPA 或 Temurin）
sudo apt install -y openjdk-21-jdk
java -version          # 确认 21.x

# 安装 Maven（仅"源码构建"方式需要）
sudo apt install -y maven
mvn -version           # 确认 >= 3.6.3

# 安装 MySQL
sudo apt install -y mysql-server
sudo systemctl enable --now mysql

# 安装 Redis
sudo apt install -y redis-server
sudo systemctl enable --now redis

# 安装 Nginx
sudo apt install -y nginx
sudo systemctl enable --now nginx

# 安装 Python（Ubuntu 24.04 自带 3.12）
sudo apt install -y python3 python3-venv python3-pip
```

---

## 5. 数据库部署

### 5.1 从开发机导出 schema 与数据

> ⚠️ 仓库内 `datagrip.txt` **只包含 movie 与 media_episodes 两张表**，不完整。
> 完整表结构需从开发环境的 MySQL 导出：`users`、`user_roles`、`role_permissions`、
> `permissions`、`movie`、`movie_resource`、`movie_score`、`movie_episodes`、
> `movie_play_source`、`resource_submissions`、`messages`、`operation_log`、
> `home_*_recommend`、`richtext_docs`、`doc_files` 等。

```bash
# 在【开发机】执行，导出到 dump 文件
mysqldump -u root -p --routines --triggers --single-transaction security > security_dump.sql

# 将 security_dump.sql 上传至服务器（scp / rsync），放入 /opt/security/
```

### 5.2 服务器建库并导入

```bash
sudo mysql -e "CREATE DATABASE IF NOT EXISTS security DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
sudo mysql -e "CREATE USER IF NOT EXISTS 'security'@'localhost' IDENTIFIED BY 'STRONG_PASSWORD';"
sudo mysql -e "GRANT ALL PRIVILEGES ON security.* TO 'security'@'localhost'; FLUSH PRIVILEGES;"

# 导入
mysql -u security -p'STRONG_PASSWORD' security < /opt/security/security_dump.sql

# 验证
mysql -u security -p'STRONG_PASSWORD' security -e "SHOW TABLES;"
```

> 应用连接用的是 `application.yml` 中 `DB_USERNAME` / `DB_PASSWORD` 环境变量，
> 生产建议创建专用低权限账号（仅 `security` 库），不要用 root。

---

## 6. 后端应用构建与部署

### 6.1 选择构建方式

| 方式 | 适用场景 |
|---|---|
| **A. 本地构建 jar 上传**（推荐） | 服务器不装 Maven/不拉代码，交付物单一 |
| B. 服务器源码构建 | 需装 Maven、Java，拉取仓库后 `mvn package` |

### 6.2 方式 A：本地构建并上传

```bash
# 在【开发机】项目根目录执行（已含 2.x 代码修复）
mvn clean package -DskipTests
# 产物: target/springboot-demo-1.0.0.jar

# 上传到服务器
scp target/springboot-demo-1.0.0.jar user@SERVER_IP:/opt/security/
```

### 6.3 服务器目录规划

```bash
sudo mkdir -p /opt/security
sudo mkdir -p /opt/security/uploads/files        # 文件上传目录（相对路径 working dir 用）
# 如开发环境已有上传文件，一并拷贝：
# scp -r uploads/* user@SERVER_IP:/opt/security/uploads/
```

### 6.4 环境变量文件（/etc/security.env）

```ini
DB_USERNAME=security
DB_PASSWORD=STRONG_PASSWORD
# RAG 未启用，可留空；启用时再填
DEEPSEEK_API_KEY=
QWEN_API_KEY=
```

> 权限收紧：`sudo chmod 600 /etc/security.env`

### 6.5 systemd 服务（/etc/systemd/system/security-app.service）

```ini
[Unit]
Description=Movie Club Spring Boot App
After=network.target mysql.service redis-server.service
Wants=mysql.service redis-server.service

[Service]
User=www-data
Group=www-data
WorkingDirectory=/opt/security
EnvironmentFile=/etc/security.env
ExecStart=/usr/bin/java -Xms512m -Xmx2g -jar /opt/security/springboot-demo-1.0.0.jar
SuccessExitStatus=143
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
sudo chown -R www-data:www-data /opt/security
sudo systemctl daemon-reload
sudo systemctl enable --now security-app
sudo systemctl status security-app
# 查看日志
sudo journalctl -u security-app -f
```

---

## 7. Python 图识服务部署（:8085）

> 需要独立项目源码（**不在本仓库**）。以下为通用流程。

```bash
# 假设上传到 /opt/python-movie-recognize/
cd /opt/python-movie-recognize
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt        # 具体依赖以该项目 requirements 为准

# 若依赖本地模型权重，确认模型文件已就位
```

systemd 服务（`/etc/systemd/system/movie-recognize.service`）：

```ini
[Unit]
Description=Movie Recognize Python Service
After=network.target

[Service]
User=www-data
WorkingDirectory=/opt/python-movie-recognize
ExecStart=/opt/python-movie-recognize/.venv/bin/python app.py --port 8085
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now movie-recognize
# 本机自测（后端是 localhost 调用，不走公网）
curl -X POST http://localhost:8085/movie/recognize -F "image=@/tmp/test.jpg"
```

---

## 8. Nginx 反向代理

### 8.1 站点配置（/etc/nginx/sites-available/security.conf）

```nginx
server {
    listen 80;
    server_name your-domain.com;   # 或服务器公网 IP

    client_max_body_size 50m;

    # 主应用：静态前端 + API 统一走 8080
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 视频流播放需支持 Range（断点续播）
    location /PLAY/STREAM {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_buffering off;
        proxy_request_buffering off;
    }

    # Python 图识服务（前端同源调用）
    location /movie/ {
        proxy_pass http://127.0.0.1:8085;
        proxy_set_header Host $host;
        proxy_read_timeout 120s;    # 推理耗时较长，放宽超时
    }
}
```

```bash
sudo ln -s /etc/nginx/sites-available/security.conf /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 8.2 可选 HTTPS（推荐）

使用 certbot：

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
# 自动续期
sudo certbot renew --dry-run
```

> 若启用 HTTPS，前端相对路径自动走 https，无需二次改动。

---

## 9. 防火墙与安全

```bash
# 仅开放必要端口
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'      # 80 + 443
sudo ufw enable
sudo ufw status

# 8080/8085/3306/6379 只监听本机回环，不要对公网开放
# application.yml 中 server.address 为 "::"，Nginx 反代走 127.0.0.1 即可
```

**MySQL / Redis 加固**：
- MySQL：root 设强密码，`GRANT` 只授 `security.*`，必要时绑定 `127.0.0.1`。
- Redis：绑定 `127.0.0.1`（默认），建议设置 `requirepass`（需同步改 `RedisConfig`）。

---

## 10. 启动顺序与验收

### 10.1 启动顺序

1. MySQL 正常（`systemctl status mysql`）
2. Redis 正常（`redis-cli ping` → `PONG`）
3. Python 图识服务正常（`curl localhost:8085/...`）
4. Spring Boot 应用（`journalctl -u security-app -f` 看到 `Started SpringbootApplication`）
5. Nginx 正常（`nginx -t` + 公网访问）

### 10.2 验收清单

| # | 验证项 | 命令 / 操作 |
|---|---|---|
| 1 | 应用已启动 | `curl -s http://localhost:8080/api/hello -H "token: x"`（未登录应返回 401） |
| 2 | 首页可访问 | 浏览器 `http://SERVER_IP/` 出现电影列表 |
| 3 | 登录 | `curl -X POST http://localhost:8080/api/user/login -d '{"username":"...","password":"..."}' -H "Content-Type: application/json"` |
| 4 | 电影接口 | `curl http://localhost:8080/FILMES/ALL`（带 token，需 `movie:view`） |
| 5 | 视频播放 | 打开 `Online.html`，验证 Range/拖动进度 |
| 6 | 图识电影 | 管理端/`JieSuo.html` 上传图片，验证走 `/movie/` 反代到 8085 |
| 7 | 文件上传 | 上传附件，确认写入 `/opt/security/uploads/files/YYYY-MM/` |
| 8 | 后台管理 | 登录 AdminKit，验证电影增删改查、留言审核、日志 |
| 9 | 重启持久化 | `systemctl restart security-app` 后登录态是否保留（取决于 2.5 是否已固定密钥） |
| 10 | HTTPS | `curl -I https://your-domain.com` 返回 200 |

---

## 11. 运维

### 11.1 常用命令

```bash
sudo systemctl status security-app          # 应用状态
sudo journalctl -u security-app -f          # 应用日志
sudo journalctl -u movie-recognize -f       # Python 服务日志
sudo systemctl status mysql redis nginx
```

### 11.2 数据库备份（cron 每日）

```bash
# /etc/cron.d/security-backup
0 3 * * * root mysqldump -u security -p'STRONG_PASSWORD' --single-transaction --routines security | gzip > /var/backups/security_$(date +\%Y\%m\%d).sql.gz
```

同时备份 `/opt/security/uploads/`（附件与视频资源）。

### 11.3 应用升级流程

1. 开发机修复/改造 → `mvn package -DskipTests`
2. 上传新 jar 到 `/opt/security/springboot-demo-1.0.0.jar`（先备份旧 jar）
3. `sudo systemctl restart security-app`
4. 按 10.2 抽查核心接口

### 11.4 回滚

```bash
# 保留上一个版本 jar，改回路径后重启即可
sudo cp /opt/security/backup/springboot-demo-1.0.0.jar.bak /opt/security/springboot-demo-1.0.0.jar
sudo systemctl restart security-app
```

---

## 12. 常见故障排查

| 现象 | 原因 | 解决 |
|---|---|---|
| 应用启动失败 `Invalid bound statement` | mapper 路径大小写（2.1） | 改 yml 或目录名，重新打包 |
| 前端接口 404 / 报错 | localhost 硬编码（2.2） | 全局替换为相对路径后重新打包 |
| 登录后重启即失效 | JWT 随机密钥（2.5） | 固定 JWT secret |
| 图识电影失败 | Python 服务未启动 / 反代未配置 | 检查 8085 服务与 `/movie/` 代理、超时 |
| 上传 500 | uploads 目录无写权限 | `chown -R www-data:www-data /opt/security` |
| 视频无法拖动 | Range 被 Nginx 缓冲 | 关闭 `proxy_buffering`（见 8.1） |
| 端口占用 | 默认 `server.address "::"` 双栈 | 确认无冲突，或绑定 `127.0.0.1` |

---

## 13. 待用户确认事项

- [ ] Python 图识服务项目源码/产物在何处？模型依赖是否就绪？
- [ ] 使用哪个域名或公网 IP？是否配置 HTTPS？
- [ ] 数据库完整导出文件（`security_dump.sql`）由开发机生成，是否可执行？
- [ ] 是否接受启用 BCrypt 密码加密（需重建用户密码哈希）？
- [ ] 部署方式偏好：源码构建 or 本地打包 jar 上传？
- [ ] 是否需要 Docker Compose 化（替代 systemd + 原生安装）？
