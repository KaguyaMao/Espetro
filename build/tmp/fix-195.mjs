// fix-195.mjs — 最小化文本修复 pla_195th.json 的两处逗号错误（保留原格式与全部改动）
// 用法: node fix-195.mjs <输入文件> <输出文件>
import fs from 'fs';

const [src, dst] = process.argv.slice(2);
const before = fs.readFileSync(src, 'utf8');
const beforeLines = before.split('\n');
let text = before;
const changes = [];

// 修复 1：数组/对象末尾的多余逗号
const trailingComma = /,(\s*\n\s*[\]}])/g;
let m;
const trailingHits = [];
while ((m = trailingComma.exec(text)) !== null) {
  const line = text.slice(0, m.index).split('\n').length;
  trailingHits.push(line);
}
if (trailingHits.length) {
  text = text.replace(trailingComma, '$1');
  changes.push(`删除末尾多余逗号（第 ${trailingHits.join(', ')} 行）`);
}

// 修复 2：对象之间缺失的逗号（'}' 或 ']' 后面直接跟 "key":）
const missingComma = /([}\]][ \t]*\n[ \t]*")/g;
const missingHits = [];
while ((m = missingComma.exec(text)) !== null) {
  const line = text.slice(0, m.index).split('\n').length;
  missingHits.push(line);
}
if (missingHits.length) {
  text = text.replace(missingComma, (s, p1) => p1.replace(/^([}\]])/, '$1,'));
  changes.push(`补齐缺失逗号（第 ${missingHits.join(', ')} 行末尾）`);
}

let j;
try {
  j = JSON.parse(text);
} catch (e) {
  console.log('修复后仍无法解析: ' + e.message);
  process.exit(1);
}

const afterLines = text.split('\n');
const diffLines = [];
for (let i = 0; i < Math.max(beforeLines.length, afterLines.length); i++) {
  if (beforeLines[i] !== afterLines[i]) diffLines.push({ n: i + 1, before: beforeLines[i], after: afterLines[i] });
}

console.log('修复动作:');
for (const c of changes) console.log('  · ' + c);
console.log(`\n实际改动的行（共 ${diffLines.length} 行）:`);
for (const d of diffLines) {
  console.log(`  第 ${d.n} 行`);
  console.log(`    - ${(d.before ?? '').trim()}`);
  if (d.after !== undefined) console.log(`    + ${d.after.trim()}`);
}
console.log(`\n行数: ${beforeLines.length} → ${afterLines.length}   JSON 解析: OK（${text.length} 字符）`);
console.log(`编制: ${j.faction?.name}  职业=${Object.keys(j.classes ?? {}).length}  VehTypes=${JSON.stringify(j.VehTypes)}  载具=${JSON.stringify(Object.keys(j.vehicles ?? {}))}`);
if (dst) {
  fs.writeFileSync(dst, text, 'utf8');
  console.log('已写出 ' + dst);
}
