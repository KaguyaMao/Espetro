// show-raw.mjs — 打印文件指定关键字段附近的原始文本（保留格式）
import fs from 'fs';

const file = process.argv[2];
const keys = process.argv.slice(3);
const raw = fs.readFileSync(file, 'utf8');
const lines = raw.split('\n');
for (const k of keys) {
  console.log(`\n──── 含 "${k}" 的行:`);
  lines.forEach((l, i) => {
    if (l.includes(k)) console.log(`${String(i + 1).padStart(4)}: ${l}`);
  });
}
console.log(`\n文件共 ${lines.length} 行, ${raw.length} 字符`);
