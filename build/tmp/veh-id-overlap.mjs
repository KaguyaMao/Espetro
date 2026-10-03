// veh-id-overlap.mjs — 检查 kubejs 覆盖的载具 ID 是否在模组 jar 内置数据里也有定义，以及是否自带角度条目
import fs from 'node:fs';
import path from 'node:path';

const KUBE = 'veh-angle/before';
const JARS = ['modjar-veh/dr', 'modjar-veh/fcp'];

function lenient(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    out += c;
  }
  try { return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1')); } catch { return null; }
}

// 收集 jar 内置定义
const jarById = new Map();
function walk(dir) {
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name);
    if (e.isDirectory()) { walk(p); continue; }
    if (!e.name.endsWith('.json') || !p.includes('sbw')) continue;
    const j = lenient(fs.readFileSync(p, 'utf8'));
    const id = j?.ID;
    if (!id) continue;
    const list = jarById.get(id) ?? [];
    list.push({ file: p, mods: j.DamageModifiers, len: (j.DamageModifiers || []).length });
    jarById.set(id, list);
  }
}
for (const d of JARS) if (fs.existsSync(d)) walk(d);

const kubeFiles = fs.readdirSync(KUBE).filter(f => f.endsWith('.json') && f !== 'manifest.json');
let overlap = 0, withAngle = [], withoutAngle = [];
for (const f of kubeFiles.sort()) {
  const j = lenient(fs.readFileSync(path.join(KUBE, f), 'utf8'));
  const id = j.ID;
  const defs = jarById.get(id);
  if (!defs) { console.log(`[仅 kubejs] ${id}`); continue; }
  overlap++;
  const anyAngle = defs.some(d => (d.mods || []).some(m => typeof m === 'string' && m.includes('getSourceAngle')));
  const info = defs.map(d => `${d.file.replace(/\\/g, '/')}(${d.len} 条)`).join(' | ');
  if (anyAngle) withAngle.push(`${id} ← ${info}`); else withoutAngle.push(`${id} ← ${info}`);
}
console.log(`\n重叠 ID: ${overlap}/${kubeFiles.length}`);
console.log(`\n内置定义里【已带】角度条目的 (${withAngle.length}):`);
withAngle.forEach(x => console.log('  ' + x));
console.log(`\n内置定义里【不带】角度条目的 (${withoutAngle.length}):`);
withoutAngle.forEach(x => console.log('  ' + x));
