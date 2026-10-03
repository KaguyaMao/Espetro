// read-radio-def.mjs — 打印 fortifications.json 里 espetro:radio 定义的原文片段
import fs from 'node:fs';
const t = fs.readFileSync('fortifications-before.json', 'utf8');
const j = JSON.parse(t);
const idx = t.indexOf('espetro:radio');
console.log('=== espetro:radio 定义片段（原文）===');
console.log(t.slice(Math.max(0, idx - 120), idx + 1500));
console.log('\n=== 各定义 usable_by（解析值）===');
for (const d of j.fortifications) {
  console.log('  ' + String(d.id).padEnd(36) + JSON.stringify(d.requirements?.usable_by));
}
