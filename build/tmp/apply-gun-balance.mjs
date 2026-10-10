import fs from 'node:fs';
import path from 'node:path';

/**
 * apply-gun-balance.mjs — 按规则改动服务端 TaCZ 枪械数据（文本级改动，保留注释与格式）
 * 用法: node apply-gun-balance.mjs <已抓取数据目录> <输出目录>
 */
const dataDir = process.argv[2];
const OUT = process.argv[3];
fs.mkdirSync(OUT, { recursive: true });

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

/** 找到 "key": <value> 中 value 的 [start,end)（字符串/注释安全） */
function valueRange(text, key, from = 0) {
  const re = new RegExp(`"${key}"\\s*:\\s*`, 'g');
  re.lastIndex = from;
  const m = re.exec(text);
  if (!m) return null;
  const start = m.index + m[0].length;
  const open = text[start];
  if (open !== '{' && open !== '[') {
    const m2 = /^-?[\d.]+|^(true|false|null)|^"[^"]*"/.exec(text.slice(start));
    return m2 ? { start, end: start + m2[0].length } : null;
  }
  const close = open === '{' ? '}' : ']';
  let depth = 0;
  for (let j = start; j < text.length; j++) {
    const c = text[j];
    if (c === '"') { j = text.indexOf('"', j + 1); if (j < 0) return null; continue; }
    if (c === '/' && text[j + 1] === '/') { while (j < text.length && text[j] !== '\n') j++; continue; }
    if (c === '/' && text[j + 1] === '*') { const e = text.indexOf('*/', j + 2); j = e < 0 ? text.length : e + 1; continue; }
    if (c === open) depth++;
    else if (c === close) { depth--; if (depth === 0) return { start, end: j + 1 }; }
  }
  return null;
}

/** 把 key 的值替换为 literal（literal 需自带引号，如果是字符串） */
function replaceValue(text, key, literal, from = 0) {
  const r = valueRange(text, key, from);
  if (!r) return null;
  return text.slice(0, r.start) + literal + text.slice(r.end);
}

const AIM = { rifle: 0.3, saw: 0.4, dmr: 0.4, gpmg: 0.5, pistol: 0.15, smg: 0.15 };
const plan = {
  'ccrp:ak74m':  { cat: 'rifle',  v: 440 },
  'ccrp:rpk74m': { cat: 'saw',    v: 480 },
  'ccrp:sr25':   { cat: 'dmr',    v: 400, note: 'M110 使用该数据' },
  'cib:pkp':     { cat: 'gpmg',   v: 412.5 },
  'cib:qbu191':  { cat: 'dmr',    v: 415, dmgPlus: 3 },
  'cib:qbz191':  { cat: 'rifle',  v: 465 },
  'cib:qbz192':  { cat: 'rifle',  v: 430 },
  'cib:qcw05':   { cat: 'smg',    v: 250 },
  'cib:qjb201':  { cat: 'saw',    v: 430 },
  'cib:qjy201':  { cat: 'gpmg',   v: 425 },
  'cib:qsz92':   { cat: 'pistol', v: 175 },
  'cib:svd':     { cat: 'dmr',    v: 415 },
  'suffuse:aks74u': { cat: 'rifle', v: 367.5 },
  'suffuse:tt33':   { cat: 'pistol', v: 210 },
  'tacz:m249':   { cat: 'saw',    v: 450 },
  'tacz:m4a1':   { cat: 'rifle',  v: 440 },
  'tacz:p320':   { cat: 'pistol', v: 175 },
  'cib:dzj08':          { cat: 'rpg' },
  'murasamet:rpg7_og7he': { cat: 'rpg' },
  'murasamet:rpg7_pg7heat': { cat: 'rpg' },
  'murasamet:rpg7_pg7vr_tandem_heat': { cat: 'rpg' },
  'suffuse:pf98a':      { cat: 'rpg' },
  'suffuse:qlz87':      { cat: 'rpg' },
  'tacz:m320':          { cat: 'rpg' },
  'ts:at4':             { cat: 'rpg' },
  'ts:gustavm4':        { cat: 'rpg' },
};

