// spotcheck.mjs — 抽查若干职业变体转换前后的补给条目
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const A = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const B = 'D:/minecraft/modp/Espetro/build/tmp/fxall-new/';
const CASES = [
  ['pla_112th_brigade_mesh.json', 'PLA_112th_mesh_RAIDER', '机瞄'],
  ['pla_112th_brigade_mesh.json', 'PLA_112th_mesh_MG', '光瞄'],
  ['pla_112th_brigade_mesh.json', 'PLA_112th_mesh_ASSAULT', '机瞄'],
  ['pla_112th_brigade_mesh.json', 'PLA_112th_mesh_BIG_SQUADMG', 'default'],
  ['us_redone.json', 'US_REDONE_SQUADMG', 'default'],
  ['ru_3th.json', 'RU_3th_MARKSMAN', 'default'],
  ['ru_3th.json', 'RU_3th_COMMANDER', 'default'],
  ['us_redone.json', 'US_REDONE_RIFLEMAN', 'default'],
];
for (const [file, cid, vid] of CASES) {
  const a = JSON.parse(fs.readFileSync(A + file, 'utf8'));
  const b = JSON.parse(fs.readFileSync(B + file, 'utf8'));
  const va = a.classes?.[cid]?.variants?.[vid];
  const vb = b.classes?.[cid]?.variants?.[vid];
  if (!va || !vb) { console.log(`\n### ${file} ${cid}/${vid}: 未找到`); continue; }
  console.log(`\n### ${file}  ${cid} / ${vid}`);
  console.log('  枪械弹药: ' + (JSON.stringify(va.commands).match(/AmmoId:\"([^\"]+)\"/g) || []).slice(0, 5).join(','));
  console.log('  旧 items: ' + (va.resupply?.items || []).map((i) => `${i.id} count=${i.count} max=${i.max}`).join(' | '));
  console.log('  新 items: ' + (vb.resupply?.items || []).map((i) => `${i.id} count=${i.count} max=${i.max}`).join(' | '));
}
