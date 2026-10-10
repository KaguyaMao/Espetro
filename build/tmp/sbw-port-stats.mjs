// sbw-port-stats.mjs — 把 fcp 斯崔克的战斗数值移植到 dragonrise 斯崔克（按位置精确改写，保留原格式）
// 用法: node sbw-port-stats.mjs [--write]
import fs from 'fs';

const DIR = 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/';
const OUT = 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh-out/';
const WRITE = process.argv.includes('--write');

// 一级 + 二级字段（fcp 有才抄，fcp 没有的保持 dr 原值）
const WEAPON_FIELDS = ['RPM', 'Damage', 'BypassesArmor', 'ExplosionRadius', 'ExplosionDamage',
  'Velocity', 'Spread', 'HeatPerShoot', 'NaturalCooldown', 'RecoilTime', 'RecoilForce',
  'Magazine', 'EmptyReloadTime', 'SoundRadius'];
// 弹种 override 里要抄的数值（fcp 有才抄）
const AMMO_FIELDS = ['Velocity', 'Damage', 'ExplosionDamage', 'ExplosionRadius', 'Spread', 'RecoilTime', 'RecoilForce', 'RPM'];

const PAIRS = [
  { fcp: 'fcp/stryker_m2.json', dr: 'dr/m1126.json', weapons: [['Coax', 'MachineGun']] },
  { fcp: 'fcp/stryker_mgs.json', dr: 'dr/m1128.json', weapons: [['Cannon', 'Cannon'], ['Coax', 'MachineGun'], ['PassengerMachineGun', 'PassengerMachineGun']] },
  { fcp: 'fcp/stryker_dragoon.json', dr: 'dr/m1296.json', weapons: [['Cannon', 'Cannon'], ['Coax', 'MachineGun']] }
];

// ---------- 位置感知 JSON 解析 ----------
function parseWithSpans(text) {
  let i = 0;
  const spans = new Map();          // path -> {start,end,kind}
  const members = new Map();        // objectPath -> [{key,keyStart,valueStart,valueEnd}]
  const skipWs = () => { while (i < text.length && /\s/.test(text[i])) i++; };
  const parseString = () => {
    if (text[i] !== '"') throw new Error('expected string @' + i);
    i++;
    let out = '';
    while (i < text.length) {
      const c = text[i];
      if (c === '\\') { out += text.slice(i, i + 2); i += 2; continue; }
      if (c === '"') { i++; return JSON.parse('"' + out + '"'); }
      out += c; i++;
    }
    throw new Error('unterminated string');
  };
  function parseValue(path) {
    skipWs();
    const start = i;
    const c = text[i];
    if (c === '{') {
      i++; skipWs();
      const list = [];
      if (text[i] === '}') { i++; spans.set(path, { start, end: i, kind: 'object' }); members.set(path, list); return; }
      while (true) {
        skipWs();
        const keyStart = i;
        const key = parseString();
        skipWs();
        if (text[i] !== ':') throw new Error('expected ":" @' + i);
        i++;
        skipWs();
        const valueStart = i;
        parseValue(path + '.' + key);
        const valueEnd = spans.get(path + '.' + key).end;
        list.push({ key, keyStart, valueStart, valueEnd });
        skipWs();
        if (text[i] === ',') { i++; continue; }
        if (text[i] === '}') { i++; break; }
        throw new Error('expected "," or "}" @' + i);
      }
      spans.set(path, { start, end: i, kind: 'object' });
      members.set(path, list);
      return;
    }
    if (c === '[') {
      i++; skipWs();
      let idx = 0;
      if (text[i] === ']') { i++; spans.set(path, { start, end: i, kind: 'array' }); return; }
      while (true) {
        skipWs();
        parseValue(path + '[' + idx + ']');
        idx++;
        skipWs();
        if (text[i] === ',') { i++; continue; }
        if (text[i] === ']') { i++; break; }
        throw new Error('expected "," or "]" @' + i);
      }
      spans.set(path, { start, end: i, kind: 'array' });
      return;
    }
    if (c === '"') { parseString(); spans.set(path, { start, end: i, kind: 'string' }); return; }
    const m = /^(true|false|null|-?\d+(?:\.\d+)?(?:[eE][+-]?\d+)?)/.exec(text.slice(i));
    if (!m) throw new Error('bad value @' + i + ': ' + JSON.stringify(text.slice(i, i + 20)));
    i += m[1].length;
    spans.set(path, { start, end: i, kind: 'literal' });
  }
  parseValue('$');
  return { spans, members };
}