const DMG_BY_AMMO = {
  'tacz:58x42': [{ distance: 50, damage: 9 }, { distance: 100, damage: 8 }, { distance: 'infinite', damage: 6.5 }],
  'tacz:556x45': [{ distance: 50, damage: 8.8 }, { distance: 100, damage: 7.8 }, { distance: 'infinite', damage: 6 }],
  'tacz:545x39': [{ distance: 50, damage: 8.5 }, { distance: 100, damage: 7.5 }, { distance: 'infinite', damage: 5.5 }],
  'suffuse:545x39': [{ distance: 50, damage: 8.5 }, { distance: 100, damage: 7.5 }, { distance: 'infinite', damage: 5.5 }],
  'tacz:308': [{ distance: 75, damage: 15 }, { distance: 125, damage: 13 }, { distance: 'infinite', damage: 10 }],
  'tacz:762x39': [{ distance: 50, damage: 9.5 }, { distance: 100, damage: 8.5 }, { distance: 'infinite', damage: 7 }],
  'tacz:45acp': [{ distance: 10, damage: 7 }, { distance: 25, damage: 6 }, { distance: 45, damage: 5 }],
  'tacz:762x25': [{ distance: 10, damage: 7 }, { distance: 25, damage: 6 }, { distance: 45, damage: 5 }],
  'cib:58x21': [{ distance: 10, damage: 7 }, { distance: 25, damage: 6 }, { distance: 45, damage: 5 }],
};

const manifest = JSON.parse(fs.readFileSync(path.join(dataDir, 'manifest.json'), 'utf8'));
const gunFiles = new Map();
for (const m of manifest) if (m.kind === 'gun') gunFiles.set(m.id, m);

