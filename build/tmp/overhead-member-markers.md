# 普通玩家头顶标识 —— 已实现（第三轮调整尚未部署）

时间：2026-09-24（22:17 首版 → 22:56 调整 → 第三轮：1/2 大小 + 再抬高）
改动文件：`client/LeaderOverheadRenderer.java`（纯客户端渲染，无网络包改动，协议仍 1.35）
新增资源（你提供）：`assets/espetro/textures/gui/overhead/fireteammate_b.png` / `fireteammate_c.png`
产物：**已编译通过（`compileJava` BUILD SUCCESSFUL）**；按你要求**尚未打包上传/部署**

## 第三轮调整（未部署）

| 反馈 | 改动 |
|---|---|
| 1/3 → **1/2** | `HALF_MEMBER = HALF / 2f = 0.07f`（世界宽 **0.14**，正好是队长标识 0.28 的 1/2） |
| 再略微提高高度 | `MEMBER_LIFT = 0.05`（原 0.01）→ 图标**底边** = nametag 顶边 + 0.05 |
| 队长/组长/指挥官 | 未改动 |

几何结果：
- nametag 顶边 = `getNameTagOffsetY()` = `bbHeight + 0.5`；
- 成员标识中心 = `bbHeight + 0.5 + 0.05 + 0.07` = **`bbHeight + 0.62`**
  （上一版 1/3 时是 `bbHeight + 0.557`，**整体高了约 0.063**）；
- 队长类仍是 `bbHeight + 1.0`，不变。

字节码核对：`HALF_MEMBER = 0.07f`、`MEMBER_LIFT = 0.05d`、仍调用 `Entity.getNameTagOffsetY()` ✓

## 第二轮反馈调整（22:56，已部署）

| 反馈 | 改动 |
|---|---|
| 1/5 太小 | 普通成员标识改为队长标识的 **1/3**（`HALF_MEMBER = HALF / 3f = 0.0466667`，世界宽 0.093） |
| 位置要低、贴着 nametag 上沿 | 成员标识中心 = `entity.getNameTagOffsetY() + 0.01 + HALF_MEMBER`；队长类仍是 `bbHeight + 1.0` 不变 |
| 队长/组长/指挥官不变 | 未改动这三类的贴图、尺寸、高度 |

**nametag 几何（实测原版字节码，非估算）**：`EntityRenderer.renderNameTag` 把画框平移到
`(0, entity.getNameTagOffsetY(), 0)` 后按 `scale(-0.025,-0.025,0.025)` 绘制、文本从锚点向下 **0.225**，
即 **nametag 顶边 = `getNameTagOffsetY()`（= `getBbHeight() + 0.5`，`Player/LivingEntity` 均未覆写）**。
所以成员标识底边 = `顶边 + 0.01`，正好贴在名字牌上方一点。

## 按你的确认实现

| 规则 | 实现 |
|---|---|
| A 组组长 = 小队长 | 不单独做；`resolve()` 里 `leader` 分支优先返回 SQUAD_LEADER，A 组组长天然走那条 |
| 其它小队成员 | `squad_leader.png`，**不画数字** |
| 本队 A 组（fireteam=0）成员 | `self_squad_leader.png`，不画数字 |
| 本队 B 组（=1）成员 | `fireteammate_b.png`（新） |
| 本队 C 组（=2）成员 | `fireteammate_c.png`（新） |
| 尺寸 | 普通成员 `HALF_MEMBER = HALF/2 = 0.07`（世界宽 0.14）＝队长标识 0.28 的 **1/2** |
| 高度 | 普通成员位于 nametag 顶边上方 0.05（中心 `bbHeight + 0.62`）；队长类不变（`bbHeight + 1.0`） |
| 距离 | **全部标识全距离可见**（含本队、含原有指挥官/小队长/组长），已删除原来的距离剔除（200/50/50） |
| 车内 | **不画普通成员标识**，车内仍只保留"军衔最高者"的指挥官/小队长/组长标识 |
| 数字 | 仅小队长画小队编号，普通标识天然无数字 |

补充说明：
- 数据来源不变（服务端只下发**己方阵营**小队名单）→ 不会给敌人显示标识。
- 无小队玩家没有分组数据 → 不显示标识（无 `squadId` 无法分类）。
- 每车只画一个标识的"最高军衔"排序扩展为：指挥官 0 < 小队长 1 < 组长 2 < 普通成员 3。

## 验证

- `gradlew build` 成功；jar 内已含两张新贴图与更新后的渲染类：
  `LeaderOverheadRenderer$T` 含 `COMMANDER/SQUAD_LEADER/FIRETEAM_LEADER/MEMBER`；
  字段 `HALF_MEMBER`、`FIRETEAMMATE_B_TEX`、`FIRETEAMMATE_C_TEX` 存在；旧的 `inRange` 已移除。
- 上传时 0 人在线。

## 部署与验证建议

- **这是纯客户端渲染功能**，服务端 jar 已同步上传但**不必立刻重启**（当前跑着的服务端不受影响）。
- **玩家必须换成新客户端 jar 才能看到普通成员标识**（贴图+渲染都在客户端；协议没变，旧客户端不会报错，
  只是看不到）。你自己这台已同步。
- 进服实测：找一个小队，A/B/C 组各站一个人 + 另一支小队的人，确认：
  1) 四种贴图正确（其它小队=squad_leader、本队A=self_squad_leader、B=fireteammate_b、C=fireteammate_c）；
  2) 尺寸约为队长标识的 1/5，且都不带数字；
  3) 远距离（100+ 格）仍可见（全距离）；
  4) 车里只有指挥官/队长/组长有标识，普通成员没有；
  5) 敌人（对方阵营）完全没有标识。
