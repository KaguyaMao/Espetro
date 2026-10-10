// patch-csv-config.mjs — nativeVehicles 改为逗号分隔字符串（Forge 列表类型会被静默重置）+ 写入默认白名单
import fs from 'node:fs';

const CFG = 'D:/minecraft/modp/Espetro/src/main/java/org/espetro/vehicle/VehicleInteractionConfig.java';
const WL = 'D:/minecraft/modp/Espetro/src/main/java/org/espetro/vehicle/VehicleNativeWhitelist.java';

// 默认白名单：卓越前线 小型无人机/陶氏导弹(集装箱) + 龙之崛起 便携武器
const DEFAULT_LIST = [
  'superbwarfare:drone',
  'superbwarfare:tow',
  'superbwarfare:container',
  'dragonrise_reforge:hj8',
  'dragonrise_reforge:9m133',
  'dragonrise_reforge:m2',
  'dragonrise_reforge:qjz89',
  'dragonrise_reforge:dshk'
].join(',');

function patch(file, pairs) {
  let t = fs.readFileSync(file, 'utf8');
  for (const [from, to] of pairs) {
    if (!t.includes(from)) { console.error('!! 未匹配 ' + file + '\n--- 期望片段 ---\n' + from); process.exit(1); }
    t = t.replace(from, to);
  }
  fs.writeFileSync(file, t, 'utf8');
  console.log('patched ' + file);
}

patch(CFG, [
  [
    'public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NATIVE_VEHICLES;',
    'public static final ForgeConfigSpec.ConfigValue<String> NATIVE_VEHICLES;'
  ],
  [
    '.defineList("nativeVehicles", new java.util.ArrayList<String>(), o -> o instanceof String);',
    '.define("nativeVehicles", "' + DEFAULT_LIST + '");'
  ]
]);

patch(WL, [
  [
    `            List<? extends String> raw = VehicleInteractionConfig.NATIVE_VEHICLES.get();
            if (raw != null) {
                for (Object element : raw) {
                    if (!(element instanceof String entry)) {
                        continue;
                    }
                    String id = entry.trim().toLowerCase(Locale.ROOT);`,
    `            // 逗号分隔字符串（Forge 的列表类型在读取时会被静默重置为默认，故不用列表）
            String raw = VehicleInteractionConfig.NATIVE_VEHICLES.get();
            if (raw != null) {
                for (String part : raw.split(",")) {
                    String entry = part;
                    String id = entry.trim().toLowerCase(Locale.ROOT);`
  ]
]);

console.log('默认白名单 = ' + DEFAULT_LIST);
