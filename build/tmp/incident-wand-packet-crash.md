# 事故记录：工事选定棒设锚点导致服务端崩溃（已修复部署）

- 崩溃时间：2026-09-23 15:08:39（UTC）/ 23:08:39 CST
- 崩溃报告：`crash-reports/crash-2026-09-23_15.08.39-server.txt`
- 影响：服务端进程退出，0 玩家在线时段之外无数据损失；重启后区块/世界无异常

## 根因

```
io.netty.handler.codec.EncoderException: String too big (was 244 characters, max 200)
  at FriendlyByteBuf.writeUtf
  at FortificationWandPacket.write(FortificationWandPacket.java:55)
  at NetworkManager.sendFortificationWand(NetworkManager.java:501)
  at FortificationAuthoringManager.syncTo(FortificationAuthoringManager.java:763)
  at FortificationWandHandler.onServerTick(FortificationWandHandler.java:105)
  at ForgeEventFactory.onPostServerTick -> MinecraftServer.tick
```

新包 `FortificationWandPacket` 里 `buf.writeUtf(problem, 200)`：`problem` 是预检消息，
带坐标时可达 240+ 字符。`writeUtf` 超长会抛 `EncoderException`；
而这条包是在 **ServerTickEvent 里**发的，异常沿服务端主循环冒泡 → 整服崩溃。

触发路径：管理员设锚点 → `syncTo()` → 预检产生一条较长的提示/报错 → 编码异常。

## 修复（三层）

1. **`FortificationWandPacket`**：新增 `MAX_PROBLEM = 256`，在 record 紧凑构造器里
   `clamp(problem)` 截断；`write`/`read` 都使用同一上限，杜绝再次超长。
2. **`FortificationAuthoringManager.syncTo()`**：整体 `try/catch` —— 任何异常只记日志
   （同一玩家最多 3 条）并退化为发送一个"清空"包，**绝不冒泡到 tick**。
3. **`FortificationWandHandler.handle()`**：交互事件同样运行在 tick 内，整体 `try/catch`，
   异常记日志并忽略（不再取消事件以外的副作用泄漏）。

## 验证

- 重新打包 `espetro-1.1.3-i.jar`（16571121 B）→ 服务端 `mods/espetro-1.1.3-x.jar` + 本地客户端同步
- 启动日志：`Done (3.908s)!`、`工事 JSON v2 已事务冻结: global=5 maps=3 aliases=7 策略剔除=0`
- 启动 ERROR 均为改动前既有项（tacz.zip、RuntimeDistCleaner、vehicle_addition 规则、nukerbomb 属性）
- 崩溃报告目录中仅有 2 条与本次工作相关：14:55 的 mod 版本依赖（已回退 1.1.3-j），
  15:08 的本条（已修复）

## 教训

**任何在服务端 tick（`ServerTickEvent`、交互事件）里发送的网络包，字段长度必须显式设上限并截断，
且发送路径必须 try/catch**；`FriendlyByteBuf.writeUtf(s, max)` 的 `max` 是硬校验，不是自动截断。
