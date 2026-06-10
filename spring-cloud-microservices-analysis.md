# Spring Cloud Alibaba 微服务架构需求分析文档

> 技术栈：Spring Cloud Alibaba 2023.0.1.x + Spring Boot 3.2.x + Java 21  
> 场景落地：电商平台（B2C）

---

## 〇、环境准备清单

开发前需要安装的软件：

### 本地安装（2个）

| 软件 | 版本 | 下载 | 说明 |
|------|------|------|------|
| **JDK** | 21 LTS | `https://adoptium.net/download/` | Eclipse Temurin 推荐 |
| **Docker Desktop** | latest | `https://www.docker.com/products/docker-desktop/` | 中间件全部用 Docker 跑 |

验证安装：
```bash
java -version    # 必须输出 21.x
docker -v        # 必须 24.x+
```

### Docker Compose 中间件（7个，一键启动）

以下 7 个中间件用一个 `docker-compose.yml` 统一管理，一条命令 `docker-compose up -d` 全部启动：

| 中间件 | Docker 镜像 | 端口 | 用途 | 隶属 |
|--------|------------|------|------|------|
| **Nacos** | `nacos/nacos-server:v2.4.0` | 8848 / 9848(gRPC) | 服务注册发现 + 配置中心 | Spring Cloud Alibaba |
| **Sentinel Dashboard** | `bladex/sentinel-dashboard:1.8.8` | 8858 | 熔断/限流可视化管控台 | Spring Cloud Alibaba |
| **Seata Server** | `seataio/seata-server:2.0.0` | 8091 / 7091 | 分布式事务协调器 | Spring Cloud Alibaba |
| **RocketMQ** | `apache/rocketmq:5.2.0` + `rocketmq-console` | 9876 / 10911 / 8080 | 消息队列 + 控制台 | Spring Cloud Alibaba |
| **MySQL** | `mysql:8.0` | 3306 | 业务数据库 + Seata undo_log | 通用 |
| **Redis** | `redis:7.2-alpine` | 6379 | 缓存 / Session / 分布式锁 | 通用 |
| **Elasticsearch** | `elasticsearch:8.12.0` | 9200 | 商品全文搜索 | 通用 |

> 前 4 个是 **Spring Cloud Alibaba 生态核心中间件**，后 3 个是通用基础设施。

### Maven 依赖（pom.xml 核心 BOM）

```xml
<!-- Spring Cloud Alibaba BOM — 统一版本管理 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-alibaba-dependencies</artifactId>
            <version>2023.0.1.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 各模块按需引入的 starter（全部来自 Spring Cloud Alibaba） -->
<dependencies>
    <!-- 1. Nacos 注册 + 配置 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>

    <!-- 2. Sentinel 熔断限流 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    </dependency>

    <!-- 3. Seata 分布式事务 -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
    </dependency>

    <!-- 4. RocketMQ Stream（消息队列） -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-stream-rocketmq</artifactId>
    </dependency>

    <!-- 5. Gateway（Spring Cloud 官方，与 Alibaba 无缝配合） -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- 6. OpenFeign + LoadBalancer（Spring Cloud 官方，Nacos 驱动） -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
    </dependency>

    <!-- 7. Spring Authorization Server（安全认证，官方组件） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-authorization-server</artifactId>
    </dependency>
</dependencies>
```

---

## 一、Spring Cloud 生态研究

### 1.1 生态定位演进

Spring Cloud 正经历从"微服务全家桶"到"云原生协同组件"的范式转型：

| 阶段 | 时间 | 特征 | 核心组件 |
|------|------|------|----------|
| **Netflix OSS 全盛期** | 2015-2020 | 自建全套基础设施，Java 8 + Spring Boot 1.x/2.x | Eureka、Ribbon、Hystrix、Zuul |
| **转型阵痛期** | 2020-2023 | Netflix 组件陆续退役，Alibaba/官方方案补位 | Nacos 替代 Eureka，Sentinel 替代 Hystrix |
| **Alibaba 主导期** | 2023-2026 | Spring Cloud Alibaba 成为新项目事实标准 | Nacos、Sentinel、Seata、RocketMQ |

### 1.2 Netflix OSS vs Spring Cloud Alibaba 对比

Netflix 全线产品已停维或退役，本项目全部采用 Spring Cloud Alibaba 体系：

