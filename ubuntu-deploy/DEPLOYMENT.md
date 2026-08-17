# movie_show_web 部署手册

> 本文档记录 `movie_show_web`（Spring Boot 电影分享平台）在 Ubuntu 服务器上的完整部署流程与 CI/CD 配置。所有密码/密钥用占位符代替，填你自己的真实值。

## 架构总览

```
本机（Windows，开发环境）
├── MySQL 8：security 库（真实数据源）
└── 项目源码（git 仓库，推到 GitHub）

Ubuntu 服务器（腾讯云 106.52.8.154，生产）
├── MySQL（systemd，本机原生）
├── Redis（systemd，本机原生）
├── Spring Boot jar → systemd 服务 security
├── nginx → 80 端口对外，反代 8080 + 静态前端
└── Git 仓库克隆在 /opt/security
```

### 部署流程图

```
本地 git push → GitHub Actions 自动构建 jar
             → scp 上传 jar + 前端三目录到服务器 ~/incoming
             → 执行 deploy.sh（备份旧 jar → 替换 → 同步前端 → systemctl restart security）
             → nginx 直接读 /opt/security/src/main/resources 前端文件，无需重启
```

---

## 一、服务器初始化

### 1. 环境要求

```bash
java -version   # JDK 21+（本项目实测 Java 25 也能跑，有 warning 但不影响）
mvn -version    # Maven 3.8+
systemctl status mysql redis-server   # MySQL/Redis 运行中
```

### 2. 项目目录

仓库克隆在 `/opt/security`：

```bash
git clone <你的仓库地址> /opt/security
```

### 3. 环境变量文件

**手动运行**时读的是终端环境变量；**systemd 服务**读 `/etc/security/security.env`。

```bash
sudo mkdir -p /etc/security
sudo tee /etc/security/security.env > /dev/null <<'EOF'
DB_USERNAME=root
DB_PASSWORD=你的MySQL密码
# DEEPSEEK_API_KEY=...   # AI 功能类目前被注释，暂不需要
# QWEN_API_KEY=...
EOF
```

**本机（Windows PowerShell）永久设置：**

```powershell
setx DB_USERNAME "root"
setx DB_PASSWORD "你的MySQL密码"
```

**Ubuntu 手动跑 jar 时设置（写进 ~/.bashrc 避免新终端丢失）：**

```bash
echo 'export DB_USERNAME=root' >> ~/.bashrc
echo 'export DB_PASSWORD=你的MySQL密码' >> ~/.bashrc
source ~/.bashrc
```

---

## 二、MySQL 关键修复（踩坑记录）

### 1. root 认证插件问题

Ubuntu 的 MySQL root 默认用 `auth_socket` 插件，**命令行能连（走 socket），jar 连不上（走 TCP 3306）**。

判断方法：
```bash
mysql -uroot -proot -e "SELECT 1;"          # socket，可能能过
mysql -h127.0.0.1 -uroot -proot -e "SELECT 1;"  # TCP，被拒 = 认证插件问题
```

修复（改成密码认证，TCP 也能连）：
```bash
mysql -uroot -proot -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root'; FLUSH PRIVILEGES;"
```

### 2. security 库迁移

**服务器 MySQL 默认没有 `security` 库，数据在本机。** 必须导出导入：

```bash
# 服务器：创建库
mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS security CHARACTER SET utf8mb4;"
```

```powershell
# 本机 PowerShell：导出
mysqldump -uroot -proot --default-character-set=utf8mb4 security > security_dump.sql
# 本机：传到服务器
scp security_dump.sql root@106.52.8.154:/opt/security/
```

```bash
# 服务器：导入（用服务器 CLI，不要用 DataGrip 远程导，权限会受限）
mysql -uroot -proot security < /opt/security/security_dump.sql
```

验证：
```bash
mysql -uroot -proot -e "USE security; SHOW TABLES;" | head -30
```

> DataGrip 远程连的是 `root@'%'`，建库/导表权限不足是正常的，**数据库管理操作一律在服务器 SSH 里做**。若确实需要远程操作：
> ```bash
> mysql -uroot -proot -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION; FLUSH PRIVILEGES;"
> ```

---

## 三、构建与手动运行

```bash
cd /opt/security
mvn package -Dmaven.test.skip=true   # 跳过测试编译（项目有引用已注释类的坏测试文件）

# 产物在 target/springboot-demo-1.0.0.jar
```

手动跑（带环境变量）：
```bash
export DB_USERNAME=root
export DB_PASSWORD=你的MySQL密码
cd /opt/security/target
java -jar springboot-demo-1.0.0.jar
```

成功标志：`Started SpringbootApplication in X.XX seconds`，端口 8080。

**停止手动进程：**
```bash
ss -tlnp | grep 8080   # 查 PID
kill <PID>
# 或
pkill -9 -f springboot-demo-1.0.0.jar
```

---

## 四、systemd 服务（推荐常驻方式）

服务文件：仓库 `ubuntu-deploy/security.service`（已改为 `User=root`，读 `/etc/security/security.env`）。

