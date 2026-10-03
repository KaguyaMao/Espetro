// compat-check.mjs — 本地复现 FactionDataLoader.isCompatibleWithMap 的前半段判定
import fs from 'fs';

const FA = 'D:/minecraft/modp/Espetro/build/tmp/fxall-icons/';
const MAPS = {
  server_battlefield: 'D:/minecraft/modp/Espetro/build/tmp/maps/hs/VehSpawn.json',
  '越南': 'D:/minecraft/modp/Espetro/build/tmp/maps/vn/VehSpawn.json',
  CREATE_PLUS: 'D:/minecraft/modp/Espetro/build/tmp/maps/cp/VehSpawn.json'
};

function parseVehSpawn(path) {
  const root = JSON.parse(fs.readFileSync(path, 'utf8'));
  const typesEl = root.VehTypes ?? root.vehtypes ?? root.vehicle_types;
  const types = (typesEl ?? []).map((t) => String(t).trim().toLowerCase());
  const sp = root.spawn_points ?? {};
  const counts = {};
  for (const t of types) {
    const el = sp[t];
    if (Array.isArray(el)) counts[t] = el.length;
    else if (el && typeof el === 'object') counts[t] = Object.keys(el).length;
    else counts[t] = -1; // 缺失
  }
  return { types, counts, root };
}

const maps = {};
for (const [name, p] of Object.entries(MAPS)) maps[name] = parseVehSpawn(p);

const factions = fs.readdirSync(FA).filter((f) => f.endsWith('.json')).sort();
for (const [mapName, m] of Object.entries(maps)) {
  console.log(`\n========== 地图 ${mapName}`);
  console.log(`VehSpawn 类型(${m.types.length}): ` + m.types.map((t) => `${t}=${m.counts[t]}`).join(' '));
  for (const f of factions) {
    const j = JSON.parse(fs.readFileSync(FA + f, 'utf8'));
    const declared = (j.VehTypes ?? []).map((t) => String(t).trim().toLowerCase());
    const vehicles = j.vehicles ?? {};
    const reasons = [];
    for (const t of declared) if (!m.types.includes(t)) reasons.push(`VehTypes '${t}' 不在地图 VehSpawn`);
    for (const [t, v] of Object.entries(vehicles)) {
      const cnt = m.counts[t];
      if (cnt === undefined || cnt === -1) { reasons.push(`类型 '${t}' 无出生点`); continue; }
      const ents = v.entity ?? [];
      if (ents.length > cnt) reasons.push(`类型 '${t}' entities(${ents.length}) > spawn点数(${cnt})`);
    }
    const mark = reasons.length ? '排除' : '通过';
    console.log(`${mark} ${f.replace('.json', '').padEnd(26)} ${reasons.join('; ')}`);
  }
}