| 能力域 | Netflix（已退役，不可用） | 本项目选型 |
|--------|--------------------------|------------|
| 服务注册/发现 | Eureka（停维） | **Nacos** — AP/CP 可切换，gRPC 通信，秒级感知 |
| 配置中心 | Config + Bus（分钟级延迟） | **Nacos Config** — 长连接推送，秒级生效，支持灰度/回滚 |
| 负载均衡 | Ribbon（停维） | **Spring Cloud LoadBalancer** + Nacos 权重路由 |
| 服务调用 | Feign | **OpenFeign** — 声明式调用，负载均衡内置 |
| 熔断降级 | Hystrix（2018 停维，线程池重） | **Sentinel** — Netty 非阻塞，QPS 高 60%，Dashboard 可视化管理 |
| 限流 | 无原生方案 | **Sentinel** — QPS/热点参数/集群流控 |
| 分布式事务 | 无 | **Seata** — AT/TCC/Saga 三模式，一个注解 `@GlobalTransactional` |
| 消息驱动 | Stream + RabbitMQ/Kafka | **Spring Cloud Stream + RocketMQ** — 事务消息、延迟消息、死信队列 |
| API 网关 | Zuul 1.x（阻塞 IO，~10K TPS） | **Spring Cloud Gateway** — WebFlux 非阻塞，~50K TPS |
| RPC 备选 | 无 | **Dubbo**（可选，高性能 gRPC 场景） |

### 1.3 2025-2026 关键变革

- **虚拟线程（Project Loom）**：Spring Boot 3.2+ 支持，百万级并发，大多数场景可替代 WebFlux
- **GraalVM 原生镜像**：冷启动 < 50ms，内存降 60-70%，Spring Cloud Alibaba 已提供 Native Hint
- **Spring AI 集成**：LLM 调用内建化，向量数据库自动配置（本项目后续可引入做智能推荐）

---

## 二、Spring Cloud 生态演进图表

```
时间线演进（Spring Cloud Alibaba 视角）：

2017          2019          2021          2023          2025          2026+
 |             |             |             |             |             |
 v             v             v             v             v             v

   Spring Cloud Alibaba 诞生 → 生产验证 → 主导生态 → 云原生协同
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐
│ 2017 开源     │  │ 2019 双11验证 │  │ Netflix退役   │  │ Nacos 3.0 (gRPC)     │
│ Nacos 0.1    │  │ Sentinel 扛住 │  │ Alibaba成标准  │  │ Sentinel 2.0 (Loom)  │
│ Sentinel 0.1 │  │ Seata 落地    │  │ Nacos 2.0 GA  │  │ Seata 2.0 (XA 2.0)   │
│ Dubbo 捐赠   │  │ RocketMQ 成熟 │  │               │  │ Spring AI 内建       │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────────────┘

Java 8             Java 8/11        Java 11/17        Java 21 (LTS)
Spring Boot 1.x    Spring Boot 2.x  Spring Boot 3.x   Spring Boot 3.2+/4.0

Netflix OSS 全盛期  替换过渡期       Alibaba 主导期     云原生融合期
(Eureka/Hystrix)   (Nacos/Sentinel  (全家桶一体化)     (K8s+网格双模)
                    逐步替代)

核心趋势：
  · Netflix OSS → Spring Cloud Alibaba 全家桶
  · 自建中间件 → Docker Compose 一键部署
  · 传统线程池 → 虚拟线程（Project Loom）
  · 纯微服务 → 微服务 + AI（Spring AI）
```

---

## 三、微服务架构设计

### 3.1 总体架构拓扑

```
                          ┌─────────────────────────────────┐
    [Web / App / 小程序]  │        前端层（Vue3 / React）      │
                          └──────────────┬──────────────────┘
                                         │ HTTPS
                          ┌──────────────▼──────────────────┐
                          │   Spring Cloud Gateway (API网关) │
                          │   · 路由转发 · Sentinel限流       │
                          │   · JWT验签 · 跨域 · 灰度发布     │
                          └──────────────┬──────────────────┘
                                         │
        ┌────────────────────────────────┼────────────────────────────────┐
        │                                │                                │
  ┌─────▼──────┐  ┌──────────────┐  ┌─────▼──────┐  ┌──────────────┐      │
  │ Nacos 集群  │  │ OAuth2 认证   │  │ Sentinel   │  │ RocketMQ 集群 │      │
  │·服务注册发现│  │ ·JWT签发/验签 │  │·熔断/限流   │  │·事务/延迟消息 │      │
  │·配置中心    │  │ ·RBAC权限    │  │·Dashboard  │  │·死信队列      │      │
  └─────┬──────┘  └──────┬───────┘  └─────┬──────┘  └──────┬───────┘      │
        │                │                │                │              │
        └────────────────┼────────────────┼────────────────┘              │
                         │                │                               │
              ┌──────────┼────────────────┼──────────────────┐            │
              │          │                │                  │            │
    ┌─────────▼──┐ ┌─────▼───┐ ┌─────────▼──┐ ┌─────────┐  │            │
    │ 用户服务    │ │商品服务  │ │ 订单服务    │ │支付服务  │  │            │
    │User Service│ │Product  │ │Order       │ │Payment  │  │            │
    └──────┬─────┘ └────┬────┘ └──────┬─────┘ └────┬────┘  │            │
           │            │            │            │        │            │
    ┌──────▼─────┐ ┌────▼────┐ ┌─────▼────┐ ┌─────▼────┐   │            │
    │ 营销服务    │ │搜索服务  │ │ 库存服务  │ │ 物流服务  │   │            │
    │Marketing   │ │Search   │ │Inventory │ │Logistic  │   │            │
    └──────┬─────┘ └────┬────┘ └─────┬────┘ └─────┬────┘   │            │
           │            │            │            │        │            │
    ┌──────▼────────────▼────────────▼────────────▼────────▼──┐         │
    │              中间件 & 数据层（全部 Spring Cloud Alibaba 生态）      │
    │  MySQL(业务+Seata undo_log)  Redis(缓存/Session/分布式锁)         │
    │  Elasticsearch(商品搜索)  SkyWalking(链路追踪)                     │
    │  Prometheus + Grafana(监控告警)                                  │
    └─────────────────────────────────────────────────────────────────┘
```

