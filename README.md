<div align="center">
  <h1>SwiftCache</h1>
  <p><b>轻量级、模块化的本地 Java 缓存库</b> — 8 种淘汰策略 · 4 种过期策略 · AOF/RDB 持久化 · 拦截器链 · 事件监听 · SPI 序列化</p>

  <p>
    <a href="#核心特性">特性</a> •
    <a href="#快速开始">快速开始</a> •
    <a href="#淘汰策略">淘汰策略</a> •
    <a href="#过期策略">过期策略</a> •
    <a href="#持久化">持久化</a> •
    <a href="#项目结构">项目结构</a>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&style=flat-square" alt="Java 17">
    <img src="https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&style=flat-square" alt="Maven">
    <img src="https://img.shields.io/badge/license-Apache2-FF0080?style=flat-square" alt="Apache 2.0">
  </p>
</div>

---

## 核心特性

| 维度 | 能力 |
|------|------|
| **淘汰策略** | FIFO、LRU、LFU、Clock、LRU-2、LRU-2Q、双链表 LRU、无淘汰 |
| **过期策略** | Redis 式随机采样、排序过期、顺序过期、无过期 |
| **持久化** | AOF 追加日志、RDB JSON 快照、无持久化 |
| **数据加载** | AOF 恢复、RDB 恢复、自定义加载器 |
| **拦截器链** | 可插拔 before/after 钩子：耗时统计、淘汰触发、淘汰跟踪、AOF 记录、惰性刷新、统计采集 |
| **事件监听** | `CacheListener` SPI — PUT、REMOVE、EXPIRE、EVICT |
| **统计信息** | 命中次数、未命中次数、淘汰次数、写入次数、命中率 |
| **序列化 SPI** | 可插拔序列化，默认 Jackson |
| **线程安全** | `ReentrantReadWriteLock` 读写锁、`AtomicInteger`/`AtomicBoolean` 原子计数器、`ConcurrentLinkedQueue` AOF 缓冲区、`synchronized` 淘汰策略 |

---

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>io.swiftcache</groupId>
    <artifactId>swiftcache-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

### 基本使用

```java
Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .size(3)
        .build();

cache.put("A", "hello");
cache.put("B", "world");
cache.put("C", "foo");

cache.get("A");                  // "hello"
cache.size();                    // 3

cache.put("D", "bar");           // 触发淘汰（默认 FIFO）
cache.containsKey("A");          // false
```

### 过期时间

```java
Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .size(3)
        .build();

cache.put("A", "hello");
cache.expireAt("A", System.currentTimeMillis() + 1000);

Thread.sleep(1500);
cache.size();                    // 0
```

---

## 淘汰策略

通过 `CacheBs.evict()` 配置，使用 `CacheEvicts` 工厂创建：

```java
Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .size(3)
        .evict(CacheEvicts.<String, String>lfu())
        .build();
```

| 工厂方法 | 策略 | 说明 |
|----------|------|------|
| `fifo()` | FIFO | 先进先出，基于 `LinkedHashSet` |
| `lru()` | LRU | 最近最少使用，基于 `LinkedHashMap`（access-order） |
| `lruDoubleListMap()` | 双链表 LRU | HashMap + 双向链表，性能优于朴素 LRU |
| `lru2Q()` | LRU-2Q | 双队列变体，命中率更高 |
| `lru2()` | LRU-2 | 频率感知变体 |
| `lfu()` | LFU | 最不经常使用，频率 map + 最小频率追踪 |
| `clock()` | Clock | 第二次机会时钟算法 |
| `none()` | 无淘汰 | 永不淘汰 |

---

## 过期策略

通过 `CacheBs.expire()` 配置，使用 `CacheExpires` 工厂创建：

```java
Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .expire(CacheExpires.<String, String>sort())
        .build();
```

| 工厂方法 | 策略 | 行为 |
|----------|------|------|
| `random()` | 随机采样 | Redis 式：随机采样 + 惰性删除 + 定时扫描。两阶段扫描（快模式检查边界 + 全量扫描） |
| `sort()` | 排序过期 | 基于 `TreeMap` 维护有序过期队列，确定性强但占用额外空间 |
| `sequence()` | 顺序过期 | 顺序遍历扫描过期 key |
| `none()` | 无过期 | 永不过期 |

过期线程池为命名守护线程（`cache-expire-*`），JVM 退出时自动清理。

---

## 持久化

```java
// RDB 快照，每 5 分钟全量持久化
Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .persist(CachePersists.<String, String>dbJson("cache.rdb"))
        .load(CacheLoads.<String, String>dbJson("cache.rdb"))
        .build();

// AOF 追加日志，每 1 秒持久化
Cache<String, String> cache2 = CacheBs.<String, String>newInstance()
        .persist(CachePersists.<String, String>aof("cache.aof"))
        .load(CacheLoads.<String, String>aof("cache.aof"))
        .build();
```

| 策略 | 机制 | 适用场景 |
|------|------|----------|
| **RDB** | 定时全量 JSON 快照 | 写入频率低、需定点恢复 |
| **AOF** | 实时追加每条写操作日志 | 写入频繁、需高持久性 |
| **无** | 不持久化 | 纯缓存场景 |