```bash
sudo cp /opt/security/ubuntu-deploy/security.service /etc/systemd/system/security.service
sudo cp /opt/security/ubuntu-deploy/deploy.sh /opt/security/deploy.sh
sudo chmod +x /opt/security/deploy.sh
sudo cp /opt/security/target/springboot-demo-1.0.0.jar /opt/security/springboot-demo-1.0.0.jar
sudo systemctl daemon-reload
sudo systemctl enable security
sudo systemctl start security
```

常用命令：
```bash
sudo systemctl status security      # 状态
sudo journalctl -u security -f      # 实时日志（相当于控制台）
sudo systemctl restart security     # 重启
sudo systemctl stop security        # 停止
```

> ⚠️ **不要同时手动 `java -jar` 和 systemd 服务**，会抢 8080 端口。

---

## 五、nginx 配置

### 端口占用排查（重要踩坑）

8080 可能被**其他服务**占用，尤其是 `xhhbot.service`（另一个项目）：
```bash
ss -tlnp | grep 8080
```
曾因 xhh-bot 自动重启反复抢 8080，最终永久禁用：
```bash
sudo systemctl stop xhhbot.service
sudo systemctl disable xhhbot.service
```

### 安装配置

完整配置在仓库 `ubuntu-deploy/nginx.conf`，是 **Ubuntu 默认 nginx.conf + server 块内嵌在 http{} 里**。

```bash
sudo apt install -y nginx
scp ubuntu-deploy/nginx.conf root@106.52.8.154:/etc/nginx/nginx.conf   # 本机上传
sudo nginx -t
sudo systemctl restart nginx
```

关键点：
- `server` 块必须放在 `http { }` **内部**，放顶层会报 `"server" directive is not allowed here`
- 已注释 `include /etc/nginx/sites-enabled/*;` 避免与默认站点冲突
- 前端路径指向 `/opt/security/src/main/resources/`（git 仓库内的前端目录），**不需要单独拷贝**
- 前端三目录：`filmlane-master`（主站）、`adminkit-web-ui-kit-dashboard-template`（后台）、`live2d-example-master`（插件）

验证：
```bash
curl -I http://localhost/    # 应返回 200
```

---

## 六、CI/CD（GitHub Actions）

### 工作流程

`push 到 master` → `.github/workflows/deploy.yml` 自动执行：
1. JDK 21 构建 `mvn package -Dmaven.test.skip=true`
2. `appleboy/scp-action` 上传 jar + 前端三目录到 `~/incoming`
3. `appleboy/ssh-action` 执行 `bash /opt/security/deploy.sh`

`deploy.sh` 逻辑：备份旧 jar → 替换新 jar → 同步前端到 `/opt/security/src/main/resources` → `systemctl restart security`。

### GitHub Secrets 配置

仓库 `Settings → Secrets and variables → Actions` 添加：

| Secret | 值 |
|---|---|
| `SERVER_HOST` | `106.52.8.154` |
| `SERVER_USER` | `root` |
| `SERVER_PORT` | `22` |
| `SERVER_SSH_KEY` | 部署私钥内容 |

### 生成部署 SSH 密钥

```powershell
# 本机
ssh-keygen -t ed25519 -f C:\Users\24405\.ssh\server_deploy_key -N ""
Get-Content C:\Users\24405\.ssh\server_deploy_key.pub
```

```bash
# 服务器：把公钥加入 root 的 authorized_keys
mkdir -p ~/.ssh && chmod 700 ~/.ssh
echo "你的公钥内容" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

### 日常更新

```bash
git add .
git commit -m "xxx"
git push origin master
```

Actions 自动构建部署，无需手动操作。

---

## 七、域名与 DNS

- 服务器 IP：`106.52.8.154`
- 域名：`ciaohello.icu`（注意：不是 `ciaohell.icu`）
- 腾讯云解析面板加 A 记录：`@` → `106.52.8.154`、`www` → `106.52.8.154`

排查：
```powershell
nslookup ciaohello.icu 8.8.8.8   # 公网 DNS 验证（绕过校园网缓存）
```

> 之前域名曾指向校园网 frp（`ciaohello.icu.hnist.cn` → `211.69.224.176`），改到服务器 IP 后注意 DNS 传播时间（TTL 600s）和本地缓存（`ipconfig /flushdns`）。

---

## 八、远程访问安全

- 对外只开 80（nginx），8080 不要对公网（避免绕过 nginx 直连后端）
- 腾讯云**安全组**：放行 80 入站
- 服务器防火墙：`sudo ufw allow 80/tcp`

---

## 九、验证清单

| 项目 | 命令 | 期望 |
|---|---|---|
| jar 启动 | `java -jar ...` | `Started SpringbootApplication` |
| systemd | `systemctl status security` | `active (running)` |
| nginx | `curl -I http://localhost/` | `200 OK` |
| 远程访问 | 浏览器打开 `http://ciaohello.icu` | 网页正常显示 |
| 登录 | 站点登录 | 无 Access denied / 库错误 |
| CI/CD | push 后看 GitHub Actions | 全绿 |