const indentOfLine = (text, pos) => (/^[ \t]*/.exec(text.slice(text.lastIndexOf('\n', pos - 1) + 1)))[0];
const valText = (v) => (typeof v === 'string' ? JSON.stringify(v) : String(v));

const reports = [];
let totalPatches = 0;

for (const pair of PAIRS) {
  const fcpRaw = fs.readFileSync(DIR + pair.fcp, 'utf8');
  const drRaw = fs.readFileSync(DIR + pair.dr, 'utf8');
  const F = JSON.parse(fcpRaw);
  const R = JSON.parse(drRaw);
  const fspans = parseWithSpans(fcpRaw);
  const dspans = parseWithSpans(drRaw);

  const edits = [];   // {start, end, text, label}
  const log = [];
  const addReplace = (path, value, label, srcPath) => {
    const sp = dspans.spans.get(path);
    if (!sp) throw new Error('找不到目标路径 ' + path);
    edits.push({ start: sp.start, end: sp.end, text: valText(value), label });
    log.push(`${label}: ${drRaw.slice(sp.start, sp.end)} → ${valText(value)}`);
    totalPatches++;
  };
  const addInsert = (objPath, key, value, afterKey, label) => {
    const list = dspans.members.get(objPath);
    if (!list) throw new Error('找不到目标对象 ' + objPath);
    const anchor = list.find((m) => m.key === afterKey) ?? list[list.length - 1];
    const indent = indentOfLine(drRaw, anchor.keyStart);
    edits.push({ start: anchor.valueEnd, end: anchor.valueEnd, text: `,\n${indent}${JSON.stringify(key)}: ${valText(value)}`, label });
    log.push(`${label}: (新增) → ${valText(value)}`);
    totalPatches++;
  };

  // 1) 血量
  addReplace('$.MaxHealth', F.MaxHealth, 'MaxHealth');
  // 2) 抗性整表覆盖（沿用 fcp 原文格式）
  {
    const fsp = fspans.spans.get('$.DamageModifiers');
    const dsp = dspans.spans.get('$.DamageModifiers');
    edits.push({ start: dsp.start, end: dsp.end, text: fcpRaw.slice(fsp.start, fsp.end), label: 'DamageModifiers' });
    log.push(`DamageModifiers: ${R.DamageModifiers.length} 条 → ${F.DamageModifiers.length} 条（整表覆盖）`);
    totalPatches++;
  }
  // 3) 车毁爆炸：只对齐伤害与半径
  addReplace('$.DestroyInfo.ExplosionDamage', F.DestroyInfo.ExplosionDamage, 'DestroyInfo.ExplosionDamage');
  addReplace('$.DestroyInfo.ExplosionRadius', F.DestroyInfo.ExplosionRadius, 'DestroyInfo.ExplosionRadius');

  // 4) 武器数值
  for (const [fw, rw] of pair.weapons) {
    const a = F.Weapons[fw], b = R.Weapons[rw];
    if (!a || !b) { log.push(`!! 跳过 ${fw}→${rw}（缺失）`); continue; }
    for (const k of WEAPON_FIELDS) {
      if (a[k] === undefined) continue;
      const p = `$.Weapons.${rw}.${k}`;
      if (dspans.spans.has(p)) addReplace(p, a[k], `Weapons.${rw}.${k}`);
      else addInsert(`$.Weapons.${rw}`, k, a[k], 'Damage', `Weapons.${rw}.${k}`);
    }
    // 弹种 override（按 Ammo ID 配对；dr 独有条目如 LAHAT 保持原样）
    const norm = (arr) => (Array.isArray(arr) ? arr : (typeof arr === 'string' && arr ? [arr] : []))
      .map((e) => (typeof e === 'string' ? { Ammo: e } : e));
    const A = norm(a.AmmoType), B = norm(b.AmmoType);
    for (let bi = 0; bi < B.length; bi++) {
      const over = B[bi].Override;
      if (!over) continue;
      const match = A.find((x) => x.Ammo === B[bi].Ammo && x.Override);
      if (!match) { log.push(`弹种 ${B[bi].Ammo}: fcp 无对应，保持 dr 原值`); continue; }
      for (const k of AMMO_FIELDS) {
        if (match.Override[k] === undefined) continue;
        const p = `$.Weapons.${rw}.AmmoType[${bi}].Override.${k}`;
        if (dspans.spans.has(p)) addReplace(p, match.Override[k], `Weapons.${rw}.弹种[${bi}].${k}`);
        else addInsert(`$.Weapons.${rw}.AmmoType[${bi}].Override`, k, match.Override[k], 'Damage', `Weapons.${rw}.弹种[${bi}].${k}`);
      }
    }
  }

  // 5) 反向应用（从后往前，避免位移）
  edits.sort((x, y) => y.start - x.start);
  let out = drRaw;
  for (const e of edits) out = out.slice(0, e.start) + e.text + out.slice(e.end);

  // 6) 校验：解析 + 目标值比对 + 非白名单字段零改动
  const parsed = JSON.parse(out);
  const okValues = [];
  const check = (label, got, want) => { if (JSON.stringify(got) !== JSON.stringify(want)) okValues.push(`${label}: 期望 ${JSON.stringify(want)} 实得 ${JSON.stringify(got)}`); };
  check('MaxHealth', parsed.MaxHealth, F.MaxHealth);
  check('DamageModifiers', parsed.DamageModifiers, F.DamageModifiers);
  check('DestroyInfo.ExplosionDamage', parsed.DestroyInfo.ExplosionDamage, F.DestroyInfo.ExplosionDamage);
  check('DestroyInfo.ExplosionRadius', parsed.DestroyInfo.ExplosionRadius, F.DestroyInfo.ExplosionRadius);
  for (const [fw, rw] of pair.weapons) {
    const a = F.Weapons[fw], b = parsed.Weapons[rw];
    if (!a || !b) continue;
    for (const k of WEAPON_FIELDS) if (a[k] !== undefined) check(`Weapons.${rw}.${k}`, b[k], a[k]);
  }

  // 非白名单字段应完全不变
  const flat = (o, p = '') => {
    const out = {};
    if (o === null || typeof o !== 'object') { out[p] = o; return out; }
    if (Array.isArray(o)) { o.forEach((v, i) => Object.assign(out, flat(v, `${p}[${i}]`))); return out; }
    for (const [k, v] of Object.entries(o)) Object.assign(out, flat(v, p ? `${p}.${k}` : k));
    return out;
  };
  const before = flat(R), after = flat(parsed);
  const allowed = (path) => {
    if (path === 'MaxHealth' || path === 'DamageModifiers' || path.startsWith('DamageModifiers[')) return true;
    if (path === 'DestroyInfo.ExplosionDamage' || path === 'DestroyInfo.ExplosionRadius') return true;
    for (const [, rw] of pair.weapons) {
      if (path.startsWith(`Weapons.${rw}.`)) {
        const tail = path.slice(`Weapons.${rw}.`.length);
        const field = tail.split('.')[0].split('[')[0];
        if (WEAPON_FIELDS.includes(field)) return true;
        if (/^AmmoType\[\d+\]\.Override\./.test(tail)) {
          const f = tail.replace(/^AmmoType\[\d+\]\.Override\./, '');
          if (AMMO_FIELDS.includes(f)) return true;
        }
      }
    }
    return false;
  };
  const unexpected = [];
  for (const k of new Set([...Object.keys(before), ...Object.keys(after)])) {
    if (JSON.stringify(before[k]) !== JSON.stringify(after[k]) && !allowed(k)) unexpected.push(`${k}: ${JSON.stringify(before[k])} → ${JSON.stringify(after[k])}`);
  }

  const linesBefore = drRaw.split('\n').length, linesAfter = out.split('\n').length;
  reports.push({ file: pair.dr, log, okValues, unexpected, linesBefore, linesAfter, charsBefore: drRaw.length, charsAfter: out.length });

  if (WRITE) { fs.mkdirSync(OUT, { recursive: true }); fs.writeFileSync(OUT + pair.dr.split('/').pop(), out, 'utf8'); }
}

for (const r of reports) {
  console.log(`\n████ ${r.file}   行 ${r.linesBefore}→${r.linesAfter}  字符 ${r.charsBefore}→${r.charsAfter}  改动 ${r.log.length} 处`);
  for (const l of r.log) console.log('   · ' + l);
  console.log(`   值校验: ${r.okValues.length ? '失败 ' + r.okValues.join('; ') : '全部符合 fcp'}`);
  console.log(`   白名单外改动: ${r.unexpected.length ? '发现 ' + r.unexpected.length + ' 处!! ' + r.unexpected.slice(0, 5).join(' | ') : '无（安全）'}`);
}
console.log(`\n合计改动 ${totalPatches} 处 → ${WRITE ? '已写出 ' + OUT : '（演练，未写盘）'}`);
