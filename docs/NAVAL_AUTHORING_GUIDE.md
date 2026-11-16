# 舰船编排（多卷）使用教程

> 本文对应 `/espetro naval` 系列指令。设计文档见 `docs/NAVAL_AUTHORING_PLAN.md`，
> 两栖作战整体方案见 `docs/AMPHIBIOUS_ASSAULT_PLAN.md`。

## 0. 前置

- 执行者需要 OP（权限等级 2）。
- 舰船可以在**任意维度**里搭好（推荐主城/编辑维度），导出时只读该维度的方块，不影响玩家存档以外的东西。
- 导出后船体存在服务端 `config/espetro/naval_structures/`，与地图存档分离；地图只在开图时粘贴。

## 1. 拿选定棒（进入舰船编排模式）

```
/espetro naval wand
```

- 左键 = 选区角 A
- 右键 = 选区角 B
- 潜行 + 右键 = 锚点（吃水线参考点）
- 潜行 + 左键 = 清空选区

进入该模式后，手里的「工事选定棒」点击归舰船编排，不再修改工事模板。
退出用 `/espetro naval cancel`（保存成功后也会自动退出）。

## 2. 打标记

站在**甲板上**想要攻方出生的位置：

```
/espetro naval marker deck
```

站在每一个载具出生位：

```
/espetro naval marker veh <type> <index>
```

- `<type>` 与 `VehSpawn.json` 的类型名一致：`mbt / mgs / ifv / acv / apc / car / truck / supply_truck / transport_helicopter`
- `<index>` 从 0 开始，同类型多台车就用 0、1、2…
- 标记是 `minecraft:structure_block`（数据模式 + 元数据 `espetro:attack_spawn` / `espetro:veh:<type>:<index>`），导出时会从方块数据里读出来，**不会**被写进船体方块。

清掉所有标记：

```
/espetro naval marker clear
```

## 3. 导出

```
/espetro naval save <name>
```

- `<name>` 只允许小写字母/数字/下划线，1–48 字符。
- 可选参数：`carve`（把选区内的空气写成结构空位，粘贴时挖空）、`noentities`（不保存实体）。
- 输出：`config/espetro/naval_structures/<name>/`
  - `manifest.json`：整船尺寸、锚点、pivot、块表（每块 `file/offset/size/blocks/markers`）
  - `part_000.nbt`、`part_001.nbt`…：三轴按 128 格分块，块坐标即 `offset`
- 旧版本自动备份为 `<name>.bak-<时间戳>`；导出失败会删掉 `.tmp` 并保留旧版。

查看 / 删除：

```
/espetro naval list
/espetro naval info <name>
/espetro naval delete <name>
```

## 4. 限额

导出按 **128×128×128 的三维方块** 切分，长宽高**都不设上限**（只受总格数与块数约束）：

| 范围 | 上限 |
|---|---|
| 单块边长 | 128 格（三轴各自） |
| 单块方块 | 2,000,000 |
| 整船长宽高 | 不限 |
| 整船方块 | 40,000,000 |
| 块数 | 512 |
| 标记数 | 256 |

例如 300×40×30 的船 = 3×1×1 = 3 块；1000×200×300 = 8×2×3 = 48 块。

超过时 `save` 会直接报错并提示超的是哪一项。

## 5. 接入地图

编辑 `EsWorld/<地图名>/EsConfig/amphibious.json`：

```json
{
  "enabled": true,
  "water_surface_y": 26,
  "assault_faction": "plamc_5th_amphibious_assault",
  "ships": [
    {
      "id": "lhd_01",
      "role": "lhd",
      "structure": "espetro:naval/my_lhd",
      "anchor": { "x": 1158, "y": 12, "z": 2100 },
      "rotation": "none",
      "barrier": { "mode": "box", "height": 24, "cap": true }
    }
  ]
}
```

- `structure`：`espetro:naval/<name>`，指向第 3 步导出的目录
- `anchor`：该船**最小角**在战场里的坐标（粘贴时 `anchor + 卷偏移`）
- `rotation`：`none` / `clockwise_90` / `180` / `counterclockwise_90`
- `override_spawn` 可以整段删掉——甲板出生点与载具点会由第 2 步的标记自动生成；
  若保留 `override_spawn`，标记优先级更高（标记先写入，`override_spawn` 只补空缺）。

改完 `amphibious.json` 需要**重启服务器**（EsConfig 只在开服时读取）。

## 6. 常见问题

- **导出报“区块未加载”**：选区太大，站在选区中心等周边区块加载完再 `save`。
- **保存后地图里没变化**：`amphibious.json` 只在开图时读取；确认 `enabled`、`structure`、`anchor` 正确，并重启服务器。
- **船没刷出来**：看服务端日志 `舰船 <id> 加载失败: …`；常见是 `structure` 名字写错或目录里缺 `manifest.json`。
- **守方载具跑到船上**：`VehSpawnMerge` 只在覆盖点缺 `defend` 时回退到岛内基础点；确认 `override_spawn` 没有被写成 `attack` 单侧。
- **框选提示“超过单轴上限 64，已撤销该角”**：这是**工事**选定棒的逻辑（工事模板单轴上限 64），说明当前没在舰船编排模式。
  先执行 `/espetro naval wand`，再左键角 A、右键角 B；成功后消息前缀是 `[舰船编排]`。
  该模式记录在玩家数据里，重启/重进不会丢；`/espetro naval cancel` 退出。
- **`/espetro naval wand` 之后点击仍走工事逻辑**：确认账号有管理员权限（`hasPermissions(2)`），且手持的是该指令发出的选定棒；可再执行一次 `/espetro naval wand` 刷新模式。
