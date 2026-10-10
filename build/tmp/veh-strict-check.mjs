// veh-strict-check.mjs — 严格校验载具 JSON（找出损坏/截断/OBB 异常的文件）
import fs from 'node:fs';
import path from 'node:path';

const DIRS = ['veh-angle/before', 'veh-angle/after'];
for (const dir of DIRS) {
  console.log(`\n===== ${dir} =====`);
  for (const f of fs.readdirSync(dir).filter(x => x.endsWith('.json') && x !== 'manifest.json').sort()) {
    const text = fs.readFileSync(path.join(dir, f), 'utf8');
    let j = null, err = null;
    try { j = JSON.parse(text); } catch (e) { err = e.message; }
    if (err) {
      const lines = text.split('\n');
      console.log(`!! ${f} 严格解析失败: ${err}  (行数=${lines.length}, 字节=${text.length})`);
      console.log(`   末 3 行: ${JSON.stringify(lines.slice(-3))}`);
      continue;
    }
    const obb = j.OBB;
    const bad = [];
    if (Array.isArray(obb)) {
      obb.forEach((o, i) => { if (!o || typeof o !== 'object') bad.push(i); });
      if (obb.length === 0) bad.push('empty');
    } else if (obb !== undefined) bad.push('not-array');
    const rates = Array.isArray(obb) ? obb.filter(o => o && o.DamageRate !== undefined).length : 0;
    if (bad.length) console.log(`?? ${f} OBB 异常: ${JSON.stringify(bad)}`);
    else if (rates) console.log(`   ${f}: OBB=${obb.length} 带 DamageRate=${rates} → ${JSON.stringify(obb.map(o => o.DamageRate))}`);
  }
}
