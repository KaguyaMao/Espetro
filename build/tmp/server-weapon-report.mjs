import fs from 'node:fs';
import path from 'node:path';

// server-weapon-report.mjs — 用"从服务端抓下来的" TaCZ 数据生成武器数据表
// 用法: node server-weapon-report.mjs <数据目录(含 manifest.json)> <本地tacz目录> <编制目录>
const dataDir = process.argv[2];
const localTaczDir = process.argv[3];
const factionDir = process.argv[4];

function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; out += '\n'; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1'));
}

const manifest = JSON.parse(fs.readFileSync(path.join(dataDir, 'manifest.json'), 'utf8'));
const byKey = new Map();
for (const m of manifest) byKey.set(`${m.kind}|${m.id}`, m);
const readSrv = (kind, id) => { const m = byKey.get(`${kind}|${id}`); return m ? lenientParse(fs.readFileSync(path.join(dataDir, m.file), 'utf8')) : null; };

// 本地语言文件（名称仅供显示）
const langs = new Map();
for (const pack of fs.readdirSync(localTaczDir)) {
  const p = path.join(localTaczDir, pack);
  try { if (!fs.statSync(p).isDirectory()) continue; } catch { continue; }
  const ad = path.join(p, 'assets');
  if (!fs.existsSync(ad)) continue;
  for (const ns of fs.readdirSync(ad)) {
    const ld = path.join(ad, ns, 'lang');
    if (!fs.existsSync(ld)) continue;
    const store = langs.get(ns) ?? {};
    for (const l of ['zh_cn', 'en_us']) { const lp = path.join(ld, `${l}.json`); if (!store[l] && fs.existsSync(lp)) { try { store[l] = lenientParse(fs.readFileSync(lp, 'utf8')); } catch { store[l] = {}; } } }
    langs.set(ns, store);
  }
}
const clean = (s) => String(s || '').replace(/§./g, '').replace(/\|/g, '·').replace(/\s+/g, ' ').trim();
const nameOf = (id) => { const idx = readSrv('index', id); const ns = id.split(':')[0]; const st = langs.get(ns) || {}; const k = idx?.name; return k ? clean(st.zh_cn?.[k] || st.en_us?.[k] || '') : ''; };

// 本地同名文件（用于对比服务端是否被改过）
const localFileOf = (serverRel) => path.join(localTaczDir, serverRel.replace(/^tacz\//, ''));
const localNormalized = (serverRel) => {
  const p = localFileOf(serverRel);
  if (!fs.existsSync(p)) return null;
  try { return JSON.stringify(lenientParse(fs.readFileSync(p, 'utf8'))); } catch { return null; }
};

// 编制里的 枪+附件 组合
const combos = new Map();
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  const fac = clean(j.faction?.name) || f.replace('.json', '');
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      for (const cmd of (va.commands || [])) {
        const s = String(cmd);
        const gm = s.match(/GunId:"([^"]+)"/);
        if (!gm) continue;
        const attach = {};
        for (const m of s.matchAll(/\[?Attachment([A-Z_]+)\]?:\{Count:\d+b,id:"tacz:attachment",tag:\{AttachmentId:"([^"]+)"\}\}/g)) attach[m[1]] = m[2];
        const key = gm[1] + '|' + Object.entries(attach).sort().map(([k, v]) => `${k}=${v}`).join(',');
        if (!combos.has(key)) combos.set(key, { gun: gm[1], attach, users: [] });
        combos.get(key).users.push(`${fac} ${clean(cls.name)}/${vk}`);
      }
    }
  }
}

console.log('# 编制武器：服务端 TaCZ 数据\n');
console.log('## 1) 基础数据（服务端枪包数据，未计附件）\n');
console.log('| 武器 | 名称 | 类型 | 伤害 | 爆头× | 无视护甲 | 穿透 | 瞄准时间 | 扩散(站/移/蹲/卧/开镜) | RPM | 弹速 | 弹匣(扩容) | 重量 | 距离衰减 | 服务端改动 |');
console.log('|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|');
const gunIds = [...new Set([...combos.values()].map(c => c.gun))].sort();
for (const id of gunIds) {
  const g = readSrv('gun', id) ?? null;
  const idx = readSrv('index', id);
  let dataId = idx?.data ? idx.data.replace(/_data$/, '') : id;
  const data = g ?? readSrv('gun', dataId);
  if (!data) { console.log(`| ${id} | ⚠ 缺数据 | | | | | | | | | | | | | |`); continue; }
  const b = data.bullet || {}, ex = b.extra_damage || {}, inc = data.inaccuracy || {}, semi = data.fire_mode_adjust?.semi || {};
  const gunEntry = byKey.get(`gun|${dataId}`) ?? byKey.get(`gun|${id}`);
  const srvNorm = gunEntry ? JSON.stringify(data) : '';
  const locNorm = gunEntry ? localNormalized(gunEntry.serverRel) : null;
  const changed = locNorm && srvNorm && locNorm !== srvNorm ? '**是**' : '';
  const dmg = semi.damage !== undefined ? `${b.damage} / ${b.damage + semi.damage}` : `${b.damage}`;
  console.log(`| ${id}${dataId !== id ? ` <br>(数据=${dataId})` : ''} | ${nameOf(id)} | ${idx?.type ?? ''} | ${dmg} | ${ex.head_shot_multiplier} | ${ex.armor_ignore} | ${b.pierce} | ${data.aim_time}s | ${inc.stand}/${inc.move}/${inc.sneak}/${inc.lie}/${inc.aim} | ${data.rpm} | ${b.speed} | ${data.ammo_amount}${Array.isArray(data.extended_mag_ammo_amount) ? ` (${data.extended_mag_ammo_amount.join('/')})` : ''} | ${data.weight} | ${Array.isArray(ex.damage_adjust) ? ex.damage_adjust.map(x => `${x.distance}m ${x.damage}`).join(' / ') : ''} | ${changed} |`);
}

