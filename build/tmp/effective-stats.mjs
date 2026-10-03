import fs from 'node:fs';
import path from 'node:path';

// effective-stats.mjs — 按编制里的实际配装计算每条"枪+附件"组合的有效数据
// 用法: node effective-stats.mjs <tacz目录> <factions目录>
const taczDir = process.argv[2];
const factionDir = process.argv[3];

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

const gunFiles = new Map(), attachFiles = new Map(), index = new Map(), langs = new Map();
for (const pack of fs.readdirSync(taczDir)) {
  const root = path.join(taczDir, pack, 'data');
  if (!fs.existsSync(root)) continue;
  for (const ns of fs.readdirSync(root)) {
    const gd = path.join(root, ns, 'data', 'guns'), ad = path.join(root, ns, 'data', 'attachments'), ix = path.join(root, ns, 'index', 'guns');
    if (fs.existsSync(gd)) for (const f of fs.readdirSync(gd).filter(x => x.endsWith('.json'))) gunFiles.set(`${ns}:${f.replace(/_data\.json$/, '')}`, path.join(gd, f));
    if (fs.existsSync(ad)) for (const f of fs.readdirSync(ad).filter(x => x.endsWith('.json'))) attachFiles.set(`${ns}:${f.replace(/_data\.json$/, '')}`, path.join(ad, f));
    if (fs.existsSync(ix)) for (const f of fs.readdirSync(ix).filter(x => x.endsWith('.json'))) { try { index.set(`${ns}:${f.replace(/\.json$/, '')}`, lenientParse(fs.readFileSync(path.join(ix, f), 'utf8'))); } catch { } }
    const ld = path.join(taczDir, pack, 'assets', ns, 'lang');
    if (fs.existsSync(ld)) {
      const store = langs.get(ns) ?? {};
      for (const l of ['zh_cn', 'en_us']) { const lp = path.join(ld, `${l}.json`); if (!store[l] && fs.existsSync(lp)) { try { store[l] = lenientParse(fs.readFileSync(lp, 'utf8')); } catch { store[l] = {}; } } }
      langs.set(ns, store);
    }
  }
}

const loadJson = (map, id) => { const f = map.get(id); if (!f) return null; try { return lenientParse(fs.readFileSync(f, 'utf8')); } catch { return null; } };
const gunOf = (id) => { const idx = index.get(id); const target = idx?.data ? idx.data.replace(/_data$/, '') : id; return loadJson(gunFiles, target) ?? loadJson(gunFiles, id); };
const clean = (s) => String(s || '').replace(/§./g, '').replace(/\|/g, '·').replace(/\s+/g, ' ').trim();
const nameOf = (id) => { const ns = id.split(':')[0], key = index.get(id)?.name, st = langs.get(ns) || {}; return key ? clean(st.zh_cn?.[key] || st.en_us?.[key] || '') : ''; };

// 收集每条 枪+附件 组合及其使用者
const combos = new Map();
for (const f of fs.readdirSync(factionDir).filter(x => x.endsWith('.json'))) {
  const j = JSON.parse(fs.readFileSync(path.join(factionDir, f), 'utf8'));
  const fac = String(j.faction?.name || f).replace(/§./g, '').trim();
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
        combos.get(key).users.push(`${fac} ${String(cls.name)}/${vk}`);
      }
    }
  }
}

console.log('| 枪 | 附件 | 伤害(全自动/半自动) | 有效瞄准时间(ADS) | 有效重量 | 弹匣 | 开镜扩散 | 腰射扩散(站) | 使用者数 |');
console.log('|---|---|---|---|---|---|---|---|---|');
for (const [key, c] of [...combos.entries()].sort()) {
  const g = gunOf(c.gun);
  if (!g) { console.log(`| ${c.gun} | ${Object.values(c.attach).join(',') || '—'} | ⚠ 无数据 | | | | | | ${c.users.length} |`); continue; }
  const mods = Object.values(c.attach).map(a => loadJson(attachFiles, a)).filter(Boolean);
  const b = g.bullet || {};
  // ADS：先加 addend（ads.addend 与 ads_addend 都算加数），再乘 multiplier
  let adsAdd = 0, adsMul = 1;
  for (const m of mods) {
    adsAdd += Number(m.ads?.addend ?? 0) + Number(m.ads_addend ?? 0);
    adsMul *= Number(m.ads?.multiplier ?? 1);
  }
  const ads = ((Number(g.aim_time ?? 0) + adsAdd) * adsMul);
  // 重量
  const weight = Number(g.weight ?? 0) + mods.reduce((s, m) => s + Number(m.weight ?? 0), 0);
  // 弹匣（扩容等级）
  const lvl = Math.max(0, ...mods.map(m => Number(m.extended_mag_level ?? 0)));
  const mag = lvl > 0 && Array.isArray(g.extended_mag_ammo_amount) ? g.extended_mag_ammo_amount[Math.min(lvl, g.extended_mag_ammo_amount.length - 1)] : g.ammo_amount;
  // 扩散修正
  const inaccMul = mods.reduce((s, m) => s * Number(m.inaccuracy?.multiplier ?? 1), 1);
  const aimInaccMul = mods.reduce((s, m) => s * Number(m.aim_inaccuracy?.multiplier ?? 1), 1);
  const semiAdd = Number(g.fire_mode_adjust?.semi?.aim_inaccuracy ?? 0);
  const dmgBase = Number(b.damage ?? 0);
  const dmgSemi = dmgBase + Number(g.fire_mode_adjust?.semi?.damage ?? 0);
  const aimInacc = ((Number(g.inaccuracy?.aim ?? 0) + semiAdd) * aimInaccMul * inaccMul).toFixed(3);
  const standInacc = (Number(g.inaccuracy?.stand ?? 0) * inaccMul).toFixed(2);
  const attText = Object.entries(c.attach).map(([k, v]) => `${k}:${v.split(':')[1]}`).join(' ') || '—';
  const dmgText = g.fire_mode_adjust?.semi?.damage !== undefined ? `${dmgBase} / ${dmgSemi}` : `${dmgBase}`;
  console.log(`| ${c.gun} ${nameOf(c.gun)} | ${clean(attText)} | ${dmgText} | ${ads.toFixed(3)}s | ${weight.toFixed(2)} | ${mag} | ${aimInacc} | ${standInacc} | ${c.users.length} |`);
}
