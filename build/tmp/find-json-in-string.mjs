import fs from 'node:fs';
import path from 'node:path';

// 扫描目录：找出"本应是数组/对象、却被写成字符串"的字段（尤以 AmmoType 为主），会触发 SBW 的 invalid item id 刷屏
const dirs = process.argv.slice(2);
let hits = 0;

function scan(node, p, file, out) {
  if (node === null || typeof node !== 'object') {
    if (typeof node === 'string' && /^\s*[\[{]/.test(node)) {
      out.push(`${p} = ${JSON.stringify(node).slice(0, 90)}`);
    }
    return;
  }
  if (Array.isArray(node)) { node.forEach((v, i) => scan(v, `${p}[${i}]`, file, out)); return; }
  for (const k of Object.keys(node)) scan(node[k], p ? `${p}.${k}` : k, file, out);
}

for (const dir of dirs) {
  if (!fs.existsSync(dir)) { console.log(`═══ ${dir}（不存在）`); continue; }
  console.log(`══════ ${dir}`);
  for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json'))) {
    const j = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf8'));
    const out = [];
    // 重点：Weapons 段里被写成字符串的字段
    for (const [wn, wd] of Object.entries(j.Weapons || {})) {
      for (const [k, v] of Object.entries(wd || {})) {
        if (typeof v === 'string' && /^\s*[\[{]/.test(v)) out.push(`Weapons.${wn}.${k} = ${JSON.stringify(v).slice(0, 120)}`);
      }
    }
    const other = [];
    scan(j, '', f, other);
    const all = [...new Set([...out, ...other.filter(x => !out.includes(x))])];
    if (all.length) { hits += all.length; console.log(`  —— ${f}`); for (const l of all) console.log(`       ${l}`); }
  }
}
console.log(`\n共 ${hits} 处"JSON 文本被塞进字符串"的字段`);
