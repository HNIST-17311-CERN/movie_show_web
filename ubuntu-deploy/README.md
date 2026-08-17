# Ubuntu 部署上传清单

本文件夹用于集中存放要传到 Ubuntu 服务器的文件。

## CI/CD 自动部署（GitHub Actions）

push 到 `master` 分支后，Actions 自动构建 jar 并部署。

### 服务器一次性初始化

```bash
# 1. 运行用户 + 目录
sudo useradd -m -s /bin/bash security
sudo mkdir -p /opt/security /etc/security
sudo chown -R security:security /opt/security /etc/security

# 2. 环境变量（密钥不入库）
sudo tee /etc/security/security.env > /dev/null <<'EOF'
DEEPSEEK_API_KEY=your_key_here
QWEN_API_KEY=your_key_here
DB_USERNAME=root
DB_PASSWORD=your_db_password
EOF

# 3. systemd 服务（security.service 上传到 /opt/security/ 后）
sudo cp /opt/security/security.service /etc/systemd/system/security.service
sudo systemctl daemon-reload
sudo systemctl enable security
sudo systemctl start security

# 4. 更新脚本
sudo cp /opt/security/deploy.sh /usr/local/bin/security-deploy
sudo chmod +x /usr/local/bin/security-deploy
```

> 提示：部署用 SSH 用户需要 NOPASSWD 重启服务的权限。可在 `/etc/sudoers.d/security` 加：
> `your_ssh_user ALL=(ALL) NOPASSWD: /bin/systemctl restart security`

### GitHub Secrets（仓库 Settings → Secrets and variables → Actions）

| Secret | 值 |
|---|---|
| `SERVER_HOST` | 服务器公网 IP |
| `SERVER_USER` | SSH 用户名 |
| `SERVER_PORT` | SSH 端口（默认 22） |
| `SERVER_SSH_KEY` | SSH 私钥（公钥加入服务器 `~/.ssh/authorized_keys`） |

日常更新只需 `git push origin master`。数据库表结构变更需手动执行 SQL。

## 文件说明

| 文件 | 服务器目标位置 | 说明 |
|---|---|---|
| `nginx-security.conf` | `/etc/nginx/sites-available/security.conf` | Nginx 站点配置（改 `server_name` 后用） |
| `springboot-demo-1.0.0.jar` | `/opt/security/` | 本地 `mvn package -DskipTests` 构建产物（待生成） |
| `security_dump.sql` | `/opt/security/` | 开发机 `mysqldump` 导出的完整数据库 |

## 静态资源目录（拷到服务器后供 nginx 使用）

```bash
# 服务器上执行
sudo mkdir -p /opt/security/static
# 从本机拷贝以下三个目录到服务器 /opt/security/static/
#   src/main/resources/filmlane-master
#   src/main/resources/adminkit-web-ui-kit-dashboard-template
#   src/main/resources/live2d-example-master
```

## systemd 服务

- Spring Boot：参考 `deployment-ubuntu.md` §6.5，`ExecStart=/usr/bin/java -Xms512m -Xmx2g -jar /opt/security/springboot-demo-1.0.0.jar`
- Python 图识服务：参考 §7（源码不在本仓库）

## 启用步骤

```bash
sudo ln -s /etc/nginx/sites-available/security.conf /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```
