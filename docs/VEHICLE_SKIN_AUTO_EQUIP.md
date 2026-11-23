# 载具自动装备涂装（Skin）机制说明

本文记录 Espetro 里「载具出生时自动套用涂装」的完整链路：配置在哪、按什么选、什么时候写、客户端怎么渲染、出问题怎么查。

> 结论先说：**Espetro 只在载具生成的那一刻写一次皮肤 id**，按「载具注册表里的 `skin` 映射 + 当前地图的 `environment` 标签」决定；之后不再改动。贴图本身由卓越前线（superbwarfare）/ FCP 的模型与皮肤表决定，Espetro 不参与渲染。

---

## 1. 配置：载具注册表 `config/EsVehicle/vehicles.json`

路径固定为游戏根目录 `config/EsVehicle/vehicles.json`（服务端实例根目录下同名文件），启动时读取一次。

```json
{
  "vehicles": {
    "ztq15": {
      "entity": "dragonrise_reforge:ztq15",
      "display_name": "ZTQ-15",
      "per_max_count": 1,
      "respawn_minutes": 10,
      "skin": { "normal": "green", "desert": "sand" }
    }
  }
}
```

`skin` 是一个 **「环境标签 → 皮肤 id」的映射**，对应字段：

| 位置 | 内容 |
|---|---|
| `src/main/java/org/espetro/vehicle/EsVehicleRegistry.java:76` | `public Map<String, String> skin = new LinkedHashMap<>();` |
| 同上 `:88-95` | `skinFor(environment)`：先取 `skin.get(env)`，取不到回落 `skin.get("normal")`，再取不到返回 `null`（= 用模型默认外观） |

⚠️ 因为字段名都是下划线风格，注册表里的每个字段都必须带 `@SerializedName`（`:52-74`），否则 Gson 会静默忽略。

---

## 2. 环境标签从哪来：地图 `game.json` 的 `environment`

| 环节 | 位置 |
|---|---|
| 地图配置读取 | `src/main/java/org/espetro/mapconfig/ActiveMapConfig.java:72-74` → `game.json` 的 `environment`，缺省 `"normal"` |
| 取值归一化 | `src/main/java/org/espetro/mapconfig/GameSettingsSnapshot.java:142-148` → 只接受 `normal / desert / snow / jungle`，其它一律回落 `normal` |
| 编制装配时读取 | `src/main/java/org/espetro/vehicle/VehicleConfig.java:265` `String environment = map.environment();` |

**线上现状**：4 张地图的 `EsWorld/<地图名>/EsConfig/game.json` 里都**没有** `environment` 键，所以全部按 `normal` 处理（这一点很关键，见第 6 节的“只写 desert 的车”）。

---

## 3. 装配时机：编制加载 → 槽位

```
EsWorld/<map>/EsConfig/game.json  (environment)
        │
        ▼
VehicleConfig.applyActiveMap()                     VehicleConfig.java:265
        │  for 每个载具槽位
        ▼
slot.skinId = def.skinFor(environment)             VehicleConfig.java:314
        │
        ▼
VehicleSlotConfig.skinId                            VehicleConfig.java:146
```

`skinFor` 的返回值就是「这台车在这个地图上该用哪个皮肤」，空值表示不指定。

---

## 4. 出生时写入：一次，且只写一次

```java
// VehicleManager.java:2004（createVehicleEntity 内，紧接 spawn items 之后）
applyVehicleSkin(entity, slot);

// VehicleManager.java:2012-2026
private void applyVehicleSkin(Entity entity, @Nullable VehicleConfig.VehicleSlotConfig slot) {
    if (entity == null || slot == null || slot.skinId == null || slot.skinId.isBlank()) return;
    try {
        entity.getClass().getMethod("setSkinId", String.class).invoke(entity, slot.skinId);
        Espetro.LOGGER.info("[载具皮肤] {} ({}) -> {}", entity.getType(), entity.getUUID(), slot.skinId);
    } catch (Throwable e) {
        Espetro.LOGGER.warn("载具皮肤设置失败 type={} skin={}: {}", entity.getType(), slot.skinId, e.toString());
    }
}
```

