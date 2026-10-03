import fs from 'node:fs';
import path from 'node:path';
// 检查：JSON.parse→stringify(2 空格) 是否与原文逐字节一致（决定能否安全重写）
const dirs = process.argv.slice(2);
for (const d of dirs) {
  let same = 0, diff = [];
  for (const f of fs.readdirSync(d).filter(x => x.endsWith('.json'))) {
    const t = fs.readFileSync(path.join(d, f), 'utf8');
    const out = JSON.stringify(JSON.parse(t), null, 2);
    if (out === t || out + '\n' === t || out === t.replace(/\r\n/g, '\n') || out + '\n' === t.replace(/\r\n/g, '\n')) same++;
    else diff.push(f);
  }
  console.log(`${d}\n  一致=${same}  不一致=${diff.length}${diff.length ? ' → ' + diff.join(', ') : ''}`);
}
