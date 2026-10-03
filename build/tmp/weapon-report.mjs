import fs from 'node:fs';
import path from 'node:path';

// weapon-report.mjs — 生成"编制在用武器"的服务端数据表
// 用法: node weapon-report.mjs <tacz目录> <srv-factions目录>
const taczDir = process.argv[2];
const factionDir = process.argv[3];

function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) {
      out += c;
      if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false;
      continue;
    }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const end = text.indexOf('*/', i + 2); i = end < 0 ? text.length : end + 1; out += '\n'; continue; }
    out += c;
  }
  out = out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1');
  return JSON.parse(out);
}

const packs = fs.readdirSync(taczDir).map(n => path.join(taczDir, n))
  .filter(p => { try { return fs.statSync(p).isDirectory(); } catch { return false; } })
  .map(p => {
    let ns = null;
    try { ns = JSON.parse(fs.readFileSync(path.join(p, 'gunpack.meta.json'), 'utf8')).namespace; } catch { }
    return { dir: p, name: path.basename(p), ns };
  }).filter(p => p.ns);

const index = new Map();   // ns:gun -> index json
const dataFiles = new Map(); // ns:name -> full path
const langs = new Map();   // ns -> {zh_cn, en_us}
const attachFiles = new Map(); // ns:id -> path

for (const pack of packs) {
  const root = path.join(pack.dir, 'data');
  if (!fs.existsSync(root)) continue;
  for (const ns of fs.readdirSync(root)) {
    const idxDir = path.join(root, ns, 'index', 'guns');
    if (fs.existsSync(idxDir)) for (const f of fs.readdirSync(idxDir).filter(x => x.endsWith('.json'))) {
      try { index.set(`${ns}:${f.replace(/\.json$/, '')}`, lenientParse(fs.readFileSync(path.join(idxDir, f), 'utf8'))); } catch { }
    }
    for (const sub of ['guns', 'attachments']) {
      const d = path.join(root, ns, 'data', sub);
      if (fs.existsSync(d)) for (const f of fs.readdirSync(d).filter(x => x.endsWith('.json'))) {
        const key = `${ns}:${f.replace(/_data\.json$/, '').replace(/\.json$/, '')}`;
        if (sub === 'guns') dataFiles.set(key, path.join(d, f)); else attachFiles.set(key, path.join(d, f));
      }
    }
    const langDir = path.join(pack.dir, 'assets', ns, 'lang');
    if (fs.existsSync(langDir)) {
      const store = langs.get(ns) ?? {};
      for (const lang of ['zh_cn', 'en_us']) {
        const lp = path.join(langDir, `${lang}.json`);
        if (!store[lang] && fs.existsSync(lp)) { try { store[lang] = lenientParse(fs.readFileSync(lp, 'utf8')); } catch { store[lang] = {}; } }
      }
      langs.set(ns, store);
    }
  }
}

const stripColor = (s) => String(s || '').replace(/§./g, '').trim();

function resolveGun(id) {
  const idx = index.get(id);
  let dataId = idx?.data ? idx.data.replace(/_data$/, '') : id;
  let file = dataFiles.get(dataId);
  if (!file) file = dataFiles.get(id);
  if (!file) return null;
  return { idx, data: lenientParse(fs.readFileSync(file, 'utf8')), file, dataId };
}

function displayName(id, idx) {
  const ns = id.split(':')[0];
  const store = langs.get(ns) || {};
  const key = idx?.name;
  if (!key) return '';
  return stripColor(store.zh_cn?.[key] || store.en_us?.[key] || '');
}

// 编制用到的武器 + 使用它的编制/职业
const usage = new Map();
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  const fac = stripColor(j.faction?.name) || f.replace('.json', '');
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, va] of Object.entries(cls.variants || {})) {
      for (const cmd of (va.commands || [])) {
        const m = String(cmd).match(/GunId:"([^"]+)"/);
        if (!m) continue;
        const id = m[1];
        if (!usage.has(id)) usage.set(id, []);
        usage.get(id).push({ fac, cls, role: stripColor(cls.name), variant: vk, vname: stripColor(va.name), cmd: String(cmd) });
      }
    }
  }
}

