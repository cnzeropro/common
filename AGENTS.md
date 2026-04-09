# AGENTS.md

本文档面向在 `common` 仓库中工作的 AI 代码代理。

## 项目定位

- 坐标 `org.zero:common`，Spring Boot 2.7 / Spring Cloud 2021 多模块公共库
- 主线 Java 8 兼容，保留高版本兼容层
- Gradle 为日常入口；Maven 承担发布、聚合职责，两者都需核对
- Root 是 `java-platform` 版本对齐入口，非普通业务模块

## 模块地图

| 模块 | 说明 |
|------|------|
| `:core-base` | 核心主体，包结构 `extension/`、`support/`、`util/`、`exception/`、`aop/` |
| `:core-jakarta` | Jakarta 适配层 |
| `:core-spring-boot3` | Spring Boot 3 / Jakarta 适配层 |
| `:core-ip2region2` | ip2region v2 适配层 |
| `:core-distribution` | Gradle 分发占位 |
| `:common-data` | 常量、枚举、异常、通用模型 |
| `:common-api` | API 契约与 DTO |
| `:common-job` | 任务调度与 cron 能力 |
| `:common-test` | 集成/演示应用，端口 34567 |
| `common-bom` | 独立模块，需单独构建 |

---

## 构建命令

### Gradle

```bash
./gradlew build                    # 构建所有模块
./gradlew test                     # 运行所有测试
./gradlew :core-base:test         # 运行 core-base 测试
./gradlew :core-base:test --tests "org.zero.common.util.StringUtilTest"  # 单测试类
./gradlew :core-base:test --tests "org.zero.common.util.StringUtilTest.isBlank*"  # 单测试方法
./gradlew :common-test:test -x test  # 跳过测试打包
./gradlew publish
```

### Maven

```bash
mvn -pl common-core/core-base -am test                    # 测试 core-base
mvn -pl common-core/core-base -am test -Dtest=StringUtilTest  # 单测试类
cd common-bom && mvn deploy
```

### 元数据同步

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

### JDK / Toolchains

- `metadata/build-metadata.toml` 只作为脚本输入源；独立脚本会同步生成 Gradle 的 `gradle/libs.versions.toml`、root `gradle.properties` 与 Maven root `pom.xml`
- Gradle 默认从 root `gradle.properties` 读取 `build.revision` 与 `build.profiles`，如需临时覆盖使用 `-Pbuild.revision=...`、`-PbuildProfile=...`；JDK 通过 `JAVA_HOME`、`JDK11_HOME`、`JDK17_HOME`、`JDK21_HOME` 自动发现
- Maven 默认读取 root `pom.xml` 中的 `<revision>`，如需临时覆盖使用 `-Drevision=...`
- Maven 不再读取仓库内 JDK 绝对路径；多版本编译依赖外部 toolchains，使用用户自带 `~/.m2/toolchains.xml` 或命令行 `mvn -t <toolchains.xml> ...`
- 如需仓库内临时 toolchains 文件，可自行创建 `.mvn/toolchains.local.xml`，该路径不会纳入版本控制
- **不要假设直接执行 `mvn` / `./gradlew` 就一定使用了项目当前需要的 Java 版本**：两者会优先使用当前 shell 的 `JAVA_HOME`、`JDK11_HOME`、`JDK17_HOME`、`JDK21_HOME` 或 Maven toolchains 配置；如果这些环境变量/配置与项目实际目标版本不一致，测试代码可能会被误判为“编译报错”
- 典型现象：代码本身没有问题，但编译阶段出现“类文件版本过高/过低”“拿到了 Spring 6 / Jakarta 依赖却在 Java 8 下编译”“同一模块在 IDEA 能过、命令行失败”等问题；这类问题优先检查实际使用的 JDK，而不是先改测试代码
- AI 代理在运行构建前，必须先确认“命令实际选中的 Java”与“模块期望的 Java”一致：Gradle 检查 `JAVA_HOME` / `JDK*_HOME`，Maven 检查 `toolchains.xml` / `-t .mvn/toolchains.local.xml`，并结合 root `gradle.properties`、root `pom.xml`、目标模块 `build.gradle` / `pom.xml` 判断
- 如果只是想验证某个模块/测试，不要因为命令行环境里的默认 JDK 偏差而修改源码；应先切换到正确的 `JAVA_HOME` 或显式指定 Maven toolchain，再重新执行命令

