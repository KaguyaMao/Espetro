// transform-resupply-to-magazines.mjs — 把补给里的散装弹药条目改为"满弹匣"条目
//
// 规则（已与用户确认）：
//  1) 散装弹药条目 → taczmagazines:magazine{AmmoCount:cap,AmmoId:ammo,MagazineFamily:family,MaxCapacity:cap}
//     count=1（每次 1 个满弹匣），max=max(1, ceil(原max/cap))，ammo_cost 原样保留（未设则不加）
//  2) 发射器/火箭筒类弹药（RPG-7 三种、120mm、40mm 榴弹、84mm）保持散装不变
//  3) 修正错配：补给弹药不在本变体枪械弹药集合内且不是发射器类 → 按"本变体主武器弹药"改写
//  4) 补齐缺口：本变体有枪械但该弹药没有任何补给条目 → 新增弹匣条目（count=1, max=6）
//
// 用法: node transform-resupply-to-magazines.mjs [--write]
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const SRC = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const DST = 'D:/minecraft/modp/Espetro/build/tmp/fxall-new/';
const WRITE = process.argv.includes('--write');
const SIDEARM_DEFAULT_MAX = 6;

const LAUNCHER_AMMO = new Set([
  'murasamet:og7he', 'murasamet:pg7heat', 'murasamet:pg7vr_tandem_heat',
  'suffuse:120mm', 'tacz:40mm', 'ts:40mm_vog25', 'ts:84mm_ffv751'
]);

const MAG = /taczmagazines:magazine\s*\{([^}]*)\}/g;
const FAM = /MagazineFamily\s*:\s*"([^"]+)"/;
const AMMO_IN = /AmmoId\s*:\s*"([^"]+)"/;
const CAP = /MaxCapacity\s*:\s*(\d+)/;
const STORED = /TaCZMag_StoredMagazine:\{Count:[^}]*AmmoId:"([^"]+)"/g;

function magMapOf(commands) {
  const map = new Map(); // ammo -> Map("family|cap" -> 次数)
  const text = commands.join('\n');
  let m; MAG.lastIndex = 0;
  while ((m = MAG.exec(text)) !== null) {
    const body = m[1];
    const am = (body.match(AMMO_IN) || [])[1];
    const fam = (body.match(FAM) || [])[1];
    const cap = Number((body.match(CAP) || [])[1]);
    if (!am || !fam || !cap) continue;
    if (!map.has(am)) map.set(am, new Map());
    const key = fam + '|' + cap;
    map.get(am).set(key, (map.get(am).get(key) || 0) + 1);
  }
  return map;
}

function pickFamily(mapForAmmo) {
  // 取出现次数最多者；并列取容量更大者
  let best = null, bestCount = -1, bestCap = -1;
  for (const [key, count] of mapForAmmo) {
    const cap = Number(key.split('|')[1]);
    if (count > bestCount || (count === bestCount && cap > bestCap)) {
      best = key; bestCount = count; bestCap = cap;
    }
  }
  return best; // "family|cap"
}

function gunAmmoOf(commands) {
  const out = [];
  const text = commands.join('\n');
  let m; STORED.lastIndex = 0;
  while ((m = STORED.exec(text)) !== null) out.push(m[1]);
  return out;
}

function magId(ammo, family, cap) {
  return `taczmagazines:magazine{AmmoCount:${cap},AmmoId:"${ammo}",MagazineFamily:"${family}",MaxCapacity:${cap}}`;
}

const report = [];
const files = fs.readdirSync(SRC).filter((f) => f.endsWith('.json'));
let totalConverted = 0, totalFixed = 0, totalAdded = 0, totalKeptLauncher = 0, totalUnchangedNoGun = 0, totalDeduped = 0;

