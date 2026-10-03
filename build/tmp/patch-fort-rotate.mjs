// patch-fort-rotate.mjs — 工事预览按 R 顺时针旋转 90°
import fs from 'node:fs';

const edits = [];
function patch(file, pairs) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label] of pairs) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次`); process.exitCode = 1; return; }
    text = text.replace(f, to.replace(/\r?\n/g, eol));
    console.log(`  ✓ [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
}

const ESP = 'src/main/java/org/espetro/Espetro.java';
const ESC = 'src/main/java/org/espetro/EspetroClient.java';
const FPC = 'src/main/java/org/espetro/client/FortificationPlacementController.java';
const FM = 'src/main/java/org/espetro/bastion/FortificationManager.java';
const LANG_ZH = 'src/main/resources/assets/espetro/lang/zh_cn.json';
const LANG_EN = 'src/main/resources/assets/espetro/lang/en_us.json';

console.log('== 1) Espetro：新增按键字段 ==');
patch(ESP, [[
  '    public static Object KEY_RADIAL; // 长按战术交互轮盘',
  '    public static Object KEY_RADIAL; // 长按战术交互轮盘\n    public static Object KEY_FORT_ROTATE; // R - 工事预览顺时针旋转 90°',
  'KEY_FORT_ROTATE 字段',
]]);

console.log('== 2) EspetroClient：注册 R 键 ==');
patch(ESC, [
  [`        net.minecraft.client.KeyMapping keyRadial = new net.minecraft.client.KeyMapping(
            "key.espetro.radial", org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT, "key.categories.espetro");
        event.register(keyTeam);
        event.register(keyClass);
        event.register(keyRadial);
        Espetro.KEY_TEAM = keyTeam;
        Espetro.KEY_CLASS = keyClass;
        Espetro.KEY_RADIAL = keyRadial;`,
   `        net.minecraft.client.KeyMapping keyRadial = new net.minecraft.client.KeyMapping(
            "key.espetro.radial", org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT, "key.categories.espetro");
        // 工事预览时按 R 顺时针旋转 90°（非预览状态无作用）
        net.minecraft.client.KeyMapping keyFortRotate = new net.minecraft.client.KeyMapping(
            "key.espetro.fort_rotate", org.lwjgl.glfw.GLFW.GLFW_KEY_R, "key.categories.espetro");
        event.register(keyTeam);
        event.register(keyClass);
        event.register(keyRadial);
        event.register(keyFortRotate);
        Espetro.KEY_TEAM = keyTeam;
        Espetro.KEY_CLASS = keyClass;
        Espetro.KEY_RADIAL = keyRadial;
        Espetro.KEY_FORT_ROTATE = keyFortRotate;`,
   '注册 R 键'],
]);

console.log('== 3) FortificationPlacementController：旋转状态 + 按键轮询 + 预览应用 ==');
patch(FPC, [
  [`    private static Direction facing = Direction.NORTH;`,
   `    private static Direction facing = Direction.NORTH;
    /** 工事朝向相对玩家朝向的顺时针旋转步数（每步 90°，按 R 增加，仅本次预览有效）。 */
    private static int rotationSteps;`,
   'rotationSteps 字段'],
  [`        preview = new Preview(packet.token(), packet.displayName(), packet.occupiedOffsets());
        anchor = null;
        boxes = List.of();
        valid = false;
    }`,
   `        preview = new Preview(packet.token(), packet.displayName(), packet.occupiedOffsets());
        anchor = null;
        boxes = List.of();
        valid = false;
        rotationSteps = 0;
    }`,
   'begin 重置旋转'],
  [`    public static void clear() {
        preview = null;
        anchor = null;
        boxes = List.of();
        valid = false;
    }`,
   `    public static void clear() {
        preview = null;
        anchor = null;
        boxes = List.of();
        valid = false;
        rotationSteps = 0;
    }`,
   'clear 重置旋转'],
  [`        if (preview != null) {
            updateOutline(minecraft);
            return;
        }`,
   `        if (preview != null) {
            pollRotationKey(minecraft);
            updateOutline(minecraft);
            return;
        }`,
   'tick 轮询 R 键'],
  [`        facing = mc.player.getDirection();`,
   `        facing = rotatedFacing(mc);`,
   '预览应用旋转'],
  [`    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {`,
   `    /** 按一次 R：工事朝向相对玩家再顺时针转 90°（0/90/180/270 循环）。 */
    private static void pollRotationKey(Minecraft mc) {
        if (!(org.espetro.Espetro.KEY_FORT_ROTATE instanceof net.minecraft.client.KeyMapping key)) {
            return;
        }
        boolean rotated = false;
        while (key.consumeClick()) {
            rotationSteps = (rotationSteps + 1) & 3;
            rotated = true;
        }
        if (rotated && mc.player != null) {
            mc.player.displayClientMessage(Component.literal(
                "§e工事朝向已顺时针旋转 " + (rotationSteps * 90) + "°（按 R 继续旋转）"), true);
        }
    }

    /** 实际工事朝向 = 玩家朝向按 rotationSteps 顺时针旋转后的方向。 */
    private static Direction rotatedFacing(Minecraft mc) {
        Direction base = mc.player == null ? Direction.NORTH : mc.player.getDirection();
        int steps = rotationSteps & 3;
        for (int i = 0; i < steps; i++) {
            base = base.getClockWise();
        }
        return base;
    }

    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {`,
   '新增旋转辅助方法'],
]);

console.log('== 4) 服务端：允许非视线方向的朝向（旋转后确认）==');
patch(FM, [[
  `        if (facing == null || !facing.getAxis().isHorizontal()) return "§c无效的工事方向。";
        if (facing != player.getDirection()) return "§c工事方向已变化，请重新对准后确认。";`,
  `        if (facing == null || !facing.getAxis().isHorizontal()) return "§c无效的工事方向。";
        // 朝向可以是玩家用 R 键旋转过的 4 个水平方向之一，因此不再要求等于视线方向。`,
  '放开朝向校验',
]]);

console.log('== 5) 语言文件 ==');
patch(LANG_ZH, [[
  '  "key.espetro.radial": "战术交互轮盘（长按）",',
  '  "key.espetro.radial": "战术交互轮盘（长按）",\n  "key.espetro.fort_rotate": "工事旋转（顺时针 90°）",',
  'zh_cn',
]]);
patch(LANG_EN, [[
  '  "key.espetro.radial": "Tactical Interaction Radial (Hold)",',
  '  "key.espetro.radial": "Tactical Interaction Radial (Hold)",\n  "key.espetro.fort_rotate": "Rotate Fortification (90° CW)",',
  'en_us',
]]);

console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