持久化线程池为命名守护线程（`cache-persist-*`），JVM 退出时自动清理。

---

## 事件监听

```java
List<CacheEvent<String, String>> events = new ArrayList<>();

Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .listener(events::add)
        .build();

cache.put("A", "1");   // 触发 CacheEventType.PUT
cache.get("A");        // 读操作不触发事件
cache.remove("A");     // 触发 CacheEventType.REMOVE
```

支持的事件类型：`PUT`、`REMOVE`、`EXPIRE`、`EVICT`。

---

## 缓存统计

```java
Cache<String, String> cache = CacheBs.<String, String>newInstance()
        .build();

cache.put("A", "1");
cache.get("A");        // 命中
cache.get("MISS");     // 未命中

CacheStats stats = cache.cacheContext().stats();
stats.hitCount();      // 1
stats.missCount();     // 1
stats.putCount();      // 1
stats.hitRate();       // 0.5
```

---

## 序列化 SPI

`CacheSerializer` 接口支持自定义序列化实现，默认内置 Jackson：

```java
public interface CacheSerializer {
    <T> String serialize(T object);
    <T> T deserialize(String json, Class<T> type);
}
```

所有持久化和 AOF 加载类（`CachePersistAof`、`CachePersistDbJson`、`CacheLoadAof`、`CacheLoadDbJson`）内部均通过此 SPI 完成序列化。

---

## 项目结构

```
swiftcache/
├── swiftcache-api/                  # SPI 接口层
│   └── src/main/java/io/swiftcache/
│       ├── api/Cache.java           # 核心缓存接口
│       ├── api/CacheEntry.java      # KV 条目
│       ├── api/context/             # CacheContext — 运行时配置
│       ├── api/evict/               # CacheEvict — 淘汰策略 SPI
│       ├── api/expire/              # CacheExpire — 过期策略 SPI
│       ├── api/interceptor/         # CacheInterceptor + 上下文
│       ├── api/listener/            # CacheListener + CacheEvent
│       ├── api/load/                # CacheLoad — 加载器 SPI
│       ├── api/persist/             # CachePersist + AOF 条目
│       ├── api/serializer/          # CacheSerializer SPI
│       └── api/stats/               # CacheStats
│
├── swiftcache-core/                 # 核心实现层
│   └── src/main/java/io/swiftcache/
│       ├── core/bs/CacheBs.java     # Fluent Builder
│       ├── core/core/               # DefaultCache + DefaultCacheContext
│       ├── core/model/              # 数据模型（FreqNode、CircleListNode 等）
│       ├── core/support/evict/      # 8 种淘汰策略实现
│       ├── core/support/expire/     # 4 种过期策略实现
│       ├── core/support/interceptor/ # 拦截器链 + 6 个拦截器
│       ├── core/support/load/       # 3 种加载器实现
│       ├── core/support/persist/    # 3 种持久化实现
│       ├── core/support/serializer/ # JacksonSerializer
│       └── core/support/struct/     # LRU 数据结构
│
└── swiftcache-test/                 # 测试（JUnit 5 + AssertJ）
```

---

## 设计说明

### Builder 模式

`CacheBs` 提供 Fluent Builder，将所有可配置组件按职责编排：

```
CacheBs.newInstance()
  .size(int)
  .evict(CacheEvict)
  .expire(CacheExpire)
  .load(CacheLoad)
  .persist(CachePersist)
  .listener(CacheListener)
  .interceptorList(List<CacheInterceptor>)
  .build()
```

### 拦截器链

每次缓存操作经过可配置的拦截器链执行 before/after 钩子。默认链包含：

1. **CacheInterceptorCommonCost** — 方法入出口日志 + 耗时统计
2. **CacheInterceptorEvict** — `put` 前触发淘汰
3. **EvictTrackingInterceptor** — 读写后更新淘汰策略访问记录
4. **CacheInterceptorAof** — 写操作追加到 AOF 日志
5. **CacheInterceptorRefresh** — 惰性过期删除
6. **StatsInterceptor** — 采集命中/未命中/写入/淘汰统计

### 线程安全

| 组件 | 机制 |
|------|------|
| DefaultCache（get/size/containsKey） | `ReentrantReadWriteLock.ReadLock` |
| DefaultCache（put/remove/expireAt） | `ReentrantReadWriteLock.WriteLock` |
| CacheEvictFifo / CacheEvictLru | `synchronized` 方法 |
| CacheEvictLfu.minFreq | `AtomicInteger` |
| CachePersistAof.bufferList | `ConcurrentLinkedQueue` |
| CacheExpireRandom.fastMode | `AtomicBoolean` |
| 定时线程池 | 命名守护线程（`ThreadFactory`）+ JVM shutdown hook 清理 |
| CacheStats 计数器 | `AtomicLong` |

### 技术栈

| 类别 | 组件 |
|------|------|
| **语言** | Java 17 |
| **构建** | Maven 3.x |
| **JSON** | Jackson 2.15 |
| **日志** | SLF4J 2.0 + Logback 1.4 |
| **测试** | JUnit 5.10 + AssertJ 3.24 |
