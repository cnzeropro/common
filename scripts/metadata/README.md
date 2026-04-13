# metadata scripts

公开入口：

- `sync.{bat,js,ps1,py,sh}`：同步 `metadata/build-metadata.toml`，默认同时处理 Gradle 和 Maven
- `verify.{bat,js,ps1,py,sh}`：校验 `metadata/build-metadata.toml`，默认同时校验 Gradle 和 Maven
- 两类入口都支持位置参数 `gradle` 或 `maven`，用于只处理单侧投影

内部实现：

- `internal/manage.*`：共享运行器，负责编译并调用 Java CLI
- `internal/cli/`：Java CLI 实现目录

推荐用法示例：

```bash
./scripts/metadata/sync.sh
./scripts/metadata/sync.sh gradle
./scripts/metadata/verify.sh maven --verbose
powershell -ExecutionPolicy Bypass -File scripts/metadata/sync.ps1 maven
powershell -ExecutionPolicy Bypass -File scripts/metadata/verify.ps1 gradle --verbose
```