### 3.2 10 个功能模块一览

| # | 模块 | 核心能力 | 技术选型 |
|---|------|----------|----------|
| 1 | **服务注册与发现** | 实例注册/注销、健康检查、元数据管理、权重路由 | Nacos Discovery |
| 2 | **配置中心** | 动态配置、灰度发布、配置回滚、加密存储 | Nacos Config |
| 3 | **API 网关** | 统一入口、路由转发、Token 校验、CORS | Spring Cloud Gateway + Nacos 路由 |
| 4 | **集中授权** | OAuth2.1 认证、JWT 签发/验签、RBAC 权限 | Spring Authorization Server |
| 5 | **服务熔断** | 异常比例熔断、慢调用熔断、Fallback 降级 | Sentinel |
| 6 | **服务限流** | QPS 限流、热点参数限流、网关限流 | Sentinel + Sentinel Dashboard |
| 7 | **消息队列** | 异步削峰、事务消息、延迟消息、死信重试 | RocketMQ + Spring Cloud Stream |
| 8 | **分布式事务** | 跨服务数据一致性、AT/TCC/Saga 模式 | Seata |
| 9 | **支付网关（模拟）** | 支付下单、回调通知、退款、对账 | 自研 + 模拟支付宝 SDK |
| 10 | **可观测性** | 链路追踪、指标采集、日志聚合、告警 | Micrometer + SkyWalking + Grafana |

### 3.3 各模块详细设计

#### 模块1：服务注册与发现（Nacos）

```
工作流程：
  ┌──────────┐  注册(启动时)   ┌──────────────┐  心跳(5s)   ┌──────────┐
  │ 微服务实例 │ ─────────────→ │  Nacos Server │ ←───────── │ 微服务实例 │
  └──────────┘                │  (3节点集群)   │            └──────────┘
                              │               │
  ┌──────────┐  服务发现(订阅)  │  AP/CP 可切换  │  实例变更推送  ┌──────────┐
  │ 调用方    │ ←─────────────→ │  元数据管理    │ ───────────→ │ 调用方    │
  └──────────┘                └──────────────┘            └──────────┘
```

关键配置：
```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: ecommerce-dev        # 环境隔离
        group: DEFAULT_GROUP
        metadata:                       # 元数据（灰度发布用）
          version: v1
          region: beijing
```

- 每个微服务启动时自动向 Nacos 注册
- 健康检查：临时实例心跳 5s，Nacos 主动探测持久实例
- 元数据标签支持灰度发布和就近路由
- 实例上下线实时推送，延迟 < 1s（对比 Eureka 的 30-60s）

#### 模块2：配置中心（Nacos Config）

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        namespace: ecommerce-dev
        file-extension: yaml
        group: DEFAULT_GROUP
        shared-configs:                # 共享配置
          - data-id: common-config.yaml
            group: DEFAULT_GROUP
            refresh: true              # 动态刷新
```

Nacos Config 控制台管理的配置项：

| Data ID | 内容 | 动态刷新 |
|---------|------|----------|
| `gateway-routes.yaml` | 网关路由规则 | ✅ |
| `sentinel-rules.yaml` | 限流/熔断规则 | ✅ |
| `seata-config.yaml` | 分布式事务配置 | ✅ |
| `common-config.yaml` | 通用参数（分页大小等） | ✅ |

- 长连接推送，配置变更秒级生效，无需重启
- 支持配置版本管理和一键回滚
- 支持灰度配置（按 IP / 实例标签定向推送）

#### 模块3：API 网关（Spring Cloud Gateway + Nacos 路由）

```
请求全链路：
  客户端请求
    → Gateway 路由匹配（路由规则来自 Nacos Config）
    → 全局过滤器：JWT验签 → 无效Token返回401
    → Sentinel Gateway 限流 → 超限返回429
    → 转发下游微服务（通过 Nacos Discovery 发现实例）
    → 下游异常 → Sentinel Fallback 降级响应

三层安全防线：
  网关层（第一道）: JWT验证 + IP黑名单 + QPS限流
  服务层（第二道）: @PreAuthorize 权限校验
  数据层（第三道）: 参数化查询 + 数据加密
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: lb://order-service          # lb:// 启用 Nacos 负载均衡
          predicates:
            - Path=/api/order/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter    # Sentinel 网关限流
              args:
                redis-rate-limiter.replenishRate: 500
