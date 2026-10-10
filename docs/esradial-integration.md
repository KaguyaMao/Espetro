# Espetro 接入 EsRadial

只处理 `1.20.1` 分支。最新接入补丁基于 `4ae5012`；已通过 Java 编译与适配测试的完整源码基于同分支的历史提交 `32d67bbff60a59c3842c786843b34e18f29eeb4d`，版本同为 `1.1.3-j`。没有修改 main 分支。

## 接入后的操作

| 场景 | 打开 | 确认 | 关闭 |
| --- | --- | --- | --- |
| 建造 / 指挥技能 | 默认按住左 Alt，可在控制设置修改 | 左键选中，子菜单原位切换 | 松开 Alt；或右键返回、根菜单右键关闭 |
| 载具 | 按住 SBW 交互键，未安装时默认 F | 左键选择；按住左键装卸物资 | 松开交互键 |
| 弹药箱 / 出生点补给箱 | 保留 Espetro 原有方块交互入口 | 左键补给、换职及选择装备 | 右键返回、根菜单右键关闭；Esc |

外围选项按鼠标方向选择，中心留给 Espetro 原有上车读条；中心上车现在还需按住左键，避免打开轮盘便自动上车。装卸的间隔取自服务端同步值，只有 EsRadial 一个重复计时器，容量 HUD 读取进度，不另外发送装卸包。

补给目录仍按原有 5 项一页，保留返回/翻页、余额、数量、费用和服务器会话 token。职业配额、技能冷却、工事目录和现有服务器权限校验继续使用原有协议。

## 修改了哪里

- 新库集中管理 AUI 圆环、图标、说明、命中检测、按键会话、导航和鼠标恢复。
- `AuraTipRadialController` 改名为 `TacticalRadialController`；四个轮盘控制器改调 `org.esradial.client`。
- 库和这四个控制器不再依赖 AuraTip 的轮盘 API。Espetro 其他页面及通知仍有 AuraTip/OELib 依赖，需要继续安装。
- 选择职业、补给子页和载具根菜单共享一次打开会话，松开 F 会一起关闭。
- 晚到的车辆换职/补给答复不应在 F 已松开后重新打开轮盘。补给增量答复不抢占其他菜单。
- `mods.toml` 新增客户端前置 EsRadial 0.3.0。服务器启动引导仍不直接加载客户端轮盘类。

## 开发构建

独立库源码：[RositaOVO/EsRadial](https://github.com/RositaOVO/EsRadial)，分支 `1.20.1`。先将它克隆到 Espetro 旁边，用 Java 17 构建和发布到 Maven local，再构建 Espetro：

```text
cd EsRadial
./gradlew :core:test --configure-on-demand
./gradlew :forge:publishToMavenLocal
cd ../Espetro
./gradlew build -PauratipJar=/absolute/path/to/auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar
```

Espetro 原工程要求作者提供固定 AuraTip 构建产物，还依赖 Tetrachord、OELib、Rhino、Architectury 和 KubeJS。接入轮盘不解除这些既有前置。发布库的 Forge jar 通过 Maven local 再由 Espetro 的 `fg.deobf` 转为开发环境命名，避免把生产 jar 直接作为 userdev 类使用。

## 已验证及未验证

独立库 Java 编译通过，39 项核心及布局测试通过。历史基线 32d67bb 的完整源码接入后也已用真实 Forge 映射 jar、AUI 1.2.6 及既有依赖通过 Java 编译；按仓库自己的 accesstransformer.cfg 开放了 5 个编译期字段。15 项 Espetro 轮盘适配与权限分类测试通过。

最新4ae5012仍缺FixedWeaponWheelPacket源码及DragonRise的SupplyStationConfig、SupplyStationDataLoader依赖，未通过完整编译。附带对应补丁，便于以后补齐该分支后使用。没有删除它们的引用或重写相关游戏功能来掩盖问题。

本机 ForgeGradle 仍有依赖解析问题。历史基线测试包已完成 Mixin 注解处理和 Forge 名称映射，基础联机轮盘操作已测试。独立客户端的实际游戏截图已确认建造、载具图标及悬停和空白区域显示；完整目录及业务场景仍需进一步验收。历史基线测试包不能等同于最新分支的正式发行产物。

载具页面已采用固定的不等大小扇区，并保留两块空白。缺少某类功能时保留原位置，其他图标不会重新均分；空白不占动作索引，也不会发送请求。仅悬停时显示所选外弧，EsRadial 统一绘制内侧轨道、中心上车进度和外侧装卸进度；已移除原 HUD 重复绘制的装卸圆环。真实进度由既有交互状态及轮盘会话提供，不改变服务端装卸间隔。

指挥、建造、技能、职业和步兵补给页面均启用 `.squadLayout()`，由当前动态目录生成不等大小的扇区及两块空白，继续使用服务端提供的按钮数据。图标容器缓存使用 AUI `appendChild` 返回的实际连接对象，避免在已被替换的普通 Element 上添加图标或设置位置。

补齐基线后，实际客户端验收应检查：不同 GUI 缩放；快速点击；进入子页仍按住原打开键；中心上车；装卸间隔与请求次数；职业不可用提示；补给翻页和余额更新；关闭后能恢复转视角/射击；失焦、死亡、断线和资源重载后退出或恢复正常。

## ES 系列后续

此次同时提供 EsPoints 分类标点接入；EsVoice、EsWeather 原有功能照旧。以后某个模组要使用这套轮盘，再给它加按钮数据和事件适配，不需要为了装 EsRadial 把整个系列一起重写。

## 玩家调整轮盘

EsRadial 0.3.0 自动接管各页布局。轮盘打开后按 F6，拖动扇区（包括空槽）调整顺序，
拖动分界线调整大小，滚轮旋转。Enter 保存、Esc 取消、R 恢复默认（Enter 保存后生效）。
编辑界面不会触发建造、补给、换职或上车。客户端 `config/esradial/layouts.json` 可手工修改，
下次打开生效；每个菜单及不同选项集合分别保存，通过稳定按钮 ID 匹配。
无需修改地图服务端 JSON。格式见 [EsRadial 布局配置](https://github.com/RositaOVO/EsRadial/blob/1.20.1/docs/LAYOUTS.md)。

## 0.3.0 分类接入

指挥根入口与已安装 EsPoints 的敌情和战术指令合并，工事按基础、防御、固定武器、后勤及其他目录分类。只显示服务端目录的真实工事；集结点保留原权限。
Tab 切换30°/45°倍数吸附，空槽与按钮都可拖动。新增 VehicleManager.tacticalVehicleSnapshots 供 EsPoints 获取己方载具位置；无新建造协议。