console.log('\n（如需按配装计算的有效值/附件表，告诉我再加。）\n');

console.log('| 枪 | 附件 | 伤害(自动/半自动) | 有效瞄准时间 | 有效重量 | 弹匣 | 开镜扩散 | 腰射扩散(站) | 后坐(x/y) | 使用者 |');
console.log('|---|---|---|---|---|---|---|---|---|---|');
for (const [, c] of [...combos.entries()].sort()) {
  const idx = readSrv('index', c.gun);
  const dataId = idx?.data ? idx.data.replace(/_data$/, '') : c.gun;
  const g = readSrv('gun', dataId) ?? readSrv('gun', c.gun);
  if (!g) { console.log(`| ${c.gun} | ${Object.values(c.attach).join(' ') || '—'} | ⚠ 缺数据 | | | | | | | ${c.users.length} |`); continue; }
  const mods = Object.values(c.attach).map(a => readSrv('attach', a)).filter(Boolean);
  let adsAdd = 0, adsMul = 1, inaccMul = 1, aimInaccMul = 1, pitchMul = 1, yawMul = 1;
  for (const m of mods) {
    adsAdd += Number(m.ads?.addend ?? 0) + Number(m.ads_addend ?? 0);
    adsMul *= Number(m.ads?.multiplier ?? 1);
    inaccMul *= Number(m.inaccuracy?.multiplier ?? 1);
    aimInaccMul *= Number(m.aim_inaccuracy?.multiplier ?? 1);
    pitchMul *= Number(m.recoil?.pitch?.multiplier ?? 1);
    yawMul *= Number(m.recoil?.yaw?.multiplier ?? 1);
  }
  const ads = (Number(g.aim_time ?? 0) + adsAdd) * adsMul;
  const weight = Number(g.weight ?? 0) + mods.reduce((s, m) => s + Number(m.weight ?? 0), 0);
  const lvl = Math.max(0, ...mods.map(m => Number(m.extended_mag_level ?? 0)));
  const mag = lvl > 0 && Array.isArray(g.extended_mag_ammo_amount) ? g.extended_mag_ammo_amount[Math.min(lvl, g.extended_mag_ammo_amount.length - 1)] : g.ammo_amount;
  const semiAdd = Number(g.fire_mode_adjust?.semi?.aim_inaccuracy ?? 0);
  const dmgBase = Number(g.bullet?.damage ?? 0);
  const dmgSemi = dmgBase + Number(g.fire_mode_adjust?.semi?.damage ?? 0);
  const aimInacc = ((Number(g.inaccuracy?.aim ?? 0) + semiAdd) * aimInaccMul * inaccMul).toFixed(3);
  const standInacc = (Number(g.inaccuracy?.stand ?? 0) * inaccMul).toFixed(2);
  const rec = g.recoil?.pitch?.[0]?.value, recy = g.recoil?.yaw?.[0]?.value;
  const recText = rec ? `${(rec[0] * pitchMul).toFixed(2)}~${(rec[1] * pitchMul).toFixed(2)} / ${(recy[0] * yawMul).toFixed(2)}~${(recy[1] * yawMul).toFixed(2)}` : '';
  const attText = Object.entries(c.attach).map(([k, v]) => `${k}=${v.split(':')[1]}`).join(' ') || '（裸枪）';
  const dmgText = g.fire_mode_adjust?.semi?.damage !== undefined ? `${dmgBase} / ${dmgSemi}` : `${dmgBase}`;
  console.log(`| ${c.gun.split(':')[1]} | ${clean(attText)} | ${dmgText} | ${ads.toFixed(3)}s | ${weight.toFixed(2)} | ${mag} | ${aimInacc} | ${standInacc} | ${recText} | ${c.users.length} 个职业 |`);
}