```

#### 模块4：集中授权（Spring Authorization Server）

```
认证流程（OAuth2.1 授权码模式 + PKCE）：
  1. 用户访问前端 → 前端重定向到 Auth Service /oauth2/authorize
  2. 用户登录（账号密码 / 手机验证码）
  3. Auth Service 签发 JWT：
       Access Token:  RS256 签名，2h 有效期
       Refresh Token: 7d 有效期，可续期
  4. Gateway 全局过滤器拦截所有请求，校验 Token
  5. 下游微服务通过 @PreAuthorize 做细粒度权限

服务间调用（客户端凭证模式）：
  订单服务 → 库存服务：使用 Client ID + Secret 获取 Service Token
```

权限模型（RBAC）：

```
用户(User) → 角色(Role) → 权限(Permission)

示例：
  张三 → 普通用户 → 下单、查看商品、评价
  李四 → 商家     → 管理商品、查看订单、发货
  王五 → 管理员   → 用户管理、系统配置、数据报表
```

关键安全措施：
- JWT RS256 非对称签名（Auth Service 持有私钥，各服务持有公钥）
- Token 黑名单存 Redis（用户登出/管理员踢下线即时生效）
- 支付接口额外做二次签名校验

#### 模块5 & 6：服务熔断与限流（Sentinel）

Sentinel 是 Spring Cloud Alibaba 的核心组件，承担熔断和限流两大职责：

```
Sentinel 规则体系：

  熔断规则（DegradeRule）：
    异常比例:  异常数/总请求数 > 50%（统计窗口10s）→ 熔断
    慢调用比例: 响应时间 > 500ms 的请求 > 60% → 熔断
    熔断恢复:  30s后半开状态，放行1个请求探测，成功则恢复
    Fallback:  读缓存兜底 / 返回友好提示

  限流规则（FlowRule）：
    QPS 限流:     /api/order/create → 500 QPS
    并发线程限流: 同一时刻最多50个线程处理（虚拟线程模式改为逻辑并发数）
    热点参数限流: 热门商品ID → 200 QPS
    链路限流:     特定调用链路限流

  规则存储：
    开发环境 → Sentinel Dashboard 控制台配置，实时生效
    生产环境 → Nacos Config 持久化，重启不丢失
```

```yaml
# Sentinel 接入配置
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8858        # Sentinel Dashboard 地址
        port: 8719                        # 与 Dashboard 通信端口
      datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            data-id: sentinel-rules.yaml  # 规则持久化到 Nacos
            rule-type: degrade            # 熔断规则
```

Sentinel 比 Hystrix 的优势：

| 维度 | Hystrix | Sentinel |
|------|---------|----------|
| 通信模型 | 线程池/信号量（重量级） | Netty 非阻塞（轻量级） |
| 规则下发 | 静态配置，需重启 | Dashboard 实时下发，秒级生效 |
| 限流粒度 | 仅 QPS | QPS + 线程数 + 热点参数 + 链路 |
| QPS 天花板 | ~50K | ~80K+ |
| 维护状态 | 2018 停维 | 持续迭代 |

#### 模块7：消息队列（RocketMQ + Spring Cloud Stream）

```
Topic 规划：

  ORDER_CREATE_TOPIC
    → Producer: 订单服务（创建订单后发送）
    → Consumer: 库存服务（扣库存）、营销服务（核销优惠券）
    → 特性: 事务消息，保证订单创建与库存扣减的一致性

  PAYMENT_NOTIFY_TOPIC
    → Producer: 支付服务（支付回调后发送）
    → Consumer: 订单服务（更新订单状态）、用户服务（发放积分）
    → 特性: 普通消息 + 重试队列

  ORDER_TIMEOUT_TOPIC
    → Producer: 订单服务（创建订单时发送延迟消息）
    → Consumer: 订单服务（30min后检查，未支付则取消）
    → 特性: RocketMQ 延迟消息（level 16 = 30min）

  DLQ（死信队列）
    → 所有消费失败重试3次后进入DLQ
    → 人工介入处理或定时回放
```

```yaml
spring:
  cloud:
    stream:
      rocketmq:
        binder:
          name-server: localhost:9876
        bindings:
          orderCreate-out-0:
            producer:
              group: order-producer-group
              transactional: true     # 事务消息
          orderCreate-in-0:
            consumer:
              group: inventory-consumer-group
              maxAttempts: 3         # 重试3次进DLQ
