# common

`common` 是一个以 `org.zero:common` 为坐标的多模块 Java 公共库，主线面向 Java 8、Spring Boot 2.7 / Spring Cloud 2021，同时保留面向更高 Java 版本与 Jakarta / Spring Boot 3 的兼容适配层。

仓库当前采用“双入口、单事实源”的工程策略：

- Gradle 作为日常开发与版本对齐入口
- Maven 作为发布、聚合与兼容性校验入口
- `metadata/build-metadata.toml` 作为构建元数据的统一输入源

## 项目定位

- 坐标：`org.zero:common`
- 主线兼容：Java 8
- 兼容适配：Java 17+、Jakarta、Spring Boot 3
- 构建体系：Gradle + Maven
- 仓库定位：公共基础库、公共模型、公共 API 契约与适配层

## 模块地图

| 模块                   | 说明                              |
|----------------------|---------------------------------|
| `:core-base`         | 核心主体，包含扩展、支持层、工具类、异常与 AOP 等公共能力 |
| `:core-jakarta`      | Jakarta 适配层                     |
| `:core-spring-boot3` | Spring Boot 3 / Jakarta 适配层     |
| `:core-ip2region2`   | ip2region v2 适配层                |
| `:core-distribution` | Gradle 分发占位模块                   |
| `:common-data`       | 常量、枚举、异常与通用模型                   |
| `:common-api`        | API 契约与 DTO                     |
| `:common-test`       | 集成 / 演示应用，默认端口 `34567`          |
| `common-bom`         | 独立 BOM 模块，需要单独构建与发布             |

## 兼容性矩阵

| 维度           | 主线        | 兼容层                         |
|--------------|-----------|-----------------------------|
| Java         | 8         | 11 / 17 / 21 / 25 profile   |
| Spring Boot  | 2.7.x     | 3.x adapter                 |
| Spring Cloud | 2021.x    | 更高版本在 adapter / profile 中处理 |
| 命名空间         | `javax.*` | `jakarta.*`                 |

## 常用命令

### Gradle

```bash
./gradlew build
./gradlew test
./gradlew :core-base:test
./gradlew :common-test:test -x test
./gradlew clean publish
```

### Maven

```bash
mvn -pl common-core/core-base -am test
mvn -pl common-data -am test
cd common-bom && mvn deploy
```

### 元数据同步与校验

```bash
./scripts/verify-metadata.sh
./scripts/verify-gradle.sh
./scripts/verify-maven.sh

powershell -ExecutionPolicy Bypass -File scripts/verify-metadata.ps1
powershell -ExecutionPolicy Bypass -File scripts/verify-gradle.ps1
powershell -ExecutionPolicy Bypass -File scripts/verify-maven.ps1
```

### 统一 CI 校验入口

```bash
./scripts/ci/verify.sh
powershell -ExecutionPolicy Bypass -File scripts/ci/verify.ps1
```

## JDK 与 Toolchains

- 不要假设直接执行 `mvn` / `./gradlew` 一定使用了项目目标 Java 版本。
- Gradle 会优先读取 `JAVA_HOME`、`JDK11_HOME`、`JDK17_HOME`、`JDK21_HOME`。
- Maven 会优先读取 `~/.m2/toolchains.xml` 或命令行 `-t` 指定的 toolchain 文件。
- 仅验证某个模块或测试失败时，先检查实际使用的 JDK，再决定是否修改代码。

## 发布说明

- Gradle 本地发布建议使用 `./gradlew clean publish`，产物输出到 `build/repos/local-staging`
- Maven 发布 `common-bom` 时使用模块目录下的 `mvn deploy`
- 依赖、版本、发布坐标、adapter 兼容层改动需要同时检查 Gradle 与 Maven

## 仓库治理文件

- [LICENSE](./LICENSE)：开源许可
- [CONTRIBUTING.md](./CONTRIBUTING.md)：贡献、验证与提交流程
- [SECURITY.md](./SECURITY.md)：漏洞报告与安全支持策略
- [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md)：协作行为准则
- [CODEOWNERS](./CODEOWNERS)：目录责任人与默认 reviewer
- [AGENTS.md](./AGENTS.md)：AI 代理工作约束

## 贡献建议

- 修改依赖、发布、adapter、Java 版本相关内容时，同时验证 Gradle 与 Maven
- 提交前优先执行元数据校验与最小必要测试
- 文档、注释默认使用中文；日志、异常、程序输出优先英文
- 避免引入会阻塞 CI 的测试，例如 `Scanner(System.in)`、依赖绝对路径的测试

## English Summary

`common` is a multi-module Java shared library centered on `org.zero:common`.
The mainline targets Java 8 and Spring Boot 2.7 / Spring Cloud 2021, while higher-version compatibility is provided through dedicated adapters and build profiles.

The repository uses Gradle for day-to-day development and version alignment, Maven for release and compatibility verification, and `metadata/build-metadata.toml` as the single source of build metadata.

Before contributing, please read [CONTRIBUTING.md](./CONTRIBUTING.md), [SECURITY.md](./SECURITY.md), and [CODEOWNERS](./CODEOWNERS).
