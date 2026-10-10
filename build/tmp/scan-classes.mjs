// scan-classes.mjs — 在 .class 字节码里搜字符串常量（constant pool 为 UTF8 明文）
// 用法: node scan-classes.mjs <目录> <关键词1> [关键词2 ...]
import fs from 'node:fs';
import path from 'node:path';

const root = process.argv[2];
const keys = process.argv.slice(3);
if (!root || keys.length === 0) { console.log('usage: node scan-classes.mjs <dir> <kw...>'); process.exit(1); }

const files = [];
(function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name);
    if (e.isDirectory()) walk(p);
    else if (e.name.endsWith('.class')) files.push(p);
  }
})(root);

const hits = new Map();
for (const f of files) {
  const buf = fs.readFileSync(f);
  const found = [];
  for (const k of keys) if (buf.includes(Buffer.from(k, 'utf8'))) found.push(k);
  if (found.length) hits.set(f, found);
}
for (const [f, found] of [...hits].sort()) {
  console.log(`${found.join(',')}  ${f.replace(root, '').replace(/\\/g, '/')}`);
}
console.log(`\n扫描 ${files.length} 个 class，命中 ${hits.size} 个`);