```

RocketMQ 选型理由（对比 RabbitMQ / Kafka）：

| 特性 | RabbitMQ | Kafka | RocketMQ |
|------|----------|-------|----------|
| 事务消息 | ❌ | ❌ (KIP-98 未 GA) | ✅ 原生支持 |
| 延迟消息 | 插件实现 | ❌ | ✅ 18个等级（1s-2h） |
| 顺序消息 | 单队列有序 | 分区有序 | ✅ 分区有序 |
| 阿里生态整合 | 无 | 无 | ✅ Nacos/Sentinel 联动 |
| 中文社区 | 弱 | 中 | 强 |

#### 模块8：分布式事务（Seata）

```
场景：用户下单  →  扣库存  →  核销优惠券  →  扣余额

  ┌─────────────────────────────────────────────────┐
  │              Seata Server（TC 事务协调器）         │
  │         协调全局事务：Begin → Commit / Rollback    │
  └────┬──────────────┬──────────────┬──────────────┘
       │              │              │
  ┌────▼─────┐  ┌─────▼────┐  ┌─────▼────┐
  │ 订单服务  │  │ 库存服务  │  │ 营销服务  │
  │ （TM）   │  │ （RM）   │  │ （RM）   │
  │开启全局事务│  │ 扣减库存  │  │ 核销优惠券 │
  └──────────┘  └──────────┘  └──────────┘

AT 模式（默认，适合90%场景）：
  · Seata 自动代理 SQL，生成 UNDO_LOG（反向SQL）
  · 事务提交 → 异步删除 UNDO_LOG
  · 事务回滚 → 执行 UNDO_LOG 恢复数据
  · 全局锁防脏写

TCC 模式（余额/积分等敏感操作）：
  · Try:    冻结金额
  · Confirm: 实际扣款
  · Cancel:  解冻金额

Saga 模式（长流程，如物流履约）：
  · 正向: 服务A → 服务B → 服务C 依次执行
  · 补偿: 服务C补偿 → 服务B补偿 → 服务A补偿（逆序回滚）
```

```yaml
# Seata 配置
seata:
  tx-service-group: ecommerce-tx-group
  service:
    vgroup-mapping:
      ecommerce-tx-group: default
    grouplist:
      default: localhost:8091
  config:
    type: nacos                           # 配置从 Nacos 读取
    nacos:
      server-addr: localhost:8848
  registry:
    type: nacos                           # 注册到 Nacos
    nacos:
      server-addr: localhost:8848
```

业务代码只需一个注解：
```java
@GlobalTransactional
public void createOrder(OrderDTO order) {
    inventoryService.decrease(order.getSkuId(), order.getQty()); // RM-1
    couponService.use(order.getCouponId());                      // RM-2
    balanceService.deduct(order.getUserId(), order.getAmount()); // RM-3
    orderRepository.save(order);                                  // RM-4
}
```

#### 模块9：支付网关（模拟）

```
模拟支付网关设计：

  接口定义：
    POST /api/payment/pay              → 创建支付单，返回模拟支付URL
    POST /api/payment/callback          → 模拟银行回调（异步通知）
    POST /api/payment/refund            → 退款申请
    GET  /api/payment/query/{orderNo}   → 查询支付状态
    GET  /api/payment/reconciliation    → T+1 对账单

  支付状态机：
              ┌──────────────────────────────┐
              │                              │
    待支付 ──支付成功──→ 已支付 ──发货──→ 已完成
      │                  │
      │ 超时30min         │ 退款申请
      ↓                  ↓
    已取消            退款中 ──退款成功──→ 已退款
                                退款失败──→ 人工处理

  关键设计：
    · 幂等性：  支付单号 payNo 全局唯一，Redis 记录处理状态，重复回调直接返回
    · 安全校验： 回调参数做 MD5+Key 签名校验，金额以服务端订单为准（不信任客户端）
    · 一致性：  支付成功后通过 RocketMQ 发送 PAYMENT_NOTIFY 消息通知订单服务
    · 对账：    每日定时任务拉取支付记录 vs 订单记录，差异自动告警
```

- 初期用模拟支付网关跑通流程，后续可接入真实支付宝/微信支付 SDK
- 模拟回调通过 Postman 或管理后台手动触发

#### 模块10：可观测性（Micrometer + SkyWalking + Grafana）

```
三大支柱：

  ┌─────────────────────────────────────────────────────┐
  │  Tracing（链路追踪）— SkyWalking + Micrometer Tracing │
  │                                                     │
  │  客户端 → [Gateway] → [OrderService] → [Inventory] → DB  │
  │          └── TraceID: abc123 ───────────────────┘   │
  │          Span: gw-span → order-span → inv-span → db-span │
  │                                                     │
  ├─────────────────────────────────────────────────────┤
  │  Metrics（指标）— Micrometer → Prometheus → Grafana  │
  │                                                     │
  │  面板: QPS | RT(P50/P99) | 错误率 | JVM堆/GC | 连接池  │
  │  告警: 错误率>1% | P99>2s | 磁盘>85% | 实例Down        │
  │                                                     │
  ├─────────────────────────────────────────────────────┤
  │  Logging（日志）— Logback JSON → Loki/ELK              │
  │                                                     │
  │  格式: {"timestamp":"...","level":"INFO","traceId":  │
  │         "abc123","message":"订单创建成功","service":   │
  │         "order-service"}                             │
  │  关联: 通过 TraceID 串联链路和日志                      │
  └─────────────────────────────────────────────────────┘
