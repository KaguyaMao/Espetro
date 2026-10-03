import fs from 'node:fs';
import path from 'node:path';

/**
 * fix-muzzle.mjs — 把 ShootPos.Positions 里"误填成模型骨骼绝对坐标"的分量改成"相对枢轴偏移"
 *
 * 规则（逐轴判定，安全优先）：
 *   对每个 weapon 的每个 Positions[i]，先在模型里找名字像炮口的骨骼（CannonPos* / MachineGunPos* /
 *   MissilePos* / BombPos* / RocketPos* / PassengerMachineGunPos*），取与该值最接近的一根 B；
 *   若某轴满足  |V[a] − B[a]| ≤ tol  且  |V[a] + P[a] − B[a]| > tol   （P = 该 Transform 的枢轴链）
 *   则该轴显然是"绝对坐标"（游戏会再加一次枢轴），改写为 B[a] − P[a]。
 *   已经是相对值的轴、以及 P≈0 的轴一律不动。
 *
 * 用法:
 *   node fix-muzzle.mjs <geoDir> [--tol=0.02] [--apply] <json文件...>
 */
const args = process.argv.slice(2);
const geoDir = args.shift();
let tol = 0.02, apply = false;
const files = [];
for (const a of args) {
  if (a.startsWith('--tol=')) tol = Number(a.slice(6));
  else if (a === '--apply') apply = true;
  else files.push(a);
}

const BONE_RE = /^(CannonPos|MachineGunPos|MissilePos|BombPos|RocketPos|PassengerMachineGunPos)/;
const cache = new Map();

function loadBones(id) {
  if (cache.has(id)) return cache.get(id);
  let geo = null;
  for (const c of [path.join(geoDir, id + '.geo.json'), path.join(geoDir, id + '.json')])
    if (fs.existsSync(c)) { geo = JSON.parse(fs.readFileSync(c, 'utf8')); break; }
  const out = {};
  for (const g of geo?.['minecraft:geometry'] || []) for (const b of (g.bones || []))
    if (b.pivot && BONE_RE.test(b.name)) out[b.name] = [b.pivot[0] / 16, b.pivot[1] / 16, -b.pivot[2] / 16];
  cache.set(id, out);
  return out;
}

function pivotFor(j, transform) {
  const tp = j.TurretPos || [0, 0, 0], bp = j.BarrelPos || [0, 0, 0];
  const pws = j.PassengerWeaponStationPos || [0, 0, 0];
  const pwsb = j.PassengerWeaponStationBarrelPos || [0, 0, 0];
  switch (transform) {
    case 'Barrel': return tp.map((v, i) => v + bp[i]);
    case 'Turret': return tp.slice();
    case 'WeaponStation': return tp.map((v, i) => v + pws[i]);
    case 'WeaponStationBarrel': return tp.map((v, i) => v + pws[i] + pwsb[i]);
    default: return [0, 0, 0]; // Default / VehicleFlat / 未知名(回退 Default) / 无
  }
}

// 在原文里定位某武器 ShootPos.Positions[k][a] 的数字 token 并替换
function replaceNumber(text, weapon, k, a, newValue) {
  const wKey = new RegExp(`"${weapon.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}"\\s*:\\s*\\{`, 'g');
  const wm = wKey.exec(text);
  if (!wm) return null;
  const rest = text.slice(wm.index);
  const spIdx = rest.indexOf('"ShootPos"');
  if (spIdx < 0) return null;
  const afterSp = rest.slice(spIdx);
  const posIdx = afterSp.search(/"Positions"\s*:/);
  if (posIdx < 0) return null;
  const arrStart = afterSp.indexOf('[', posIdx);
  // 解析 Positions 数组的外层结构，找到第 k 个内层数组的起止
  let depth = 0, inner = -1, innerStart = -1;
  for (let i = arrStart; i < afterSp.length; i++) {
    const ch = afterSp[i];
    if (ch === '[') {
      depth++;
      if (depth === 2) { inner++; innerStart = i + 1; }
      if (depth === 1 && inner >= 0 && i > arrStart) { /* 内层数组结束在 ']' 处处理 */ }
    } else if (ch === ']') {
      if (depth === 2 && inner === k) {
        // 在 innerStart..i 之间做第 a 个数字替换
        const seg = afterSp.slice(innerStart, i);
        const nums = [...seg.matchAll(/-?\d+(?:\.\d+)?(?:[eE][-+]?\d+)?/g)];
        if (nums.length <= a) return null;
        const target = nums[a];
        const absStart = wm.index + spIdx + innerStart + target.index;
        const absEnd = absStart + target[0].length;
        return text.slice(0, absStart) + String(newValue) + text.slice(absEnd);
      }
      depth--;
    }
  }
  return null;
}

const round = (v) => Math.round(v * 1e6) / 1e6;
let totalChanges = 0;
const report = [];

for (const f of files) {
  let text = fs.readFileSync(f, 'utf8');
  const j = JSON.parse(text);
  const id = (j.ID || '').split(':').pop() || path.basename(f, '.json');
  const bones = loadBones(id);
  const boneNames = Object.keys(bones);
  if (!boneNames.length) { report.push(`  [跳过] ${path.basename(f)}：模型里没有炮口骨骼，无法核对`); continue; }

  for (const [wn, wd] of Object.entries(j.Weapons || {})) {
    const sp = wd.ShootPos;
    if (!sp?.Positions?.length) continue;
    const T = sp.Transform || '(缺失)';
    const P = pivotFor(j, sp.Transform);
    sp.Positions.forEach((V0, k) => {
      const V = V0.slice();
      // 找最接近的骨骼（按三轴最大偏差）
      let best = null;
      for (const bn of boneNames) {
        const B = bones[bn];
        const dev = Math.max(...V.map((v, i) => Math.abs(v - B[i])));
        if (!best || dev < best.dev) best = { bn, B, dev };
      }
      if (!best) return;
      for (let a = 0; a < 3; a++) {
        const isAbs = Math.abs(V[a] - best.B[a]) <= tol;
        const relWrong = Math.abs(V[a] + P[a] - best.B[a]) > tol;
        if (isAbs && relWrong) {
          const nv = round(best.B[a] - P[a]);
          const nt = replaceNumber(text, wn, k, a, nv);
          if (nt === null) { report.push(`  [失败] ${path.basename(f)} ${wn}[${k}][${a}] 文本定位失败`); return; }
          text = nt;
          report.push(`  ${path.basename(f)} ${wn}[${k}][${a}]  骨骼 ${best.bn} 绝对=${round(best.B[a])}  枢轴=${round(P[a])}`
            + `  ${V[a]} → ${nv}`);
          totalChanges++;
        }
      }
    });
  }
  if (apply) {
    try { JSON.parse(text); } catch (e) {
      console.log(`  [中止] ${path.basename(f)} 改写后 JSON 非法：${e.message}（未写入）`);
      continue;
    }
    fs.writeFileSync(f, text, 'utf8');
  }
}

console.log(report.join('\n'));
console.log(`\n共 ${totalChanges} 处${apply ? '（已写入）' : '（预演，未写入）'}，涉及 ${files.length} 个文件`);
