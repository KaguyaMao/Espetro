# 战局可挖掘白名单（可配置、无掉落）—— 设计方案（待审批）

> 目标：在战局（部署/战斗）里，允许玩家用**原版生存方式**挖掉指定方块；默认名单 = 所有门 + 活板门 + 玻璃；
> 这些方块**不产生掉落物**。名单可配置。

---

## 1. 关键机制（已用字节码核实，不是推测）

`ServerPlayerGameMode.destroyBlock(BlockPos)` 的实际流程：

```
ForgeHooks.onBlockBreakEvent(...)          // 触发 BlockEvent.BreakEvent（可取消）
canHarvestBlock(...)  → istore 8           // flag1 = 能否采集
itemstack.mineBlock(...)                   // 工具耐久照扣
removeBlock(pos, ...) → istore 9           // flag = 方块是否被移除
if (flag && flag1)  → Block.playerDestroy(...)   // ← 掉落物与经验只在这里产生
```

**结论**：`removeBlock`（方块消失）**与** `canHarvestBlock` 无关；`canHarvestBlock=false` 只会让
`playerDestroy` 不执行 → **方块照旧被挖掉，但没有掉落物、没有经验**。

现有 `BattlefieldMiningHandler` 之所以叠加三层（HarvestCheck 归 false + BreakSpeed 归 0 +
BreakEvent 取消），是为了做到"**完全挖不动**"。本次只需要**放开后两层、保留第一层**，就得到
"能挖掉、但什么都不掉"——正好是需求。

另外核实：Forge 1.20.1 的 `net.minecraftforge.event.level` 里**没有** `BlockDropsEvent`，
所以掉落只能通过 `HarvestCheck` 这条路控制，与上面的方案吻合。

## 2. 配置载体：方块标签（推荐）

新增方块标签 **`#espetro:minable_in_battle`**，默认内容随 mod 发布：

`src/main/resources/data/espetro/tags/blocks/minable_in_battle.json`

```json
{
  "replace": false,
  "values": [
    "#minecraft:doors",
    "#minecraft:trapdoors",
    "minecraft:glass",
    "minecraft:tinted_glass",
    "minecraft:glass_pane",
    "minecraft:white_stained_glass", "minecraft:orange_stained_glass",
    "minecraft:magenta_stained_glass", "minecraft:light_blue_stained_glass",
    "minecraft:yellow_stained_glass", "minecraft:lime_stained_glass",
    "minecraft:pink_stained_glass", "minecraft:gray_stained_glass",
    "minecraft:light_gray_stained_glass", "minecraft:cyan_stained_glass",
    "minecraft:purple_stained_glass", "minecraft:blue_stained_glass",
    "minecraft:brown_stained_glass", "minecraft:green_stained_glass",
    "minecraft:red_stained_glass", "minecraft:black_stained_glass",
    "minecraft:white_stained_glass_pane", "…（16 种染色玻璃板全部列出）"
  ]
}
```

（`1.20.1` 只有 `doors / wooden_doors / trapdoors / wooden_trapdoors / fence_gates`，
**没有**玻璃标签，所以玻璃 35 个 id 逐个列：2 种玻璃 + 16 染色玻璃 + 玻璃板 + 16 染色玻璃板。
模组的门/活板门只要自己 tag 进了原版标签就会自动包含。）

**可配置性**（不用重启，`/reload` 生效）：
- 数据包：在自己的包里加同名标签文件追加/覆盖（`"replace": true` 即整体替换）。
- **kubejs**（你常用的方式）：
  ```js
  ServerEvents.tags('block', e => {
    e.add('espetro:minable_in_battle', 'minecraft:oak_fence_gate')    // 追加
    e.remove('espetro:minable_in_battle', 'minecraft:tinted_glass')   // 移除
  })
  ```

> 备选载体：`config/espetro/battle_mining.json`（服务端 JSON，风格同 `fortifications.json`），
> 但**需要重启**才生效、且不能像标签那样被数据包/kubejs 自然增量修改。除非你更偏好 JSON 文件，否则用标签。

## 3. 代码改动（只改 `team/BattlefieldMiningHandler`）

```java
private static final TagKey<Block> MINABLE = TagKey.create(Registries.BLOCK,
    ResourceLocation.fromNamespaceAndPath(Espetro.MOD_ID, "minable_in_battle"));

private static boolean whitelisted(BlockState state) { return state.is(MINABLE); }

// 1) HarvestCheck：保持 canHarvest=false（白名单也一样）→ 一律无掉落/无经验
//    （白名单只决定"能不能挖掉"，不决定掉落）
// 2) BreakSpeed：白名单方块**不归零**（保留原版挖掘速度）→ 客户端正常显示裂纹
// 3) BreakEvent：白名单方块**不取消**，并 setExpToDrop(0) 兜底；其它照旧取消
```

