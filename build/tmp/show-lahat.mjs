// show-lahat.mjs — 对比 m1128 的 LAHAT override 音效块（原始/当前服务器/本地）
import fs from 'fs';

const FILES = {
  '原始(去弹夹前)': 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh-backup/m1128.json',
  '当前服务器状态': 'D:/minecraft/modp/Espetro/build/tmp/sbw-strip-out/dr/m1128.json',
  '本地GScode': 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/m1128.json'
};

for (const [tag, p] of Object.entries(FILES)) {
  const j = JSON.parse(fs.readFileSync(p, 'utf8'));
  const ov = j.Weapons?.Cannon?.AmmoType?.[2]?.Override;
  console.log(`\n===== ${tag}`);
  if (!ov) { console.log('  无 AmmoType[2].Override'); continue; }
  console.log('  SoundInfo: ' + JSON.stringify(ov.SoundInfo, null, 1).replace(/\n/g, '\n  '));
  console.log('  SoundRadius: ' + JSON.stringify(ov.SoundRadius));
  console.log('  Override 键: ' + Object.keys(ov).join(', '));
  console.log('  Magazine/EmptyReloadTime: ' + JSON.stringify([ov.Magazine, ov.EmptyReloadTime]));
}
