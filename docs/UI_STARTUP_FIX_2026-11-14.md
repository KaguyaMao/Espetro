# 客户端启动失败修复 · 2026-11-14

## 原因

09:29:47 的启动日志明确报告 `MixinTargetAlreadyLoadedException`：Espetro 新增的 `client.FancyMenuBundleMixin` 准备注入 `de.keksuccino.fancymenu.FancyMenu` 时，该主类已被提前加载。这是上一轮菜单自动覆盖入口引入的启动回归。此前的编译和单元测试没有覆盖实际 Mixin 准备顺序。

## 修复

删除 `FancyMenuBundleMixin` 源码与注册，不再向 FancyMenu 主类注入。菜单安装器改由已有 `EspetroMixinPlugin.onLoad()` 调用，严格检查 CLIENT；安装器只依赖文件 API 与日志，不加载 Minecraft 或 FancyMenu 类。早期调用的链接异常被记录，正常客户端初始化提供幂等重试，避免菜单磁盘操作导致启动终止。

保留所有内置菜单、五项视觉修改、原有补给 PNG 素材、自动备份和差异覆盖。服务端不执行菜单安装。

## 验证

- 构建与本地 Maven 发布成功，38 项相关测试通过。
- 生产客户端使用本机原版启动配置、真实 mods 和资源，以离线测试身份 `EspetroUITest` 直接运行；未使用用户账号令牌。
- 09:50:20 菜单安装器成功执行，已有283文件无需更新。
- 09:51:07 资源重载完成，FancyMenu `title_screen` 注册成功；ModernFix 记录启动耗时约 62.809 秒。
- 原 `MixinTargetAlreadyLoadedException`、Mixin 准备失败不再出现。本次只验证启动恢复，未进入对局测试 UI 交互；离线测试进程已关闭。
- Windows 窗口工具因“failed to write kernel assets”不可用，因此未获取实际截图。无需把日志上传第三方服务，本地日志已能明确定位。
- 已安装 jar 与 Maven 产物 SHA256 一致；ZIP 校验及283个内置菜单资源哈希通过，旧错误 mixin 类与注册均不在成品中。

客户端已替换：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`

SHA256：`67ba80ea377a8998949d8ef01bd2b4701627a5fe7625699346a193f6513afb99`

故障 jar、原始 latest/debug 日志及修改前源码保存在：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\espetro-backups\20261114-094513-before-startup-fix`

完整测试、产物与启动记录见 [JSON 核验](UI_STARTUP_FIX_2026-11-14.json)。
