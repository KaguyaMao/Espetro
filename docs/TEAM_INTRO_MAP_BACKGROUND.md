# 队伍分配提示页背景修复（2026-10-19）

## 需求
User said (m01449): 「修复ui：装载地图完后显示的展示玩家所属队伍的那个页面背景用和其它投票页面一样的模糊地图图片背景」。

## 现状问题
`org.espetro.client.gui.TeamAssignIntroScreen`（队伍分配提示页：白字「您已被分配至」+ 大字「进攻方/防守方」+ 黄字「游戏即将开始，正在进入投票阶段」）
的 `renderBeforeMenu` 只调用 `EspetroAuiWidgets.drawScreenShade(...)`，是纯暗幕；同一轮流程里的地图投票页 / 阵营投票页 / 指挥官投票页都已经是模糊地图图背景，视觉断裂。

## 改动
`src/main/java/org/espetro/client/gui/TeamAssignIntroScreen.java`：

```java
@Override
protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    // 与地图投票页保持一致的「模糊地图图」背景；地图未定/加载失败时渲染器自动回退为暗幕。
    CurrentMapBackgroundRenderer.render(
        graphics, this.width, this.height, resolveBackgroundMapFolder());
}

/** 优先本局已装载的地图，其次走投票页的「本人投票 → 胜出 → 首个候选」回退链。 */
private static String resolveBackgroundMapFolder() {
    String current = ClientGameState.getCurrentMapFolder();
    if (current != null && !current.isBlank()) {
        return current;
    }
    return MapVoteScreen.resolveBackgroundMapFolder();
}
```

`src/main/java/org/espetro/client/gui/MapVoteScreen.java`：把
`private static String resolveBackgroundMapFolder()` 改为包内可见 `static String resolveBackgroundMapFolder()`，
其它阶段页复用同一条回退链（本人投票 → 胜出地图 → 当前已装载地图 → 首个候选）。

## 数据来源时序
1. 服务端 `GameStateManager.broadcastTeamAssignShow(server)`（NetworkManager:1249）**只发 `TeamAssignPacket(team, seconds)`，不带地图**。
2. 客户端 `ClientGameState.currentMapFolder` 由 `GamePhaseSyncPacket` 写入（`ClientPacketHandlers` 里 `setCurrentMapFolder(...)`）；
   TEAM_SELECT 阶段服务端 `GameStateManager.getCurrentMapFolder()` 仍返回 `pendingMap`（`pendingMap` 到回合结束才置空），所以此时客户端已有正确地图文件夹。
3. 兜底：若 `currentMapFolder` 为空（例如阶段包丢失），走 `MapVoteScreen` 的静态回退链；再不行由 `CurrentMapBackgroundRenderer` 自动回退为暗幕（不会出现黑屏或崩溃）。

## 构建与部署
- 构建：`.\gradlew.bat build -x test "-PauratipJar=D:\minecraft\Java\Projects\AuraTip\build\libs\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar"` → `build/libs/espetro-1.1.3-l.jar`（16,863,007 B）。
- 服务端：`mods/espetro-1.1.3-x.jar` 旧件改名 `espetro-1.1.3-x.jar.bak-uifb`，新件上传（服务端回列大小 16,863,007 一致）。
- 客户端：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-l.jar`，旧件备份 `espetro-1.1.3-l.jar.bak-uifb`。
- 纯客户端绘制改动，**不新增任何网络包**（包序号表未变，不存在上次登录事故那类错位风险）。
- 服务端副本需重启才生效；客户端下次启动即生效。

## 回滚
- 服务端：`espetro-1.1.3-x.jar.bak-uifb` 改回原名。
- 客户端：`espetro-1.1.3-l.jar.bak-uifb` 改回原名。