for (const f of files) {
  const j = JSON.parse(fs.readFileSync(SRC + f, 'utf8'));
  let converted = 0, fixed = 0, added = 0, keptLauncher = 0, noGunSkipped = 0, deduped = 0;

  for (const [cid, cls] of Object.entries(j.classes || {})) {
    for (const [vid, v] of Object.entries(cls.variants || {})) {
      if (!v.resupply || !Array.isArray(v.resupply.items)) continue;
      const commands = v.commands || [];
      const magMap = magMapOf(commands);
      const gunAmmos = gunAmmoOf(commands);              // 主武器弹药（按出现顺序）
      const primaryAmmo = gunAmmos.find((a) => magMap.has(a)) || null;
      const items = v.resupply.items;
      const out = [];
      const coveredAmmo = new Set();

      for (const it of items) {
        const id = String(it.id || '');
        const am = (id.match(/AmmoId\s*:\s*"([^"]+)"/) || [])[1];
        if (!/^tacz:ammo/i.test(id) || !am) { out.push(it); continue; }

        if (LAUNCHER_AMMO.has(am)) { out.push(it); keptLauncher++; continue; }

        let targetAmmo = am;
        let isMismatch = false;
        if (!magMap.has(am)) {
          if (!primaryAmmo) { out.push(it); noGunSkipped++; continue; }
          targetAmmo = primaryAmmo;
          isMismatch = true;
        }
        const key = pickFamily(magMap.get(targetAmmo));
        if (!key) { out.push(it); noGunSkipped++; continue; }
        const [family, capStr] = key.split('|');
        const cap = Number(capStr);
        const origMax = Number(it.max || it.count || 1);
        const maxMags = Math.max(1, Math.ceil(origMax / cap));
        const replaced = { id: magId(targetAmmo, family, cap), count: 1, max: maxMags };
        if (it.ammo_cost !== undefined) replaced.ammo_cost = it.ammo_cost;
        out.push(replaced);
        coveredAmmo.add(targetAmmo);
        converted++;
        if (isMismatch) fixed++;
      }

      // 补齐缺口：本变体有枪械弹药但无补给条目
      for (const am of new Set(gunAmmos)) {
        if (LAUNCHER_AMMO.has(am)) continue;
        if (!magMap.has(am) || coveredAmmo.has(am)) continue;
        const key = pickFamily(magMap.get(am));
        if (!key) continue;
        const [family, capStr] = key.split('|');
        out.push({ id: magId(am, family, Number(capStr)), count: 1, max: SIDEARM_DEFAULT_MAX });
        coveredAmmo.add(am);
        added++;
      }

      // 去重：同一弹匣条目出现多次时合并（max 取较大者，count 归 1，保留 ammo_cost）
      const merged = [];
      const byId = new Map();
      for (const it of out) {
        const key = String(it.id || '');
        if (!/^taczmagazines:/i.test(key)) { merged.push(it); continue; }
        const prev = byId.get(key);
        if (!prev) {
          const copy = { id: it.id, count: 1, max: Math.max(1, Number(it.max || 1)) };
          if (it.ammo_cost !== undefined) copy.ammo_cost = it.ammo_cost;
          byId.set(key, copy);
          merged.push(copy);
        } else {
          const newMax = Math.max(1, Number(it.max || 1));
          if (newMax > prev.max) prev.max = newMax;
          if (prev.ammo_cost === undefined && it.ammo_cost !== undefined) prev.ammo_cost = it.ammo_cost;
          deduped++;
        }
      }

      v.resupply.items = merged;
    }
  }
  totalConverted += converted; totalFixed += fixed; totalAdded += added;
  totalKeptLauncher += keptLauncher; totalUnchangedNoGun += noGunSkipped; totalDeduped += deduped;
  report.push(`${f}: 转换=${converted} 修正错配=${fixed} 新增缺口=${added} 保留发射器=${keptLauncher} 无枪跳过=${noGunSkipped} 去重=${deduped}`);

  if (WRITE) {
    fs.mkdirSync(DST, { recursive: true });
    fs.writeFileSync(DST + f, JSON.stringify(j, null, 2) + '\n', 'utf8');
  }
}

console.log(report.join('\n'));
console.log(`\n合计: 转换=${totalConverted} 修正错配=${totalFixed} 新增缺口=${totalAdded} 保留发射器=${totalKeptLauncher} 无枪跳过=${totalUnchangedNoGun} 去重=${totalDeduped}`);
console.log(WRITE ? '已写出到 ' + DST : '（未写盘，加 --write 才写出）');
