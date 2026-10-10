# 编制载具「部署自带物品/弹药」配置说明（2026-09-23）

## 默认行为：自动套用载具补给站配置
编制载具**部署时默认自动装满补给站配置里的弹药**，数据来自服务器
`kubejs/data/dragonrise_reforge/supply_station/*.json`（和载具补给站用的是同一份配置，
服务端 `/reload` 后立即生效，不需要重启；`/reload` 只对*这个*配置有效，
`EsFactions` 本身仍是启动时冻结）。

规则（与补给站实体一致）：
1. 该载具类型在 `VehicleOverrides` 里有专属配置 → 用它；否则用 `Default`。
2. 只给**该载具武器实际用到**的弹药（遍历座位→武器→弹药消费者），不会塞用不上的弹药。
3. `FIXED` 模式 → 按 `FixedAmount` 数量给；`PACKAGE` 模式 → 给 `customItem × customItemCount`；
   `MAGAZINE` 模式按 `FixedAmount` 给（本包所有弹药 `loadAmount = 1`，所以"发数=物品数"）。
4. 额外给 `BonusItem × BonusItemCount`（如 `superbwarfare:medical_kit ×2`），
   想关掉就在补给站配置里把 `BonusItem` 设为 `""`（补给站也一起不再给）。

当前会带的东西（示例）：
- `dragonrise_reforge:zbd04a`：small_shell_ap 200 / small_shell_he 300 / rifle_ammo 500 / large_shell_he 22 / ATGM 4 / 医疗包×2
- `dragonrise_reforge:ztz99a`：large_shell_ap 21 / large_shell_he 18 / ATGM 4 / rifle_ammo 500 / heavy_ammo 500 / 医疗包×2
- 没有专属覆盖的车型（Stryker 系列、Z-20、MV3、URAL 等）走 `Default`，其中该车用不到的弹药会被过滤掉

## 手动覆盖 / 关闭
| 写法 | 效果 |
|---|---|
| 载具节点里写 `"items": [...]` | 该车型**改用**手写清单，不再套补给站配置 |
| 载具节点里写 `"supply_loadout": false` | 该车型**不带弹药**出生（别名 `supplyLoadout` / `auto_ammo` / `autoAmmo`） |

## 字段
写在 `EsFactions/<编制>.json` → `vehicles` → 每台载具节点里：

| 字段 | 类型 | 说明 |
|---|---|---|
| `items` | 字符串数组 | 部署时写入**载具集装箱（物品栏）**的物品；别名 `cargo` / `vehicle_items` / `vehicleItems` |
| `supply_loadout` | 布尔 | 是否自动套用补给站弹药配置，默认 `true` |
| `nbt` | 字符串 | 部署时应用的 SNBT；**目前只认 `Energy`**（int，走 Forge 能量能力充满），其它键会被忽略并打警告 |

- 物品字符串语法与 `/give`、职业技能 `commands` **完全一致**：`<物品id>[<SNBT>] <数量>`，数量可省略（默认 1）。
- 载具级 `items` 会被同型号的每个部署点继承；未单独指定时使用载具级配置。
- `nbt` 不写＝**默认满电**。

## 示例
```json
"vehicles": {
  "mbt": {
    "display_name": "ZTZ-96A 主战坦克",
    "entity": ["dragonrise_reforge:ztz96a"],
    "per_max_count": 1,
    "respawn_minutes": 15,
    "troop_value": 15,
    "max": 100,
    "initial_deploy_delay_seconds": { "attack": 1200, "defend": 1200 },
    "vehicle_crew_seats": 3,
    "nbt": "{Energy:5000000}",
    "items": [
      "superbwarfare:large_shell_ap 12",
      "superbwarfare:large_shell_he 6",
      "superbwarfare:medium_anti_ground_missile 4",
      "superbwarfare:repair_tool{Energy:100000}",
      "superbwarfare:medical_kit 2"
    ]
  },
  "ifv": {
    "display_name": "ZBD-04A 步兵战车",
    "entity": ["dragonrise_reforge:zbd04a"],
    "items": [
      "superbwarfare:small_shell_ap 200",
      "superbwarfare:small_shell_he 100",
      "superbwarfare:large_shell_he 20",
      "superbwarfare:repair_tool{Energy:100000}"
    ]
  }
}
```

## 集装箱容量（由载具数据里的 `VehicleContainerType` 决定）
| 类型 | 格数 |
|---|---|
| Mini | 9 |
| Small | 27 |
| Medium | 54 |
| Large | 78 |
| Huge | 102 |

装不下的部分会被丢弃，日志里出现一条 `载具部署：集装箱装不下 …`（不影响载具生成）。
同一个物品数量超过单格堆叠上限时会自动分到多格。

## 注意事项
1. **EsFactions 是启动时冻结的**：改完 JSON 必须**重启服务器**才生效（`/reload` 不够）。
2. `items` 只写物品栏，**不会**去碰 SBW 的 `Entity#load`，所以不存在"残缺 NBT 把载具血量清零"的问题。
3. 首发、手动部署、自动刷新生成的载具都会带上（同一个生成函数）。
4. 已有载具不会被追溯补货；补给/换装仍走原有补给站逻辑。
