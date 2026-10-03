# 工事配置 v1 → v2 升级记录

时间：2026-09-23 21:44 上传，21:47:59 重启生效（期间 1 名玩家 ly01_lin，已提前公告）
文件：`/data/config/espetro/fortifications.json`（6147 B，schema_version=2，5 条定义）

## 为什么要升

旧文件没有 `schema_version`，走 `migrateV1` 一次性迁移：
- 日志刷 `读取到旧工事格式；仅进行一次性兼容迁移`；
- 迁移只认死 3 个 id，**任何自定义旧条目都会被拒**（`拒绝以 inline blocks 继续运行`），
  而解析失败是全局的 → `工事 registry 无法冻结` → `failStartup`，服务器开不了图；
- v1 字段（`place_type`/`block_id`/`blocks`）无法表达 v2 的 `durability`、
  `damage_reduction`、`template_by_team`、`behavior` 等能力。

## 做了什么

用 mod 内置的 v2 兜底配置（`src/main/resources/data/espetro/config/fortifications.json`，
即 `bundledDefaultJson()` 的同一份内容）替换服务端文件。数值与原 v1 **逐项核对一致**，
行为不变：

| id | behavior | template / entity | cost.construction | 施工 100/5/5 之类 | 结构值 |
| --- | --- | --- | --- | --- | --- |
| espetro:radio | radio | espetro:fortifications/radio | 0 | 600/30/5 | 600 |
| espetro:hab | hab | hab_attack + hab_defend(按队) | 500 | 200/5/5 | 200 |
| espetro:ammo_crate | ammo_crate | espetro:fortifications/ammo_crate | 100 | 100/5/5 | 100 |
| espetro:vehicle_supply_station | vehicle_supply_station | dragonrise_reforge:ammo_supply_station | 200 | 100/5/5 | 100 |
| espetro:sandbag_wall | generic | espetro:fortifications/sandbag_wall | 100 | 100/5/5 | 100 |

v1 里 `ammo_crate` 的 `block_id: minecraft:shulker_box` 在迁移路径中本来就被丢弃
（运行时已用结构模板），所以这一项也无变化。

## 验证

```
[21:47:59] 工事 JSON v2 已事务冻结: global=5 maps=3 aliases=7
[21:48:01] Done (3.046s)!
```
- 不再出现 `读取到旧工事格式`；
- 三条必需定义 + 2 个 legacy 别名（builtin_radio / builtin_hab）都在；
- 三张图（server_battlefield / 越南 / CREATE_PLUS）各自编译通过。

## 遗留（未改，功能正常）

三张图的 `EsConfig/logistics.json` 还在用旧字段
`hab_construction_cost` / `ammo_crate_construction_cost`，each 启动各刷 2 条警告：
`仅作为一次性迁移输入；请改写到 fortification_overrides.<id>.cost.construction`。
建议后续改为：

```json
"logistics": {
  "fortification_overrides": {
    "espetro:hab": {"cost": {"construction": 500}},
    "espetro:ammo_crate": {"cost": {"construction": 100}}
  }
}
```
（值与原字段相同，纯去警告；改完需再次完整重启。）

## 回滚点

- 服务端原文件：`config/espetro/fortifications.v1.bak.json`（2893 B）
- 本地备份：`build/tmp/srv-fortifications-before-v2.json`
- 升级后镜像：`build/tmp/srv-fortifications-v2.json`、待部署源 `build/tmp/fortifications-v2-deploy.json`

## 后续加新工事（v2 生效后）

1. 往 `config/espetro/fortifications.json` 的 `fortifications` 数组里加一条（`behavior: "generic"`，
   `placement.type` 为 `structure` 或 `entity`），**保留原有 5 条**；
2. 结构型需要模板 NBT：`espetro:fortifications/<名>` →
   `world/datapacks/<包名>/data/espetro/structures/fortifications/<名>.nbt`（+ `pack.mcmeta`, pack_format 15），
   或由我加进 mod 的 `structure_sources/fortifications/*.snbt` 并重编 jar；
3. 完整重启服务器（`/reload` 对工事无效）；
4. 游戏内 Alt 轮盘 → 建造工事 选择摆放。