const rows = [];
for (const id of [...usage.keys()].sort()) {
  const g = resolveGun(id);
  if (!g) { rows.push({ id, missing: true, users: usage.get(id).length }); continue; }
  const d = g.data, b = d.bullet || {}, ex = b.extra_damage || {}, inc = d.inaccuracy || {}, semi = d.fire_mode_adjust?.semi || {};
  rows.push({
    id, name: displayName(id, g.idx), type: g.idx?.type, ammo: d.ammo,
    users: usage.get(id).length,
    factions: [...new Set(usage.get(id).map(u => u.fac))].length,
    dmg: b.damage, dmgSemi: semi.damage !== undefined ? (b.damage ?? 0) + semi.damage : null,
    head: ex.head_shot_multiplier, armor: ex.armor_ignore, pierce: b.pierce,
    falloff: Array.isArray(ex.damage_adjust) ? ex.damage_adjust.map(x => `${x.distance}m ${x.damage}`).join(' / ') : '',
    aim: d.aim_time, draw: d.draw_time,
    incStand: inc.stand, incMove: inc.move, incSneak: inc.sneak, incLie: inc.lie, incAim: inc.aim,
    rpm: d.rpm, speed: b.speed, mag: d.ammo_amount,
    magExt: Array.isArray(d.extended_mag_ammo_amount) ? d.extended_mag_ammo_amount.join('/') : '',
    weight: d.weight, modes: (d.fire_mode || []).join('/'),
    sharedData: g.dataId !== id ? g.dataId : null,
  });
}

console.log('| 武器 (id) | 名称 | 类型 | 伤害 | 半自动伤害 | 爆头× | 无视护甲 | 穿透 | 瞄准时间(ADS) | 扩散(站/移/蹲/卧/开镜) | RPM | 弹速 | 弹匣(扩容) | 重量 | 距离衰减 |');
console.log('|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|');
for (const r of rows) {
  if (r.missing) { console.log(`| ${r.id} | ⚠ 无数据文件（${r.users} 个职业在用） | | | | | | | | | | | | | |`); continue; }
  console.log(`| ${r.id}${r.sharedData ? ` <br>(数据取自 ${r.sharedData})` : ''} | ${r.name} | ${r.type ?? ''} | ${r.dmg} | ${r.dmgSemi ?? '—'} | ${r.head} | ${r.armor} | ${r.pierce} | ${r.aim}s | ${r.incStand}/${r.incMove}/${r.incSneak}/${r.incLie}/${r.incAim} | ${r.rpm} | ${r.speed} | ${r.mag}${r.magExt ? ` (${r.magExt})` : ''} | ${r.weight} | ${r.falloff} |`);
}

console.log('\n--- 附件（编制里实际用到的）---');
const attachIds = new Set();
for (const list of usage.values()) for (const u of list) {
  for (const m of u.cmd.matchAll(/Attachment([A-Z_]+):\{Count:\d+b,id:"tacz:attachment",tag:\{AttachmentId:"([^"]+)"\}\}/g)) attachIds.add(m[2]);
}
for (const aid of [...attachIds].sort()) {
  const f = attachFiles.get(aid);
  if (!f) continue;
  try {
    const a = lenientParse(fs.readFileSync(f, 'utf8'));
    const mods = a.modifier || a.modifiers || a.effects || {};
    const modText = Object.entries(mods).filter(([, v]) => typeof v === 'number' || typeof v === 'string' || Array.isArray(v))
      .map(([k, v]) => `${k}=${JSON.stringify(v)}`).join(' ');
    console.log(`  ${aid.padEnd(30)} ${modText || '（无 modifier，仅外观/倍率）'}`);
  } catch (e) { console.log(`  ${aid.padEnd(30)} ⚠ 解析失败`); }
}
