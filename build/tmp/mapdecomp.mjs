import { readFileSync, writeFileSync, readdirSync } from 'fs';
// 1. build srg->mojmap map
const lines = readFileSync('D:/minecraft/modp/Espetro/build/tmp/reverse.tsrg', 'utf8').split('\n');
const map = new Map();
for (const line of lines) {
  if (!line.trim() || line.startsWith('tsrg2')) continue;
  if (line.startsWith('\t')) {
    const parts = line.trim().split(/\s+/);
    if (parts.length === 3) map.set(parts[0], parts[2]);
    else if (parts.length === 2) map.set(parts[0], parts[1]);
  }
}
console.log('map size=' + map.size);
// 2. replace in decompiled files
const dir = 'D:/minecraft/modp/Espetro/build/tmp/decomp/org/espetro/client/gui';
const targets = ['VehicleWheelController.java', 'AuraTipRadialController.java', 'RadioRadialController.java', 'ResupplyRadialController.java'];
for (const t of targets) {
  const p = dir + '/' + t;
  let code = readFileSync(p, 'utf8');
  // 从长到短排序（避免前缀冲突：f_91067_ vs f_9106_）
  const keys = [...map.keys()].sort((a, b) => b.length - a.length);
  for (const k of keys) {
    code = code.split(k).join(map.get(k));
  }
  writeFileSync(dir + '/' + t.replace('.java', '.mapped.java'), code, 'utf8');
  console.log('mapped ' + t);
}
