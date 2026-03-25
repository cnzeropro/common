# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概况

`org.zero:common` — Spring Boot 2.7 / Spring Cloud 2021 体系下的 Java 多模块公共库。
主线兼容 Java 8，同时保留 Java 9/11/17/21 的 multi-release jar 兼容层。
Gradle 是日常开发入口，Maven 承担发布与聚合职责，两套构建并存且版本存在漂移。

## 构建命令

```bash
# Gradle（日常开发）
./gradlew build                          # 全量构建
./gradlew :core-base:test                # 单模块测试
./gradlew :core-base:test --tests '*.CharSequenceUtilTest'  # 单类测试
./gradlew clean publishToMavenLocal      # 发布到本地

# Maven（发布 / 聚合）
mvn -pl common-core/core-base -am test   # 单模块测试
mvn -pl common-test -am package          # 打包演示应用
cd common-bom && mvn deploy              # 发布 BOM
```

注意：`common-bom` 不在 root Gradle 子项目中，需进入目录单独构建。
受限环境下 `gradlew` 可能因下载 gradle-8.14.3 失败；未验证前不要宣称"已通过构建"。

## 模块结构

| Gradle 项目 | 目录 | 职责 |
|---|---|---|
| `:core-base` | `common-core/core-base/` | 核心主体，日常改动首选 |
| `:core-jakarta` | `common-core/adapters/core-jakarta/` | Jakarta 适配层 |
| `:core-spring-boot3` | `common-core/adapters/core-spring-boot3/` | Spring Boot 3 适配层 |
| `:core-ip2region2` | `common-core/adapters/core-ip2region2/` | ip2region v2 适配 |
| `:core-distribution` | `common-core/core-distribution/` | Gradle 分发占位；Maven 侧聚合产出 `common-core` |
| `:common-data` | `common-data/` | 常量、枚举、异常、通用模型 |
| `:common-api` | `common-api/` | API 契约与 DTO（Loki 相关） |
| `:common-job` | `common-job/` | 任务调度与 cron |
| `:common-test` | `common-test/` | Spring Boot 集成/演示应用（端口 34567，H2） |

其他：`buildSrc/` 含自定义 convention 插件；`common-bom/` 独立发布 BOM。
`settings.gradle` 显式声明子项目映射，支持 `-PincludeProjects=...` 与 `-PexcludeProjects=...` 过滤。

## core-base 包架构

根包 `org.zero.common.core`，按职责分层：

- `extension/` — JDK 类型扩展（DataUnit、IO 流等）
- `support/` — 80+ 子包，按关注点组织：协议(`http/`、`feign/`)、缓存(`cache/`、`redisson/`)、编解码(`codec/`、`crypto/`)、框架集成(`spring/`、`jackson/`、`hibernate/`)、安全(`jwt/`、`captcha/`)等
- `util/` — 静态工具类（null-safe 风格）
- `exception/` — 异常体系
- `aop/` — 切面

Multi-release 源码目录：`src/main/java9`、`java11`、`java17`、`java21`（当前实际内容很少，但已进入构建语义）。

## Gradle / Maven 版本漂移

Spring 核心一致（Framework 5.3.39、Boot 2.7.18、Cloud 2021.0.9），但以下存在漂移：

| 依赖 | Gradle（`gradle/libs.versions.toml`） | Maven (`pom.xml`) |
|---|---|---|
| Jetty | 12.0.19 | 9.4.58.v20250814 |
| Jakarta EE | 11.0.0 | 9.1.0 |
| Redisson | 3.27.2 | 3.52.0 |
| ip2region | 2.7.0 | 3.1.0 |

改动依赖版本、发布坐标、adapter 兼容层、聚合逻辑、Java 版本配置时，必须同时检查 Gradle 与 Maven。
中立版本清单位于 `metadata/build-metadata.toml`，通过独立脚本同步到 `gradle/libs.versions.toml`、root `gradle.properties` 与 Maven POM。
不要把 Gradle project path、Maven artifactId、目录名视为一一对应。

## 依赖生态

