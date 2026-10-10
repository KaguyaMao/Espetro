// Apply new shell-resistance profiles (A组 / B组) + set all HE shell direct damage to 5.
// Strategy: minimal text surgery on the raw json (no reformatting), originals kept.
import fs from 'node:fs';
import path from 'node:path';

const TMP = 'D:/minecraft/modp/Espetro/build/tmp';
const SRC = path.join(TMP, 'srv-vehicles-fresh');
const OUT = path.join(TMP, 'srv-vehicles-new');
const BAK = path.join(TMP, 'srv-vehicles-before-shelledit');
fs.mkdirSync(OUT, { recursive: true });
fs.mkdirSync(BAK, { recursive: true });

const GROUP_A = ['dragonrise_reforge:bmp3', 'dragonrise_reforge:csk181', 'dragonrise_reforge:m1126', 'dragonrise_reforge:m1128',
  'dragonrise_reforge:m113', 'dragonrise_reforge:m1296', 'dragonrise_reforge:m3a3', 'dragonrise_reforge:zbd04a',
  'dragonrise_reforge:zbd05', 'dragonrise_reforge:zbl08', 'dragonrise_reforge:zlt11', 'dragonrise_reforge:zsl10',
  'dragonrise_reforge:ztd05', 'fcp:bmp1am', 'fcp:bmp2', 'fcp:bmp2d', 'fcp:bmp2m', 'fcp:btr80', 'fcp:btr82',
  'fcp:gaz_tigr_gl', 'fcp:gaz_tigr_mg', 'fcp:gaz_tigr_rws', 'fcp:matv', 'fcp:matv_9in1', 'fcp:matv_crow',
  'fcp:matv_tow', 'fcp:stryker_dragoon', 'fcp:stryker_m2', 'fcp:stryker_mgs', 'fcp:stryker_mortar'];
const GROUP_B = ['dragonrise_reforge:m1a2sepv1', 'dragonrise_reforge:m1a2sepv2', 'dragonrise_reforge:t72b3',
  'dragonrise_reforge:t90mh', 'dragonrise_reforge:ztz96a', 'dragonrise_reforge:ztz99a'];

const lines = t => t.split('\n');
const findLine = (arr, trimmed) => arr.findIndex(l => l.trim() === trimmed);

function insertAround(text, anchor, before, after) {
  const arr = lines(text);
  const i = findLine(arr, anchor);
  if (i < 0) throw new Error(`anchor not found: ${anchor}`);
  const indent = arr[i].match(/^\s*/)[0];
  const mk = list => list.map(s => indent + JSON.stringify(s) + ',');
  const out = [...arr.slice(0, i), ...(before ? mk(before) : []), arr[i], ...(after ? mk(after) : []), ...arr.slice(i + 1)];
  return out.join('\n');
}
function replaceLine(text, anchor, replacement) {
  const arr = lines(text);
  const i = findLine(arr, anchor);
  if (i < 0) throw new Error(`anchor not found: ${anchor}`);
  const indent = arr[i].match(/^\s*/)[0];
  arr[i] = indent + JSON.stringify(replacement) + (arr[i].trimEnd().endsWith(',') ? ',' : '');
  return arr.join('\n');
}
function deleteLine(text, anchor) {
  const arr = lines(text);
  const i = findLine(arr, anchor);
  if (i < 0) throw new Error(`anchor not found: ${anchor}`);
  arr.splice(i, 1);
  return arr.join('\n');
}
// replace the value of "Damage" that sits at depth 1 inside the object starting at index `start` (index of '{')
function setDamageInObject(text, start, value) {
  let depth = 0, i = start;
  const keyRe = /"Damage"\s*:\s*(-?[\d.]+)/g;
  const bodyStart = i + 1;
  // find matching close brace
  let end = -1;
  for (let k = i; k < text.length; k++) {
    const c = text[k];
    if (c === '{') depth++;
    else if (c === '}') { depth--; if (depth === 0) { end = k; break; } }
  }
  if (end < 0) throw new Error('unbalanced braces');
  // scan depth-1 tokens for Damage
  depth = 0;
  for (let k = i; k < end; k++) {
    const c = text[k];
    if (c === '{' || c === '[') depth++;
    else if (c === '}' || c === ']') depth--;
    else if (depth === 1 && text.startsWith('"Damage"', k)) {
      keyRe.lastIndex = k;
      const m = keyRe.exec(text);
      if (m && m.index === k) return text.slice(0, k) + `"Damage": ${value}` + text.slice(k + m[0].length);
    }
  }
  // not found -> insert right after opening brace
  const inner = text.slice(bodyStart, bodyStart + 2);
  const nl = text.includes('\r\n') ? '\r\n' : '\n';
  return text.slice(0, bodyStart) + `${nl}            "Damage": ${value},` + text.slice(bodyStart);
}
function findEnclosingObject(text, pos) {
  let depth = 0;
  for (let k = pos; k >= 0; k--) {
    const c = text[k];
    if (c === '}') depth++;
    else if (c === '{') { if (depth === 0) return k; depth--; }
  }
  return -1;
}
function setHeDamage(text, heId, value) {
  let count = 0;
  let searchFrom = 0;
  for (;;) {
    const idx = text.indexOf(`"${heId}"`, searchFrom);
    if (idx < 0) break;
    searchFrom = idx + heId.length + 2;
    const before = text.slice(Math.max(0, idx - 20), idx);
    if (/"Ammo"\s*:\s*$/.test(before)) {
      // case b: value of "Ammo" -> find enclosing object, then its "Override" object
      const objStart = findEnclosingObject(text, idx);
      if (objStart < 0) throw new Error('no enclosing object for ' + heId);
      const oIdx = text.indexOf('"Override"', objStart);
      if (oIdx < 0) throw new Error('no Override for ' + heId);
      const braceIdx = text.indexOf('{', oIdx);
      text = setDamageInObject(text, braceIdx, value);
      count++;
    } else {
      // case a: plain entry in AmmoType -> expand to object with Override
      text = text.slice(0, idx) + `{"Ammo": "${heId}", "Override": {"Damage": ${value}}}` + text.slice(idx + heId.length + 2);
      count++;
    }
  }
  return { text, count };
}

