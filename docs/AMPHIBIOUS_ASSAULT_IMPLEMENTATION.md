# 两栖登陆（Amphibious Assault）P0 实现与使用说明

> 目标：让一张普通侵攻地图变成「冲滩图」——地图加载时自动把一两艘舰船粘到指定位置，
> 攻方出生点与攻方载具出生点都落在舰上，舰体周围生成屏障，其余战斗流程不变。
>
> 设计背景与取舍见 `docs/AMPHIBIOUS_ASSAULT_PLAN.md`；本文只写**已经实现**的部分。

## 1. 玩家看到的流程

1. 打进 `island` 这类冲滩图 → 地图激活时：

   ```
   战场关键区块开始分批预载: N 个
   已粘贴舰船 lhd_01（espetro:naval/lhd_placeholder）: anchor=..., size=..., blocks=23790
   两栖地图已就绪: Island（舰船 2 艘，屏障 5819 格，载具类型 9 种，甲板出生点 1188.5,27.0,2113.5）
   两栖载具点已合并: [mbt, mgs, ifv, acv, apc, car, truck, supply_truck, transport_helicopter]
   两栖攻方出生点已覆盖: 1188.5, 27.0, 2113.5 (yaw 180.0)
   ```

2. 攻方（ATTACK）玩家在甲板上出生，载具在甲板上的 16 个固定点位刷新。
3. 舰体外围一圈屏障（默认高 24 格、带顶），把玩家关在舰上；开打时由既有的
   「攻方等待区屏障」逻辑统一清除（`GameStateManager.placeAttackWaitingBarrier`）。

## 2. 配置文件：`EsWorld/<地图>/EsConfig/amphibious.json`

**可选文件**。不存在、或 `enabled=false`，该地图就是普通侵攻图。
解析失败/校验不过会写进 `ActiveMapConfig` 的拒载原因，日志关键词 `amphibious:`。

```jsonc
{
  "enabled": true,
  "assault_faction": null,        // P0 仅解析，尚未锁定选编制（P1 使用）
  "beach_direction": 0.0,         // 冲滩方向（度），P0 未使用，留给 P1 的引导提示
  "water_surface_y": 12,          // 地图海面世界 Y，用于干船坞校准（P0 仅记录）

  "ships": [
    {
      "id": "lhd_01",
      "role": "lhd",                        // lhd | destroyer
      "structure": "espetro:naval/lhd_placeholder",
      "source": "builtin",                  // builtin(走结构管理器) | config(config/espetro/naval_structures) | world(已烘进地图)
      "draft_adjust": 0,                    // 吃水微调（格），正数上浮
      "anchor": { "x": 1158, "y": 12, "z": 2100 },   // 结构**最小角**的世界坐标
      "rotation": "none",                   // none | clockwise_90 | clockwise_180 | counterclockwise_90
      "barrier": { "mode": "box", "height": 24, "cap": true },  // box | hull | none
      "spawn_from_markers": false,          // true=用结构里的标记块定位出生点（P1）
      "solid": false,                       // P0 未使用（预留给「不可穿透舰体」）
      "water_surface_y": -1                 // 单舰覆盖地图级海面
    }
  ],

  "override_spawn": {
    "attack": { "x": 1188.5, "y": 27, "z": 2113.5, "yaw": 180 },
    "vehicles": {
      "mbt": [ { "id": "mbt_1", "attack": { "x": 1159.5, "y": 27, "z": 2105.5, "yaw": 0 } } ]
    }
  }
}
```

### 关键语义

| 字段 | 语义 | 注意 |
|---|---|---|
| `anchor` | 结构模板**最小角**（与 `StructureTemplate.placeInWorld` 一致） | 写错 30 格就会整船偏移 |
| `draft_adjust` | 实际 Y = `anchor.y + draft_adjust` | 船浮太高/太低只调这个 |
| `barrier.mode=box` | 沿结构包围盒四壁 + 可选顶盖铺 `minecraft:barrier` | 包围盒按旋转后计算 |
| `override_spawn.attack` | 攻方出生点（甲板站立方块） | **必须**给，否则该图按普通侵攻运行 |
| `override_spawn.vehicles` | 按类型替换攻方载具点；`defend` 省略则复制 `attack` | 编制槽位按数组顺序取点，数量必须够 |

## 3. 岛图（island）当前配置

| 项 | 值 |
|---|---|
| 地图 | `island`（`EsWorld/island/EsConfig/`） |
| 防守方出生点 | `2384, 42, 578`（写在 `spawn_points.json`，本功能不动） |
| 两栖攻击舰 | 锚点 `1158, 12, 2100`，尺寸 `61×15×26`，甲板顶 `y=26`，占 `x 1158..1218 / z 2100..2125` |
| 攻方出生点 | `1188.5, 27, 2113.5`，yaw 180（甲板正中，对 `x` 居中） |
| 驱逐舰 | 锚点 `1051, 18, 1828`，尺寸 `21×9×7`，甲板顶 `y=26`，无屏障（装饰） |
| 攻方载具点 | 16 个：mbt 2 / mgs 1 / ifv 2 / acv 2 / apc 2 / car 1 / truck 2 / supply_truck 2 / transport_helicopter 2 |
| 点位排布 | 两排 `z=2105 / 2116`，`x=1159..1201` 步长 6（横向间距 6 格，纵向 11 格） |
| 舰船屏障 | 四壁 + 顶盖，高 24 格 |

生成脚本：`build/tmp/gen-naval.py`（同时产出 `build/tmp/island-amphibious/amphibious.json`）。
占位舰是纯立方体（灰混凝土船体 + 铁块甲板），只为先把流程跑通，后续换成真模型只需替换 nbt。

## 4. 生效方式

1. 上传 `build/tmp/island-amphibious/amphibious.json` → 服务端 `EsWorld/island/EsConfig/amphibious.json`；
2. 上传 `build/libs/espetro-1.1.3-l.jar` → `mods/espetro-1.1.3-x.jar`（内含 `data/espetro/structures/naval/*.nbt`）；
3. **重启服务器**（`EsConfig` 只在启动时读取，`/reload` 无效）；
4. 客户端**无需更新**（本功能不改协议、不改客户端渲染，`PROTOCOL_VERSION` 未变）。

## 5. 验收清单

- [ ] 启动日志出现 `已粘贴舰船 lhd_01` 与 `两栖地图已就绪`
- [ ] 观察者传送到 `1188 27 2113` 能看到立方体船体与铁块甲板
- [ ] 攻方玩家出生在甲板（不是原地图攻击方出生点）
- [ ] 舰船周围有屏障，玩家出不去；开打后屏障消失
- [ ] `veh list` / 载具刷新日志显示 16 个攻方载具点，且都在甲板上
- [ ] 阵亡重生仍在甲板，不落水

## 6. 已知限制（P0）

- 舰船是**纯立方体占位**，不是 LHD 模型；
- 世界里的舰体是「结构方块粘贴」，**地图保存后不会回写**（每次开图重贴，`source=world` 可改为烘进模板）；
- `assault_faction` 只解析未锁定（P1）；
- 甲板上的载具是普通载具（不是两栖专属编制里的特殊载具）；
- 屏障是 `minecraft:barrier` 全包围，**对飞行载具无效**（直升机可直接飞出去）——P1 需要额外限制。