```

```yaml
# Micrometer 暴露指标给 Prometheus
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  metrics:
    tags:
      application: ${spring.application.name}
```

Sentinel Dashboard 与 Grafana 的分工：

| 工具 | 侧重 | 使用者 |
|------|------|--------|
| Sentinel Dashboard | 限流/熔断规则实时管理、流量监控 | 开发/运维 |
| SkyWalking UI | 链路拓扑、慢SQL分析、服务依赖 | 开发 |
| Grafana | JVM/系统指标面板、告警规则、大屏 | 运维/值班 |

---

## 四、数据库与中间件总览

| 组件 | 选型 | 生态归属 | 用途 | 部署模式 |
|------|------|----------|------|----------|
| 服务注册/发现 | Nacos 2.4 | **Spring Cloud Alibaba** | 实例管理、健康检查 | 3节点集群 |
| 配置中心 | Nacos Config | **Spring Cloud Alibaba** | 动态配置、灰度发布 | 与注册共用 |
| 熔断/限流 | Sentinel 1.8 | **Spring Cloud Alibaba** | 流量防护、降级 | Dashboard + 应用内嵌 |
| 分布式事务 | Seata 2.0 | **Spring Cloud Alibaba** | 全局事务协调 | Server + DB存储 |
| 消息队列 | RocketMQ 5.2 | **Spring Cloud Alibaba** | 异步通信、削峰 | 2主2从 |
| 关系型数据库 | MySQL 8.0 | 通用 | 业务数据 + Seata undo_log | 主从 |
| 缓存 | Redis 7.2 | 通用 | Session、热点数据、分布式锁 | Sentinel/Cluster |
| 搜索引擎 | Elasticsearch 8.12 | 通用 | 商品全文搜索 | 3节点 |
| 链路追踪 | SkyWalking 10.x | 通用 | 全链路 APM | Standalone |
| 监控告警 | Prometheus + Grafana | 通用 | 指标采集、可视化、告警 | Standalone |

---

## 五、场景落地分析（电商 B2C）

### 5.1 技术必要性

| 电商痛点 | Spring Cloud Alibaba 方案 | 不采用微服务架构的后果 |
|----------|--------------------------|----------------------|
| 单体应用扩展难 | Nacos 服务发现 + K8s HPA 弹性伸缩 | 大促期间只能整体扩容，资源浪费 |
| 高并发流量冲击 | Gateway + Sentinel QPS 限流 + 排队 | 服务雪崩，全站不可用 |
| 故障扩散 | Sentinel 熔断 + 线程/信号量隔离 | 支付异常拖垮订单、商品等所有服务 |
| 超卖 | Seata 分布式事务 + Redis 分布式锁 | 库存数据不一致，资损 |
| 消息丢失 | RocketMQ 事务消息 + 持久化 + DLQ | 订单创建成功但库存未扣，数据错乱 |
| 故障定位难 | SkyWalking 全链路追踪 + Grafana 告警 | MTTR 小时级甚至天级 |
| 支付安全 | OAuth2 认证 + 支付回调签名 + 幂等 | 支付篡改、重放攻击、重复扣款 |

### 5.2 技术适配性

| 技术 | 电商适配分析 | 适配结论 |
|------|-------------|----------|
| **Nacos** | 阿里双11百万实例验证；AP模式保证高可用；动态配置秒级生效适配大促临时调参 | ✅ 完美适配 |
| **Sentinel** | Netty非阻塞架构，QPS天花板80K+；Dashboard实时改规则，大促期间随时调整限流阈值 | ✅ 完美适配 |
| **Seata AT** | 一个 @GlobalTransactional 注解搞定事务，开发成本极低；AT模式自动回滚，覆盖90%电商场景 | ✅ 完美适配 |
| **RocketMQ** | 事务消息保证订单-库存一致性；延迟消息天然适配30分钟未支付取消订单；死信队列兜底 | ✅ 完美适配 |
| **Gateway** | WebFlux非阻塞，单节点30K+ QPS；Nacos驱动路由动态生效 | ✅ 完美适配 |
| **负载均衡** | Nacos权重路由支持灰度发布（先上线1台验证，再全量） | ✅ 完美适配 |

> 全部 6 项核心能力均完美适配电商场景，且经过阿里双11级别的生产验证。

### 5.3 可演进性

```
阶段1：MVP 上线（8个服务，单机房）
  ├── 时间：4-6周
  ├── 部署：Docker Compose 单机（开发/测试）
  ├── Nacos 单节点，MySQL 单机，RocketMQ 单Broker
  └── 目标：跑通核心交易链路（浏览→下单→支付→发货）

阶段2：生产就绪（10-12个服务，单机房生产）
  ├── 时间：+4周
  ├── 部署：K8s 集群，3台 Node
  ├── Nacos 3节点集群，MySQL 主从，RocketMQ 2主2从，Redis Sentinel
  ├── 接入：Sentinel Dashboard 生产规则，SkyWalking 全链路追踪
  └── 目标：支撑日活 1万+，QPS 1000+

