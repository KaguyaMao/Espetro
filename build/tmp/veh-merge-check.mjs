// veh-merge-check.mjs — 对比 jar 内置定义与 kubejs 覆盖定义（HP/抗性条数/炮弹倍率/角度条目）
import fs from 'node:fs';
import path from 'node:path';

function lenient(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}
function parseEntry(raw) {
  const s = String(raw).trim();
  const sc = s.match(/^\$entity\.getSourceAngle\(source,\s*([\d.]+)\)/);
  if (sc) return { script: Number(sc[1]) };
  const m = s.match(/^(\S+)\s*([*+\-/])\s*(-?[\d.]+)$/);
  if (!m) return null;
  const [, source, op, num] = m; const v = Number(num);
  return { source, op, value: op === '*' ? v : (op === '+' ? v : -v) };
}
function multFor(mods, proj) {
  let a = 100;
  for (const raw of (mods || [])) {
    const e = parseEntry(raw);
    if (!e || e.script !== undefined) continue;
    const src = e.source;
    const match = src === 'All' || src === `@${proj}` || src === 'superbwarfare:projectile_hit' || src === `superbwarfare:projectile_hit`;
    if (!match) continue;
    a = e.op === '*' ? a * e.value : Math.max(a + e.value, 0);
  }
  return a / 100;
}
const JAR = { dragonrise: 'modjar-veh/dr/data/dragonrise_reforge/sbw/vehicles', fcp: 'modjar-veh/fcp/data/fcp/sbw/vehicles' };
for (const f of fs.readdirSync('veh-angle/before').filter(x => x.endsWith('.json') && x !== 'manifest.json').sort()) {
  const ns = f.startsWith('fcp__') ? 'fcp' : 'dragonrise';
  const short = f.replace(/^(fcp|dragonrise)__/, '');
  const jarPath = path.join(JAR[ns], short);
  if (!fs.existsSync(jarPath)) continue;
  const jar = lenient(fs.readFileSync(jarPath, 'utf8'));
  const kube = lenient(fs.readFileSync(path.join('veh-angle/before', f), 'utf8'));
  const ja = (jar.DamageModifiers || []).filter(x => String(x).includes('getSourceAngle'));
  const ka = (kube.DamageModifiers || []).filter(x => String(x).includes('getSourceAngle'));
  console.log(`${kube.ID}`);
  console.log(`   jar   : HP=${jar.MaxHealth} 条数=${(jar.DamageModifiers || []).length} 主炮倍率=${multFor(jar.DamageModifiers, 'superbwarfare:cannon_shell').toFixed(3)} 机炮倍率=${multFor(jar.DamageModifiers, 'superbwarfare:small_cannon_shell').toFixed(3)} 角度=${ja.length ? ja.join(',') : '无'}`);
  console.log(`   kubejs: HP=${kube.MaxHealth} 条数=${(kube.DamageModifiers || []).length} 主炮倍率=${multFor(kube.DamageModifiers, 'superbwarfare:cannon_shell').toFixed(3)} 机炮倍率=${multFor(kube.DamageModifiers, 'superbwarfare:small_cannon_shell').toFixed(3)} 角度=${ka.length ? ka.join(',') : '无'}`);
}