---

## 代码风格

### 格式 (.editorconfig)

| 类型 | 缩进 | 最大行宽 | 换行 |
|------|------|----------|------|
| Java/Groovy/Kotlin/XML | Tab 4 | 120 | LF |
| YAML/JSON/Properties | 2 空格 | 120 | LF |

### 命名约定

- 类/接口：`UpperCamelCase`（如 `StringUtil`）
- 方法/变量：`lowerCamelCase`（如 `isBlank()`）
- 常量：`UPPER_SNAKE_CASE`（如 `MAX_RETRY_COUNT`）
- 包名：全小写
- 抽象类：以 `Abstract` 开头

### Imports

1. `java.*` / `javax.*`
2. 第三方库
3. `org.zero.*`
4. 当前项目

- 避免 `.*` 通配符导入
- 使用 IntelliJ Organize Imports

### 注解与 Javadoc

- `@Override` 必加
- `@Nullable` / `@NonNull` 标注可空参数和返回值
- Lombok 优先：`@Data`、`@Builder`、`@AllArgsConstructor`、`@RequiredArgsConstructor`
- Javadoc 格式：
```java
/**
 * 描述。
 *
 * @author Zero (cnzeropro@163.com)
 * @since YYYY/MM/DD
 */
```

### 包结构

- `extension/` - 扩展接口/实现
- `support/` - 框架支持（80+ 子包）
- `util/` - 工具类
- `exception/` - 异常定义
- `aop/` - AOP 切面

---

## 测试策略

- 框架：JUnit 5，匹配：`*Test`、`*Tests`、`*Spec`
- 已知陷阱：`TaskManagerTest`、`SocketPingClientTest`（等待 `Scanner(System.in)`）、`JmhTests`（Benchmark）、`ChunkedByteBufferTest`、`ExcelUtilTest`、`ExportControllerTest`（依赖绝对路径或输出目录）

---

## Maven / Gradle 差异

| 方面 | 漂移 |
|------|------|
| 依赖版本 | `metadata/build-metadata.toml` 生成的 Gradle catalog / root `gradle.properties` ≠ root `pom.xml` |
| Java 版本 | 主线 Java 8，adapter POM 更高 |
| 聚合逻辑 | Gradle `:core-distribution` ≠ Maven `common-core` |

改动必须双边核对：依赖版本、发布坐标、adapter 兼容层、聚合逻辑、Java 版本

## Multi-Release JAR

- `src/main/java` - Java 8 主线
- `src/main/java9`、`java11`、`java17`、`java21`

日常改 Java 8 主线；触及版本差异再检查高版本目录。

## 代理工作流

1. 读 `settings.gradle`、目标 `build.gradle`、相关 `pom.xml` 确认边界
2. 用 grep/AST-grep 搜索符号、测试、调用点
3. 涉及依赖/发布/adapter/Java 版本，同时检查 Gradle 和 Maven
4. 只做最小必要修改

## 常见陷阱

1. **禁止在测试中使用 `Scanner(System.in)`** - 会导致测试挂起
2. **禁止硬编码 Windows 绝对路径** - 使用相对路径或 `@TempDir`
3. **禁止遗漏 `@Override`** - 编译会失败
4. **禁止使用类型绕过** - `as any`、`@ts-ignore` 禁止

---

## 增量补充

- Gradle 可用 `-PexcludeProjects=...` 过滤动态扫描模块（见 `settings.gradle`）：
- `./gradlew -PexcludeProjects=common-test test`
- 本地发布建议使用 clean + publish 组合（Gradle 产物输出到 `build/repos/local-staging`，不写入 Maven Local）：
- `./gradlew clean publish`
- Maven 打包演示应用（跳过 root 全量）：
- `mvn -pl common-test -am package`
- 受限网络环境下，Gradle Wrapper 需下载 `gradle-8.14.3-bin.zip`，失败时需在结论中标注“未完成构建验证”。