const log = [];
const HE_IDS = ['superbwarfare:large_shell_he', 'superbwarfare:small_shell_he'];
for (const f of fs.readdirSync(SRC).filter(x => x.endsWith('.json') && x !== 'manifest.json')) {
  const raw = fs.readFileSync(path.join(SRC, f), 'utf8');
  let j; try { j = JSON.parse(raw); } catch { log.push(`${f}: !! json parse failed`); continue; }
  const id = j.ID;
  let text = raw;
  const notes = [];
  if (GROUP_A.includes(id)) {
    text = insertAround(text, '"All - 13",', ['@superbwarfare:small_cannon_shell + 13', '@superbwarfare:cannon_shell + 13'], null);
    text = insertAround(text, '"All * 0.2",', null, ['@superbwarfare:small_cannon_shell * 5', '@superbwarfare:cannon_shell * 5']);
    text = insertAround(text, '"superbwarfare:projectile_hit * 1.35",', null,
      ['@superbwarfare:small_cannon_shell * 0.0740740741', '@superbwarfare:cannon_shell * 0.3703703704']);
    text = replaceLine(text, '"superbwarfare:custom_explosion * 2",', 'superbwarfare:custom_explosion * 0.54');
    notes.push('A组');
  } else if (GROUP_B.includes(id)) {
    text = insertAround(text, '"All - 20",', ['@superbwarfare:small_cannon_shell + 20', '@superbwarfare:cannon_shell + 20'], null);
    text = insertAround(text, '"All * 0.2",', null, ['@superbwarfare:small_cannon_shell * 5', '@superbwarfare:cannon_shell * 5']);
    text = insertAround(text, '"superbwarfare:projectile_hit * 1.3",', null,
      ['@superbwarfare:small_cannon_shell * 0.0769230769', '@superbwarfare:cannon_shell * 0.2307692308']);
    text = replaceLine(text, '"superbwarfare:custom_explosion * 0.65",', 'superbwarfare:custom_explosion * 0.0433333333');
    text = deleteLine(text, '"@superbwarfare:small_cannon_shell * 0.7",');
    notes.push('B组');
  }
  let heCount = 0;
  for (const heId of HE_IDS) {
    const r = setHeDamage(text, heId, 5);
    text = r.text; heCount += r.count;
  }
  if (heCount) notes.push(`HE直击→5 ×${heCount}`);
  // validate
  let ok = true, err = '';
  try { JSON.parse(text); } catch (e) { ok = false; err = e.message; }
  if (!ok) { log.push(`${f} (${id}): !! EDITED JSON INVALID: ${err}`); continue; }
  fs.writeFileSync(path.join(BAK, f), raw);
  fs.writeFileSync(path.join(OUT, f), text);
  if (notes.length) log.push(`${f} (${id}): ${notes.join(' ; ')}`);
}
console.log(log.join('\n'));
console.log(`\n写出行数: ${log.length}, 输出目录: ${OUT}`);