阶段3：规模化（15+服务，多机房）
  ├── 时间：+8周
  ├── 引入：ShardingSphere 分库分表，Canal 数据同步，异地多活
  ├── 升级：K8s + Istio 服务网格（双模控制平面，Nacos与Istio协同）
  └── 目标：支撑日活 10万+，QPS 5000+，99.95% 可用性

阶段4：平台化 & 智能化
  ├── 引入 Spring AI 做个性化商品推荐
  ├── 自研内部开发者平台（IDP），一站式服务治理
  ├── 非核心服务（营销推送、报表）迁移至 Serverless
  └── 目标：多业务线复用基础设施，研发效率提升 3x
```

### 5.4 安全性

| 安全层面 | 具体措施 | 对应组件 |
|----------|----------|----------|
| **传输安全** | 全链路 HTTPS，内部服务间 mTLS | K8s 证书管理 |
| **认证安全** | OAuth2.1 + JWT RS256 非对称签名，Access Token 2h + Refresh Token 7d | Spring Authorization Server |
| **授权安全** | Gateway 全局 JWT 验签（第一道） + @PreAuthorize 注解（第二道）双重校验 | Gateway + Spring Security |
| **限流防护** | Sentinel QPS限流 + 热点参数限流，防止刷单/爬虫 | Sentinel |
| **支付安全** | 回调签名校验（MD5+Key）、幂等控制（Redis SETNX）、金额服务端计算 | 自研支付网关 |
| **数据安全** | 支付敏感数据 AES-256 加密、Redis 密码认证 + 网络隔离、MySQL 访问白名单 | 通用 |
| **防注入** | 参数化查询（防SQL注入）、输出编码（防XSS）、CSRF 禁用（JWT 无状态） | 应用层 |
| **操作审计** | AOP 拦截所有 Service 方法，记录操作人/时间/参数/结果到 operation_log 表 | 自研 AOP |
| **依赖安全** | OWASP Dependency-Check Maven 插件，定期扫描 CVE 漏洞 | CI 流水线 |
| **数据备份** | MySQL 每日全量 + binlog 增量备份，RDB 持久化 + AOF | 运维配置 |

---

## 六、部署拓扑

### 6.1 开发环境（Docker Compose，单机）

```
localhost (开发机)
├── 8080  Spring Cloud Gateway
├── 8081  Auth Service
├── 8082  User Service
├── 8083  Product Service
├── 8084  Order Service
├── 8085  Payment Service
├── 8086  Inventory Service
├── 8087  Search Service
├── 8848  Nacos (注册+配置中心)
├── 8858  Sentinel Dashboard
├── 8091  Seata Server
├── 9876  RocketMQ NameServer
├── 10911 RocketMQ Broker
├── 3306  MySQL
├── 6379  Redis
├── 9200  Elasticsearch
└── 8088  SkyWalking UI (可选)
```

### 6.2 生产环境（Kubernetes）

```
┌───────────────────────────────────────────────────────────┐
│  Kubernetes Cluster (3 Node)                               │
│                                                            │
│  ┌────────────────────────┐  ┌──────────────────────────┐ │
│  │  Namespace: infra       │  │  Namespace: services     │ │
│  │  · Nacos ×3 副本        │  │  · User Service ×3       │ │
│  │  · Sentinel Dashboard   │  │  · Product Service ×3    │ │
│  │  · Seata Server ×2      │  │  · Order Service ×3      │ │
│  │  · RocketMQ ×2主2从     │  │  · Payment Service ×2    │ │
│  │  · MySQL 主从           │  │  · Inventory Service ×2  │ │
│  │  · Redis Cluster ×6     │  │  · Search Service ×2     │ │
│  │  · Elasticsearch ×3     │  │  · Logistic Service ×2   │ │
│  │  · SkyWalking OAP + UI  │  │  · Marketing Service ×2  │ │
│  │  · Prometheus + Grafana │  │  · Auth Service ×2       │ │
│  └────────────────────────┘  │  · Gateway ×3             │ │
│                               └──────────────────────────┘ │
│                                                            │
│  外部: Ingress Controller → Gateway (LoadBalancer)         │
└───────────────────────────────────────────────────────────┘
```

---

## 七、技术栈总览

```
Java 21 (LTS) + Spring Boot 3.2.x + Spring Cloud Alibaba 2023.0.1.x

Spring Cloud Alibaba 核心层（全家桶）:
  ├── Nacos          → 服务注册 + 发现 + 配置中心
  ├── Sentinel       → 熔断 + 降级 + 限流 + 系统保护
  ├── Seata          → 分布式事务（AT / TCC / Saga）
  ├── RocketMQ       → 消息队列（事务消息 / 延迟消息 / DLQ）
  └── Dubbo（可选）   → 高性能 RPC（gRPC 场景备选）

Spring Cloud 官方组件（与 Alibaba 无缝配合）:
  ├── Spring Cloud Gateway    → API 网关
  ├── OpenFeign + LoadBalancer → 声明式调用 + Nacos 驱动负载均衡
  └── Spring Authorization Server → OAuth2.1 认证授权