要点：

- **反射调用** `VehicleEntity#setSkinId(String)`，这样 Espetro 不依赖载具模组的编译期接口；模组缺失/方法改名时只打 WARN，不炸。
- 写入发生在 `addFreshEntity` **之前**，服务端数据与实体一起入库。
- 全工程只有这一处 `setSkinId` 调用（`grep -rn setSkinId src/` 可复核），**部署同步、复活、补给都不会重写皮肤**。

---

## 5. 同步与渲染：Espetro 只负责“写 id”

- `VehicleEntity.SKIN_ID` 是 SBW 的**同步数据**（`EntityDataAccessor<String>`，`javap` 实证：`public static final net.minecraft.network.syncher.EntityDataAccessor<java.lang.String> SKIN_ID`）。
- 因此 `setSkinId` 只是改了服务端这份同步值；客户端在收到同步包后才会换贴图 —— **生成瞬间可能先按模型默认外观渲染一小会儿，属正常**（代码注释也写了这一点，`VehicleManager.java:2018-2019`）。
- 真正的贴图解析在载具模组侧，典型链路（FCP 系，`javap` 反编译）：
  ```
  XxxRenderer.getTextureLocation(entity)
    → XxxEntity.getCurrentTexture()
    → VehicleSkins.currentTexture(vehicle)
        skin = VehicleSkin.getSkin(vehicle);   // 按同步的 SKIN_ID 查表
        if (skin == null) skin = firstSkin();  // 回落：皮肤表第 0 条
        return skin.getTexture();
  ```
- `vanilla` 是 SBW/FCP 的**保留 id**（按“模型默认”处理），不要当作自定义皮肤名使用；需要“看起来是默认色”就留空不配，需要显式指定就用皮肤表里真实存在的 id。

---

## 6. 三种写法的实际效果（最容易踩的坑）

| 写法 | 普通图（normal） | 沙漠图（desert） |
|---|---|---|
| `{"normal":"green","desert":"sand"}` | green | sand |
| `{"desert":"sand"}` | **null → 模型默认外观** | sand |
| `{"normal":"green"}` | green | **回落 normal → green** |

第三种是 `skinFor` 的“缺省回落 normal”行为；第二种表示“只有沙漠图才指定皮肤”。线上目前多数是第二种。

### 线上 `skin` 配置一览（取自服务端 `config/EsVehicle/vehicles.json`，53 条中 17 条带 skin）

| 载具 | skin |
|---|---|
| `ztq15` / `ztq15_mgs` / `ztq15_mbt` | `normal=green, desert=sand` |
| `m3a3` / `aav7a1` / `aavc7c1` | `normal=green, desert=sand` |
| `ztz96a` / `zbd04a` / `ztz99a` / `t72b3` / `bmp3` / `m1a2sepv1` / `m1a2sepv1_mbt` / `ural4320_supply` | `desert=sand` |
| `mv3_supply` | `desert=yellow` |
| `m1a2sepv2` | `normal=green` |
| `m939` | `normal=sand, desert=sand` |

---

## 7. 启动期自检：皮肤 id 写错会在日志里报警

```java
// EsVehicleRegistry.java:213-233 warnMissingSkins()
// EsVehicleRegistry.java:240-266 querySkinIds() —— 反射读 SBW 已注册皮肤
Class<?> skinClass = Class.forName("com.atsuishio.superbwarfare.data.vehicle_skin.VehicleSkin");
Object data = skinClass.getMethod("getSkins", EntityType.class).invoke(null, type);
```

日志形态：

```
[载具注册表] m939 的 desert 皮肤 'sand' 不存在（可用: [vanilla, green, sand, black])
```

查不到 SBW 类（未装载具模组）时跳过校验，运行期再容错。

---

## 8. 运维：加一台带涂装的车 / 加一个皮肤

### 8.1 皮肤数据在哪（载具模组侧，不是 Espetro）

