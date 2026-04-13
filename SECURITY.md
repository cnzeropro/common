# Security Policy / 安全策略

## 当前状态

本仓库的目标状态是使用专用安全邮箱接收漏洞报告。由于专用安全别名尚未在仓库内公开配置，当前临时私密联系方式为：

- `yufa.wang@ronganchina.com`

这是一个过渡方案，用于避免将漏洞报告暴露在公开 issue 中。对外长期发布前，请将该地址替换为专用安全别名。

## 支持版本

| 版本范围         | 支持状态          | 说明          |
|--------------|---------------|-------------|
| `master`     | Supported     | 默认维护分支      |
| 最新 `1.x` 发布线 | Supported     | 仅接受可复现的安全问题 |
| 更早版本         | Not Supported | 需要先复现到受支持分支 |

## 漏洞报告方式

请不要通过公开 issue、公开 PR / MR、评论区或社交渠道披露漏洞细节。

建议按以下方式报告：

1. 使用私密邮件发送到上方地址
2. 标题包含 `Security Report` 或 `Vulnerability Report`
3. 描述受影响版本、复现步骤、影响范围、利用条件和缓解建议
4. 如有 PoC，请最小化并避免包含真实生产数据

## 响应承诺

- 3 个工作日内确认收到报告
- 10 个工作日内完成初步分级
- 30 个自然日内给出修复计划、缓解建议或状态更新

## 披露原则

- 在修复发布前，请不要公开披露漏洞细节
- 维护者会在修复可用后决定公告内容、影响版本和升级建议
- 如果问题不构成安全漏洞，维护者可能将其转为普通缺陷流程处理

## English Summary

Please do not report vulnerabilities through public issues or public pull / merge requests.
Use the private contact address listed above and include affected versions, reproduction steps, impact, and mitigation details.

The current address is a temporary private contact and should be replaced with a dedicated security alias before public release.