仓库通过 BOM 和 version catalog 管理的主要依赖：
ORM(Hibernate/MyBatis Plus/MyBatis Flex/QueryDSL)、缓存(Redisson/JetCache)、序列化(Kryo/Fastjson2)、安全(Shiro/Sa-Token/Jasypt)、调度(ElasticJob/XXL-Job/PowerJob)、微服务(Dubbo/Nacos/Sentinel/Spring Cloud Gateway)、文件(X-File-Storage)、Excel(EasyExcel/FastExcel/POI)。

## 代码约定

格式以 `.editorconfig` 为准：UTF-8、LF、行宽 120。Java/Groovy/Kotlin/XML 用 Tab(宽度4)，YAML/JSON/Shell 用 2 空格。

Javadoc 模板：
```java
/**
 * 描述。
 *
 * @author Zero (cnzeropro@163.com)
 * @since YYYY/MM/DD
 */
```

- 大量使用 Lombok（1.18.38）和 MapStruct（1.6.3）
- 编译带 `-parameters` flag
- 修改现有代码时延续周边写法，不混入另一套风格
- 包结构遵循 `extension/` / `support/` / `util/` / `exception/` 分层，不要混写
- 历史遗留的 `@date`、邮箱不一致等写法，修改旧文件时保持局部一致即可

## 测试策略

JUnit 5，Gradle 匹配 `*Test`、`*Tests`、`*Spec`。优先定向验证，不建议默认跑全量。

必须跳过的测试：
- `TaskManagerTest`、`SocketPingClientTest` — 等待 `Scanner(System.in)`，会阻塞
- `JmhTests` — benchmark，非回归测试
- `ChunkedByteBufferTest`、`ExcelUtilTest`、`FileRangeSplitterTest` — 依赖绝对 Windows 路径
- `ExportControllerTest` — 写文件到 `target/download`
- `common-test` 集成测试 — 依赖 Spring Boot 上下文和 H2

## 工作流

1. 先用 `rg` / Grep 建立上下文，再决定是否需要构建命令
2. 先读 `settings.gradle`、目标模块 `build.gradle`、相关 `pom.xml`，确认项目边界
3. 涉及依赖/发布/adapter/Java 版本时，同时检查 Gradle 与 Maven
4. `gradlew` 因网络失败时不要假设"理论上能过"，明确说明阻塞点
5. 默认跳过交互式、benchmark、绝对路径依赖测试
6. 仓库无 CI（无 `.github/workflows`），不要假设有自动化兜底
7. `AGENTS.md` / `CLAUDE.md` 是本地协作文件，提交前确认是否纳入版本控制

## 验收口径

完成改动后回答：改动影响哪个模块、需要修改 Gradle/Maven 还是两者、实际执行了哪些验证、哪些验证因环境限制未执行。

## Metadata

```bash
powershell -ExecutionPolicy Bypass -File scripts/sync-metadata.ps1
powershell -ExecutionPolicy Bypass -File scripts/verify-metadata.ps1
.\scripts\sync-metadata.bat
.\scripts\verify-metadata.bat
py scripts/sync-metadata.py
py scripts/verify-metadata.py
node scripts/sync-metadata.js
node scripts/verify-metadata.js
./scripts/sync-metadata.sh
./scripts/verify-metadata.sh

# verbose examples
powershell -ExecutionPolicy Bypass -File scripts/verify-metadata.ps1 --verbose
py scripts/verify-metadata.py --verbose
node scripts/verify-metadata.js --verbose
./scripts/verify-metadata.sh --verbose
.\scripts\verify-metadata.bat --verbose
```

Gradle 与 Maven 已完全解耦：`metadata/build-metadata.toml` 只在 `sync/verify-metadata` 脚本中读取，普通构建运行期只消费 Gradle `gradle.properties`、`gradle/libs.versions.toml` 与 Maven root `pom.xml`；临时覆盖分别使用 `-Pbuild.revision=...`、`-PbuildProfile=...` 和 `-Drevision=...`。Gradle 通过环境变量自动发现 JDK，Maven 多版本编译依赖用户自带 `toolchains.xml` 或命令行 `mvn -t <toolchains.xml> ...`。
