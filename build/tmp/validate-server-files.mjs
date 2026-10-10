// validate-server-files.mjs — 对服务器当前 EsFactions 文件做与模组一致的校验
import fs from 'fs';

const DIR = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/srv-all/';
const MAPS = {
  server_battlefield: 'D:/minecraft/modp/Espetro/build/tmp/maps/hs/VehSpawn.json',
  '越南': 'D:/minecraft/modp/Espetro/build/tmp/maps/vn/VehSpawn.json',
  CREATE_PLUS: 'D:/minecraft/modp/Espetro/build/tmp/maps/cp/VehSpawn.json'
};

function parseVehSpawn(p) {
  const root = JSON.parse(fs.readFileSync(p, 'utf8'));
  const types = (root.VehTypes ?? root.vehtypes ?? root.vehicle_types ?? []).map((t) => String(t).trim().toLowerCase());
  const sp = root.spawn_points ?? {};
  const counts = {};
  for (const t of types) {
    const el = sp[t];
    counts[t] = Array.isArray(el) ? el.length : (el && typeof el === 'object' ? Object.keys(el).length : -1);
  }
  return { types, counts };
}

const maps = {};
for (const [n, p] of Object.entries(MAPS)) maps[n] = parseVehSpawn(p);

const files = fs.readdirSync(DIR).filter((f) => f.endsWith('.json')).sort();
console.log('=== 1) JSON 语法 / 结构校验（模组 loadExternalFrozen 同类检查）');
const ok = {};
for (const f of files) {
  const raw = fs.readFileSync(DIR + f, 'utf8');
  let j;
  try { j = JSON.parse(raw); }
  catch (e) {
    // 定位报错行内容，便于人工修
    const m = /position (\d+)/.exec(e.message);
    const pos = m ? Number(m[1]) : -1;
    let ctx = '';
    if (pos >= 0) {
      const before = raw.slice(0, pos);
      const line = before.split('\n').length;
      const lines = raw.split('\n');
      ctx = ` 行${line}: ${String(lines[line - 1]).trim().slice(0, 90)} | 下一行: ${String(lines[line] ?? '').trim().slice(0, 60)}`;
    }
    console.log(`拒载 ${f}: ${e.message}${ctx ? '\n     ' + ctx : ''}`);
    ok[f] = false;
    continue;
  }
  const problems = [];
  if (!j.faction) problems.push('缺少 faction 节点');
  if (!j.VehTypes) problems.push('缺少 VehTypes 数组');
  if (j.faction && !j.faction.faction_id) problems.push('faction.faction_id 缺失');
  const declared = (j.VehTypes ?? []).map((t) => String(t).trim().toLowerCase());
  const dup = declared.filter((t, i) => declared.indexOf(t) !== i);
  if (dup.length) problems.push('VehTypes 重复: ' + dup.join(','));
  for (const t of Object.keys(j.vehicles ?? {})) {
    if (!declared.includes(t)) problems.push(`vehicles.${t} 未在 VehTypes 声明`);
  }
  const clsCount = Object.keys(j.classes ?? {}).length;
  console.log(`${problems.length ? '异常' : '通过'} ${f.padEnd(28)} 职业=${String(clsCount).padStart(3)} VehTypes=${declared.length} 载具=${Object.keys(j.vehicles ?? {}).length} ${problems.join('; ')}`);
  ok[f] = problems.length === 0;
}

console.log('\n=== 2) 地图兼容（VehTypes ⊆ 地图 VehSpawn、每类型 entities ≤ 出生点数）');
for (const [mapName, m] of Object.entries(maps)) {
  console.log(`\n--- 地图 ${mapName}  VehSpawn: ` + m.types.map((t) => `${t}=${m.counts[t]}`).join(' '));
  let compatible = 0;
  const affiliations = new Set();
  for (const f of files) {
    if (!ok[f]) { console.log(`  × ${f.replace('.json', '')} —— 文件本身被拒载`); continue; }
    const j = JSON.parse(fs.readFileSync(DIR + f, 'utf8'));
    const declared = (j.VehTypes ?? []).map((t) => String(t).trim().toLowerCase());
    const reasons = [];
    for (const t of declared) if (!m.types.includes(t)) reasons.push(`VehTypes '${t}' 不在地图 VehSpawn`);
    for (const [t, v] of Object.entries(j.vehicles ?? {})) {
      const c = m.counts[t];
      if (c === undefined || c === -1) { reasons.push(`类型 '${t}' 无出生点`); continue; }
      const ents = v.entity ?? [];
      if (ents.length > c) reasons.push(`类型 '${t}' entities(${ents.length}) > spawn点数(${c})`);
    }
    if (reasons.length) console.log(`  × 排除 ${f.replace('.json', '').padEnd(24)} ${reasons.join('; ')}`);
    else { compatible++; affiliations.add(j.faction?.faction_id ?? '?'); console.log(`  √ 通过 ${f.replace('.json', '')}`); }
  }
  console.log(`  兼容编制 ${compatible}，不同 faction_id ${affiliations.size} → ${affiliations.size >= 2 ? '地图可玩' : '地图不可玩(需≥2)'}`);
}
