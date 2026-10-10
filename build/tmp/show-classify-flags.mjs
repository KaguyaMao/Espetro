// show-classify-flags.mjs — 打印出场载具覆盖文件的严格/宽松解析标志
import fs from 'node:fs';
const rows = JSON.parse(fs.readFileSync('inplay-veh-classify.json', 'utf8'));
console.log('id | 覆盖严格可解析 | 覆盖含尾随逗号 | jar严格 | 覆盖条目数 | jar条目数');
for (const r of rows.sort((a, b) => a.id.localeCompare(b.id))) {
  console.log(`${r.id} | ${r.kjsStrict === undefined ? '-' : r.kjsStrict} | ${r.kjsStrict === false ? '有' : ''} | ${r.jarStrict} | ${r.kjsMods ?? '-'} | ${r.jarMods ?? '-'}`);
}
const bad = rows.filter((r) => r.kjsStrict === false);
console.log(`\n覆盖文件无法严格解析的出场载具 (${bad.length}): ${bad.map((r) => r.id).join(', ')}`);
