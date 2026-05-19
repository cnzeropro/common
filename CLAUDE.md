# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## 项目概况

- 坐标：`org.zero:common`
- 技术栈：Spring Boot 2.7 / Spring Cloud 2021
- 主线兼容：Java 8
- 兼容层：保留 `java11` / `java17` / `java21` / `java25` multi-release source set
- 构建职责：Gradle 是日常开发入口，Maven 负责发布和聚合
- Root 项目是 `java-platform` 版本对齐入口，不是普通业务模块

## 构建命令

```bash
# Gradle
./gradlew build
./gradlew test
./gradlew :core-base:test
./gradlew :core-base:test --tests "*.CharSequenceUtilTest"
./gradlew clean publish

# Maven
mvn -pl common-core/core-base -am test
mvn -pl common-test -am package
cd common-bom && mvn deploy
```

注意：

- `common-bom/` 不在 root Gradle 子项目里，需要进入目录单独构建
- 受限网络环境下，`gradlew` 可能因下载 `gradle-8.14.3` 失败；没有实际跑通前，不要宣称“已通过构建”

## JDK / Toolchains

这是本仓库最容易误判的问题之一。

- 不要假设直接执行 `mvn` 或 `./gradlew` 就一定使用了项目当前需要的 Java 版本
- Gradle 会优先受当前 shell 的 `JAVA_HOME`、`JDK11_HOME`、`JDK17_HOME`、`JDK21_HOME` 影响
- Maven 会优先受 `~/.m2/toolchains.xml` 或显式 `-t <toolchains.xml>` 影响
- 如果当前命令实际选中的 JDK 和模块期望的 JDK 不一致，就会出现“代码本身没问题，但命令行编译/测试失败”的假象

典型症状：

- 类文件版本不匹配，例如 `52.0` / `61.0`
- 命令行拿到了 Spring 6 / Jakarta 依赖，却按 Java 8 在编译
- 同一模块在 IDEA 能过，但 `mvn test` / `gradlew test` 失败

Agent 在运行构建前，必须先做这几个检查：

1. 读 root `gradle.properties`、root `pom.xml`、目标模块 `build.gradle` / `pom.xml`
2. 判断该模块期望的 Java 版本和 profile
3. 确认当前命令实际会使用哪个 JDK / toolchain
4. 如果两者不一致，先修正环境，再复跑命令

结论规则：

- 在没有确认 Java / toolchain 选择正确前，不要因为测试编译失败就直接修改源码或测试代码
- 环境问题要先修环境，不要把环境偏差误判成代码问题

## 模块结构

| Gradle 项目            | 目录                                        | 说明                                     |
|----------------------|-------------------------------------------|----------------------------------------|
| `:core-base`         | `common-core/core-base/`                  | 核心主体，包含任务调度与 cron                      |
| `:core-jakarta`      | `common-core/adapters/core-jakarta/`      | Jakarta 适配层                            |
| `:core-spring-boot3` | `common-core/adapters/core-spring-boot3/` | Spring Boot 3 适配层                      |
| `:core-ip2region2`   | `common-core/adapters/core-ip2region2/`   | ip2region v2 适配层                       |
| `:core-distribution` | `common-core/core-distribution/`          | Gradle 分发占位；Maven 侧对应 `common-core` 聚合 |
| `:common-data`       | `common-data/`                            | 常量、枚举、异常、通用模型                          |
| `:common-api`        | `common-api/`                             | API 契约与 DTO                            |
| `:common-test`       | `common-test/`                            | Spring Boot 集成/演示应用，端口 `34567`         |

补充：

- `build-logic/` 包含自定义 convention plugin
- `common-bom/` 是独立发布模块
- `settings.gradle` 支持 `-PincludeProjects=...` 和 `-PexcludeProjects=...`

## 版本与元数据

- 中立元数据源是 `metadata/build-metadata.toml`
- 同步脚本会生成 Gradle 的 `gradle/libs.versions.toml`、root `gradle.properties` 和 Maven root `pom.xml`
- Gradle 日常运行期读取 `gradle.properties` 与 version catalog
- Maven 日常运行期读取 root `pom.xml`
- 修改依赖版本、发布坐标、adapter、聚合关系、Java 版本时，必须同时检查 Gradle 和 Maven

元数据命令：

```bash
powershell -ExecutionPolicy Bypass -File scripts/metadata/sync.ps1
powershell -ExecutionPolicy Bypass -File scripts/metadata/verify.ps1
powershell -ExecutionPolicy Bypass -File scripts/metadata/sync.ps1 gradle
powershell -ExecutionPolicy Bypass -File scripts/metadata/verify.ps1 maven
.\scripts\metadata\sync.bat
.\scripts\metadata\verify.bat
.\scripts\metadata\sync.bat gradle
.\scripts\metadata\verify.bat maven
py scripts/metadata/sync.py
py scripts/metadata/verify.py
py scripts/metadata/sync.py gradle
py scripts/metadata/verify.py maven
node scripts/metadata/sync.js
node scripts/metadata/verify.js
node scripts/metadata/sync.js gradle
node scripts/metadata/verify.js maven
./scripts/metadata/sync.sh
./scripts/metadata/verify.sh
./scripts/metadata/sync.sh gradle
./scripts/metadata/verify.sh maven
```

## 代码约定

- 遵循 `.editorconfig`
- Java/Groovy/Kotlin/XML 使用 Tab 4，行宽 120，LF
- YAML/JSON/Properties 使用 2 空格，行宽 120，LF
- 大量使用 Lombok 和 MapStruct，保持现有风格
- 修改旧文件时优先局部一致，不要把整份文件改成另一套风格
- 包结构遵循 `extension/`、`support/`、`util/`、`exception/`、`aop/`

Javadoc 模板：

```java
/**
 * 描述。
 *
 * @author Zero (cnzeropro@163.com)
 * @since YYYY/MM/DD
 */
```

## 测试策略

- 测试框架：JUnit 5
- Gradle 测试匹配：`*Test`、`*Tests`、`*Spec`
- 优先跑定向验证，不默认跑全量
- 测试类命名遵循“一类一测”：被测类 `Xxx` 的测试类命名为 `XxxTest`，不要额外添加 `Javax`、`Jakarta`、`Functional`、`Compiled` 等前置或后置语义
- 测试类必须放在与被测类对应的测试源码根和同名包下，例如 `src/main/java/org/example/Foo.java` 对应 `src/test/java/org/example/FooTest.java`，multi-release source set 也按各自源码根对应
- 一个被测类只对应一个测试类；一个测试类也只验证一个被测类，不要在同一个测试类中混放多个生产类的测试
- 多个测试共享的数据、对象构造或断言辅助逻辑应抽到测试夹具或 helper 类，且不要以 `Test`、`Tests`、`Spec` 结尾，避免被测试框架当作测试类

需要避开的测试：

- `SocketPingClientTest`：依赖 `Scanner(System.in)`，会挂起
- `JmhTests`：benchmark，不是常规回归测试
- `ChunkedByteBufferTest`、`ExcelUtilTest`、`FileRangeSplitterTest`：依赖绝对 Windows 路径
- `ExportControllerTest`：写文件到 `target/download`
- `common-test` 集成测试：依赖 Spring Boot 上下文和 H2

## 回答要求

完成改动后，明确说明：

- 影响了哪个模块
- 改动涉及 Gradle、Maven，还是两者都涉及
- 实际执行了哪些验证
- 哪些验证因为环境限制没有执行
