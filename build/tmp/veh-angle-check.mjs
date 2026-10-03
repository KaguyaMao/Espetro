// veh-angle-check.mjs — 检查每个载具 JSON 的 DamageModifiers 状态
import fs from 'node:fs';
import path from 'node:path';

const dir = process.argv[2] ?? 'veh-angle/before';
function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}

let noMods = [], hasAngle = [], ok = 0, styles = new Set();
for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json') && x !== 'manifest.json').sort()) {
  const text = fs.readFileSync(path.join(dir, f), 'utf8');
  let j;
  try { j = lenientParse(text); } catch (e) { console.log(`解析失败 ${f}: ${e.message}`); continue; }
  const has = Array.isArray(j.DamageModifiers);
  const angle = has && j.DamageModifiers.some(x => typeof x === 'string' && x.includes('getSourceAngle'));
  if (angle) hasAngle.push(f);
  if (!has) noMods.push(`${f} (${j.ID})`);
  else {
    ok++;
    // 记录缩进风格
    const m = text.match(/\n(\s*)"(?:All|@|#|minecraft|\$)/);
    if (m) styles.add(m[1].length);
  }
}
console.log(`有 DamageModifiers: ${ok} / 已含 getSourceAngle: ${hasAngle.length}`);
if (hasAngle.length) console.log('  已含:', hasAngle.join(', '));
if (noMods.length) console.log('无 DamageModifiers:\n  ' + noMods.join('\n  '));
console.log('条目缩进宽度候选:', [...styles].join(', '));
