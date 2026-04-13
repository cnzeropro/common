# Contributing Guide / 贡献指南

本文档用于统一 `common` 仓库的人类协作者与自动化代理协作方式。仓库默认以中文文档为主，同时提供必要的英文摘要，保证在 Gitee、GitHub、GitLab 等平台上都有一致的贡献入口。

## 1. 环境准备

- JDK：主线 Java 8，建议同时准备 Java 11、17、21，用于 profile / adapter 校验
- Gradle：优先使用仓库内 `./gradlew`
- Maven：优先使用仓库内 `./mvnw`
- Toolchains：如需多版本 Maven 编译，优先通过 `~/.m2/toolchains.xml` 或 `-t` 指定

在修改代码前，请先确认当前 shell 实际选中的 Java 与目标模块要求一致。

## 2. 分支与提交建议

- 分支命名建议：`feat/...`、`fix/...`、`refactor/...`、`docs/...`、`chore/...`
- 提交信息建议采用 `type(scope): summary` 风格
- 一次提交应尽量只覆盖一个明确主题，避免把依赖升级、行为变更和格式化混在一起

## 3. 选择 Gradle 还是 Maven

- 日常开发、快速验证、版本对齐：优先 Gradle
- 发布、聚合兼容性、POM 侧验证：必须补充 Maven 校验
- 涉及依赖版本、发布坐标、adapter、Java profile 的变更：Gradle 与 Maven 都必须检查

## 4. 提交前最低校验

### 必做校验

```bash
./scripts/verify-metadata.sh
./scripts/verify-gradle.sh
./scripts/verify-maven.sh
```

### 建议使用统一入口

```bash
./scripts/ci/verify.sh
```

### 针对性测试

- 修改 `common-data` 时，补充该模块相关测试
- 修改 `core-base` 时，补充该模块相关测试
- 修改发布、元数据或 adapter 时，记录你使用的 JDK 与命令

## 5. 文档与语言规则

- 默认语言：简体中文
- 代码注释：中文为主，保留必要英文术语
- 异常信息、日志、程序输出：原则上使用英文
- 根级治理文档：中文主内容 + 英文摘要

## 6. 已知测试陷阱

以下测试或模式在默认 CI 中应避免直接纳入稳定校验入口：

- 使用 `Scanner(System.in)` 的测试
- 依赖绝对路径的测试
- 输出到固定目录、依赖人工交互或 benchmark 环境的测试
- 文档中已注明的高风险测试，如 `SocketPingClientTest`、`JmhTests`、`ExcelUtilTest`

## 7. Pull Request / Merge Request 清单

- [ ] 改动范围清晰，说明了影响模块
- [ ] 记录了使用的 JDK / toolchain
- [ ] 如涉及依赖、发布、adapter、Java 版本，已同时验证 Gradle 与 Maven
- [ ] 如涉及元数据，已执行 metadata 校验
- [ ] 如涉及公共行为，补充了测试或说明未补测原因
- [ ] 如涉及文档或配置，已同步更新对应说明

## 8. 沟通原则

- 普通问题、使用咨询、功能建议：走 issue
- 代码实现与行为变更：走 PR / MR
- 安全漏洞：不要公开提交 issue，请遵循 [SECURITY.md](./SECURITY.md)

## English Summary

Use Gradle for day-to-day development and Maven for release-oriented or compatibility verification.
If your change touches dependencies, publishing, adapters, or Java version profiles, you must verify both Gradle and Maven.

Before opening a PR or MR, run metadata verification, record the Java version you used, and avoid tests that rely on interactive input or absolute file paths.
