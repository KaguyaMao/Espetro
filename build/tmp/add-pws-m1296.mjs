import fs from 'node:fs';
import path from 'node:path';

// 给 m1296 补上缺失的武器站枢轴字段（值取自模组包内数据，避免 WeaponStationBarrel 变换拿到 null 而 NPE）
const files = process.argv.slice(2);
const PWS = [-0.3187, 0.90171875, 0.48108125];
const PWSB = [5e-5, 0.22046875, -0.00016875];

for (const f of files) {
  let text = fs.readFileSync(f, 'utf8');
  const j = JSON.parse(text);
  if (j.PassengerWeaponStationPos) { console.log(`[跳过] ${f}：已有 PassengerWeaponStationPos`); continue; }
  const m = text.match(/^(\s*)"BarrelPos":\s*\[[^\]]*\],?\s*$/m);
  if (!m) { console.log(`[失败] ${f}：未找到 BarrelPos 行`); continue; }
  const indent = m[1];
  const ins = `${m[0].replace(/,?\s*$/, ',')}\n`
    + `${indent}"PassengerWeaponStationPos": [${PWS.join(', ')}],\n`
    + `${indent}"PassengerWeaponStationBarrelPos": [${PWSB.join(', ')}],`;
  text = text.replace(m[0], ins);
  try { JSON.parse(text); } catch (e) { console.log(`[失败] ${f}：改写后非法 JSON ${e.message}`); continue; }
  fs.writeFileSync(f, text, 'utf8');
  const after = JSON.parse(text);
  console.log(`[已写入] ${f}\n   PassengerWeaponStationPos=${JSON.stringify(after.PassengerWeaponStationPos)}`);
}