```json
// data/fcp/sbw/vehicle_skins/m939.json（FCP 2.0.0 jar 内原文，430 B）
{
  "Skins": [
    { "Id": "vanilla", "Name": "Green", "Texture": "fcp:textures/entity/m939/m939_green.png", "Priority": 1 },
    { "Id": "sand",    "Name": "Sand",  "Texture": "fcp:textures/entity/m939/m939_sand.png",  "Priority": 2 },
    { "Id": "black",   "Name": "Black", "Texture": "fcp:textures/entity/m939/m939_black.png", "Priority": 3 }
  ]
}
```

- 文件名 = 载具实体短名（`fcp:m939` → `m939.json`），目录 `data/<ns>/sbw/vehicle_skins/`。
- 数组**第 0 条就是渲染回落用的那张**（`VehicleSkin.getSkin()` 为 null 时取 `firstSkin()`），所以第 0 条通常叫 `vanilla`。
- `Priority` 只影响选择界面的排序，不影响出生时的自动选择。

### 8.2 操作步骤

1. 确认目标皮肤 id 存在（看上面那个 json，或看启动日志里的 `可用: [...]`）。
2. **写注册表**：在 `config/EsVehicle/vehicles.json` 对应载具下加 `skin`，键用环境标签（`normal` / `desert` / `snow` / `jungle`）。
3. **让地图进沙漠**：在 `EsWorld/<地图>/EsConfig/game.json` 里加 `"environment": "desert"`。
4. **重启服务端**（注册表与地图配置都是启动期读取，不热重载），看日志确认没有 `皮肤 'xxx' 不存在` 的 WARN。
5. 进游戏部署一台，服务端应出现：`[载具皮肤] entity.xxx (uuid) -> sand`。

---

## 9. 已知现象与对应处理

| 现象 | 原因 | 处理 |
|---|---|---|
| 部署瞬间先显示默认色，再变成目标涂装 | `SKIN_ID` 是同步数据，客户端晚一帧/几帧才换图 | 正常；无法从服务端消除 |
| 某台车“永远是绿色” | 该车配置只写了 `desert`，普通图回落 null → 模型默认（`m939` 模型默认就是 `m939_green.png`） | 给它补 `normal`，或把 `normal` 指向沙色 |
| 涂装“复原成绿色” | 不是 Espetro 改回去：全工程只有出生时写一次；若确有回退，多半是客户端某一帧走了 `VehicleSkin.getSkin()==null → firstSkin()` 的回落 | 用资源包把该车皮肤表第 0 条指向目标贴图（例如 `Espetro FCP Skins.zip` 把 `m939` 的 `vanilla` 指向沙色），即可盖掉回落帧 |
| 外观“抽搐一下” | FCP 的 `M939Entity` 首次 tick 会 `setTent(random.nextBoolean())`（随机帐篷，`tentInit` 只做一次） | 与涂装无关，属载具模组行为 |

---

## 10. 代码索引

| 作用 | 文件:行 |
|---|---|
| 皮肤映射字段 | `src/main/java/org/espetro/vehicle/EsVehicleRegistry.java:76` |
| 环境→皮肤选择（含回落） | `src/main/java/org/espetro/vehicle/EsVehicleRegistry.java:88-95` |
| 皮肤 id 可用性自检 | `src/main/java/org/espetro/vehicle/EsVehicleRegistry.java:213-266` |
| 地图环境标签 | `src/main/java/org/espetro/mapconfig/ActiveMapConfig.java:72-74` |
| 环境值归一化 | `src/main/java/org/espetro/mapconfig/GameSettingsSnapshot.java:142-148` |
| 编制装配时取值 | `src/main/java/org/espetro/vehicle/VehicleConfig.java:265`、`:314` |
| 槽位皮肤容器 | `src/main/java/org/espetro/vehicle/VehicleConfig.java:146` |
| 出生时应用皮肤 | `src/main/java/org/espetro/vehicle/VehicleManager.java:2004`、`:2012-2026` |

---

## 11. 一句话排查口诀

> 没有 `[载具皮肤]` 日志 → 该环境没有配 skin（或被回落逻辑吃掉了）；
> 有日志但颜色不对 → 皮肤 id 在载具模组里指向的贴图不是你以为的那张；
> 只有一瞬不对 → 同步时序 / `firstSkin()` 回落，用资源包兜底。