通用基础设施:
  ├── MySQL 8.0               → 核心业务数据
  ├── Redis 7.2               → 缓存 / Session / 分布式锁
  ├── Elasticsearch 8.12      → 商品全文搜索
  ├── SkyWalking 10.x         → 全链路追踪（OpenTelemetry 兼容）
  └── Prometheus + Grafana    → 指标监控 + 可视化告警

部署 & 运维:
  ├── Docker + Docker Compose → 本地开发环境
  ├── Kubernetes 1.30+        → 生产容器编排
  └── GraalVM Native Image    → 冷启动 < 50ms（后续引入）
```

---

## 八、下一步工作

### 第一阶段：环境搭建（1天）
1. 安装 JDK 21 + Docker Desktop
2. 编写 Docker Compose，一键启动 7 个中间件（Nacos / Sentinel / Seata / RocketMQ / MySQL / Redis / ES）
3. 验证所有中间件控制台可访问

### 第二阶段：框架搭建（2天）
4. 创建 Maven 父工程，引入 Spring Cloud Alibaba BOM
5. 搭建 Gateway + Auth Service（认证授权核心）
6. 各业务微服务接入 Nacos 注册发现
7. 各业务微服务接入 Sentinel（配置 Dashboard 地址）
8. Seata 配置接入 Nacos

### 第三阶段：业务开发（2-3周）
9. 按依赖顺序开发业务模块：用户 → 商品 → 搜索 → 库存 → 订单 → 支付 → 营销 → 物流
10. 订单模块集成 RocketMQ（事务消息 + 延迟消息）和 Seata（AT 模式）
11. 支付模块开发模拟支付网关 + 回调处理
12. Gateway 配置路由规则 + Sentinel 限流规则

### 第四阶段：测试与部署（1周）
13. 编写集成测试（验证熔断/降级/限流/事务回滚行为）
14. 接入 SkyWalking + Prometheus + Grafana
15. 编写 Dockerfile 和 K8s Deployment/Service/Ingress YAML
16. 部署到 K8s 集群，完成全链路压测

---

## 九、实施进展（2026-06-10）

### 9.1 已完成

| 事项 | 状态 | 说明 |
|------|------|------|
| 中间件部署 | ✅ | Docker Compose 一键启动 Nacos / Sentinel / Seata / RocketMQ / ES |
| 项目框架搭建 | ✅ | Maven 多模块（父工程 + gateway + demo-service） |
| Nacos 服务注册 | ✅ | demo-service 和 gateway-service 均已注册成功 |
| Gateway 路由转发 | ✅ | `/api/demo/**` → `lb://demo-service` 链路验证通过 |
| Sentinel 接入 | ✅ | demo-service 已接入 Sentinel Dashboard |

### 9.2 实际项目结构

```
untitled/
├── pom.xml                          ← Maven 父工程（packaging=pom）
│   └── 管理 Spring Boot 3.3.7 + Spring Cloud 2023.0.3 + Alibaba 2023.0.1.0
│
├── gateway/                         ← 模块1：API 网关
│   ├── pom.xml                      ← 依赖：Gateway + Nacos Discovery + LoadBalancer
│   └── src/.../GatewayApplication.java   → 端口 9010
│
└── demo-service/                    ← 模块2：业务服务
    ├── pom.xml                      ← 依赖：Web + Nacos Discovery + Sentinel
    └── src/.../DemoApplication.java      → 端口 9011
         .../DemoController.java          → GET /api/demo/hello
```

**关键设计决策**：Gateway 和 Web 拆到不同 Maven 模块，各自独立 classpath，彻底消除 `SpringMvcFoundOnClasspathConfiguration` 冲突。

### 9.3 验证结果

```
浏览器 → http://localhost:9010/api/demo/hello
  → Gateway(9010) 路由匹配 "/api/demo/**"
  → Nacos 发现 demo-service 实例(9011)
  → 转发请求
  → {"service":"demo-service","message":"Hello from Demo Service!","time":"..."}

Nacos 控制台 (localhost:8848/nacos):
  ✅ demo-service    1个实例，健康
  ✅ gateway-service 1个实例，健康
```

### 9.4 下一步计划

| 优先级 | 任务 | 涉及模块 | 验证点 |
|--------|------|----------|--------|
| P0 | 新建 user-service 模块 | 父工程 + 新模块 | Feign 调用 demo-service |
| P0 | Sentinel 限流验证 | demo-service | Dashboard 配置 QPS 规则生效 |
| P1 | 新建 order-service + payment-service | 多模块 | RocketMQ 消息 + Seata 事务 |
| P1 | Python 服务接入 Nacos | Python FastAPI | Java ↔ Python 互调 |
| P2 | MySQL + Redis 集成 | 各业务模块 | 数据持久化 |
| P2 | 全链路可观测 | SkyWalking + Grafana | 链路追踪 |