const report = [];
const outManifest = [];
for (const [gunId, cfg] of Object.entries(plan)) {
  const m = gunFiles.get(gunId);
  if (!m) { report.push(`${gunId}: ⚠ 缺少数据文件，跳过`); continue; }
  let text = fs.readFileSync(path.join(dataDir, m.file), 'utf8');
  const before = lenientParse(text);
  const notes = [];

  // 1) 瞄准时间（火箭筒不变）
  if (cfg.cat !== 'rpg') {
    const t = replaceValue(text, 'aim_time', String(AIM[cfg.cat]));
    if (t === null) notes.push('⚠ aim_time 未找到'); else text = t;
  }

  // 2) 扩散：站/移/蹲/卧 = 0.5，瞄准状态不动
  const inc = valueRange(text, 'inaccuracy');
  if (!inc) notes.push('⚠ inaccuracy 未找到');
  else {
    for (const k of ['stand', 'move', 'sneak', 'lie']) {
      const t = replaceValue(text, k, '0.5', inc.start);
      if (t === null) notes.push(`⚠ inaccuracy.${k} 未找到`); else text = t;
    }
  }

  // 3) 爆头倍率 = 2
  const t2 = replaceValue(text, 'head_shot_multiplier', '2');
  if (t2 === null) notes.push('⚠ head_shot_multiplier 未找到'); else text = t2;

  // 4) 伤害与弹速（火箭筒不变）
  if (cfg.cat !== 'rpg') {
    const table = DMG_BY_AMMO[before.ammo];
    if (!table) {
      notes.push(`⚠ 弹药 ${before.ammo} 不在伤害规则内（伤害不变）`);
    } else {
      const adjust = table.map(e => ({ distance: e.distance, damage: +(e.damage + (cfg.dmgPlus ?? 0)).toFixed(2) }));
      const bullet = valueRange(text, 'bullet');
      if (!bullet) notes.push('⚠ bullet 未找到');
      else {
        const t3 = replaceValue(text, 'damage', String(adjust[0].damage), bullet.start);
        if (t3 === null) notes.push('⚠ bullet.damage 未找到'); else text = t3;

        const bullet2 = valueRange(text, 'bullet');
        const ex = valueRange(text, 'extra_damage', bullet2.start);
        if (!ex) notes.push('⚠ extra_damage 未找到');
        else {
          const da = valueRange(text, 'damage_adjust', ex.start);
          if (!da) notes.push('⚠ damage_adjust 未找到');
          else {
            const arr = '[' + adjust.map(e => `{"distance":${typeof e.distance === 'string' ? `"${e.distance}"` : e.distance},"damage":${e.damage}}`).join(',') + ']';
            text = text.slice(0, da.start) + arr + text.slice(da.end);
          }
        }

        const bullet3 = valueRange(text, 'bullet');
        const t4 = replaceValue(text, 'speed', String(cfg.v), bullet3.start);
        if (t4 === null) notes.push('⚠ bullet.speed 未找到'); else text = t4;
      }
    }
  }

  let after;
  try { after = lenientParse(text); } catch (e) { report.push(`${gunId}: ✘ 改后无法解析：${e.message}`); continue; }

  // 除目标字段外，其它字段必须不变
  const flat = (o, p = '', out = {}) => {
    if (o === null || typeof o !== 'object') { out[p] = o; return out; }
    if (Array.isArray(o)) { o.forEach((v, i) => flat(v, `${p}[${i}]`, out)); return out; }
    for (const k of Object.keys(o)) flat(o[k], p ? `${p}.${k}` : k, out);
    return out;
  };
  const fa = flat(before), fb = flat(after);
  const target = (k) => /^(aim_time|inaccuracy\.(stand|move|sneak|lie)|bullet\.damage|bullet\.speed|bullet\.extra_damage\.head_shot_multiplier|bullet\.extra_damage\.damage_adjust(\[\d+\]\.(distance|damage))?)$/.test(k);
  const stray = [...new Set([...Object.keys(fa), ...Object.keys(fb)])].filter(k => !target(k) && JSON.stringify(fa[k]) !== JSON.stringify(fb[k]));

  const outFile = `${gunId.replace(':', '__')}__${path.basename(m.file)}`;
  fs.writeFileSync(path.join(OUT, outFile), text, 'utf8');
  outManifest.push({ gun: gunId, cat: cfg.cat, serverRel: m.serverRel, file: outFile, ammo: before.ammo });

  const diff = [];
  if (cfg.cat !== 'rpg') diff.push(`瞄准 ${before.aim_time}→${after.aim_time}`);
  diff.push(`扩散 ${before.inaccuracy?.stand}/${before.inaccuracy?.move}/${before.inaccuracy?.sneak}/${before.inaccuracy?.lie}/镜${before.inaccuracy?.aim} → ${after.inaccuracy?.stand}/${after.inaccuracy?.move}/${after.inaccuracy?.sneak}/${after.inaccuracy?.lie}/镜${after.inaccuracy?.aim}`);
  diff.push(`爆头 ${before.bullet?.extra_damage?.head_shot_multiplier}→${after.bullet?.extra_damage?.head_shot_multiplier}`);
  if (cfg.cat !== 'rpg') {
    diff.push(`伤害 ${before.bullet?.damage}→${after.bullet?.damage}`);
    diff.push(`弹速 ${before.bullet?.speed}→${after.bullet?.speed}`);
    diff.push(`衰减 → ${JSON.stringify(after.bullet?.extra_damage?.damage_adjust)}`);
  }
  report.push(`${gunId}  [${cfg.cat}] ${notes.join(' ')}`);
  report.push(`    ${diff.join(' | ')}${stray.length ? `  ✘ 额外改动: ${stray.join(', ')}` : ''}`);
}
fs.writeFileSync(path.join(OUT, 'manifest.json'), JSON.stringify(outManifest, null, 2), 'utf8');
console.log(report.join('\n'));
console.log(`\n已写出 ${outManifest.length} 个文件 → ${OUT}`);