三处都是"加一个白名单条件"，无新事件、无网络包改动。

## 4. 行为细节

| 场景 | 行为 |
|---|---|
| 挖门 / 活板门 / 玻璃（白名单） | 正常速度挖掉、有音效与粒子、**无掉落物、无经验**；双格门两半都消失、都无掉落 |
| 挖其它方块 | 与现在完全一致：挖不动（速度 0 + 事件取消） |
| 创造力/旁观 | 不受限（原逻辑 `instabuild`/spectator 直接放行） |
| 非战局阶段 / 非战场维度 | 不受限（原逻辑） |
| 爆炸 | 本来就不经挖掘限制 → 白名单方块会被炸掉；**爆炸掉落默认保持原版**（会掉） |
| 挖掘疲劳/工具 | 已不使用疲劳；工具耐久照扣（`mineBlock` 在掉落判定之前） |

## 5. 需要你确认（5 项）

1. 配置载体：**方块标签**（推荐，`/reload` 生效、kubejs 可改）还是 `config/espetro/*.json`（重启生效）？
2. 默认名单是否还要含 **栅栏门**（`#minecraft:fence_gates`）？（你没提，我默认不加）
3. **爆炸**摧毁这些方块时是否也要"无掉落"？（默认保持原版=会掉落；要改需额外钩子）
4. 是否需要**第二档白名单**："可挖且正常掉落"（例如允许砍树叶掉树苗）？默认不做。
5. 是否限制角色（例如只有工兵能挖）？默认：任意生存玩家都能挖白名单方块。

## 6. 交付与部署（审批后）

1. 新增标签 JSON + 改 `BattlefieldMiningHandler`；
2. `gradlew build` → 上传服务端 `mods/espetro-1.1.3-x.jar`、同步你的客户端；
3. **服务端需重启**（服务端逻辑变更）；**客户端也要换新 jar**（裂纹/进度在客户端预测）——协议不变、不强制升级；
4. 验证清单：挖门/活板门/玻璃（消失且无掉落）→ 挖石头（仍挖不动）→ `/reload` 后用 kubejs 增删 →
   再挖验证立即生效 → 爆炸行为确认 → 创造模式不受限。

---

## 7. 变更记录：加入全部草（2026-09-25）

**需求**：把"所有的草"加入战局可破坏名单。

**实施方式**：不动 mod、不重启 —— 新增 kubejs 数据包标签覆盖文件
`kubejs/data/espetro/tags/blocks/minable_in_battle.json`（`"replace": false` → 与模组内置 38 项**取并集**），
`/reload` 立即生效。（模组源码里的同名标签也同步加上了这几项，供以后重编 jar 时保持一致。）

**加入的 6 个方块**（先用临时探针枚举了服务端**全部已注册方块**，确认本服**没有任何模组草**，
`BlockProbe` 命中 25 项里除苔石/盆栽/浆果丛外，草类只有这 6 个）：

| ID | 中文 |
|---|---|
| `minecraft:grass` | 矮草（1.20.1 的短草） |
| `minecraft:tall_grass` | 高草 |
| `minecraft:fern` | 蕨 |
| `minecraft:large_fern` | 大型蕨 |
| `minecraft:seagrass` | 海草 |
| `minecraft:tall_seagrass` | 高海草 |

**未加入**（属于"植被"但不是草，需要的话说一声）：
- `minecraft:grass_block`（草方块，地形；加入等于允许挖地）
- `minecraft:dead_bush`（枯死的灌木）、`minecraft:rose_bush` / `minecraft:sweet_berry_bush`（灌木）
- `minecraft:moss_block` / `minecraft:moss_carpet`（苔藓）、`minecraft:mossy_*`（苔石系）
- 藤蔓/发光地衣/下界根须/花（`#minecraft:replaceable_by_trees` 可一次覆盖除草方块外的全部植被）

**验证**：用一次性 KubeJS 探针枚举 `#espetro:minable_in_battle` 的**有效成员 = 83 项**
（原有玻璃/门/活板门/栅栏门全部保留，Create 的门也经原版标签并入，6 种草均在列）；探针已停用（改名 `.disabled`）。

