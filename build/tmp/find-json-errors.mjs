// find-json-errors.mjs — 逐个定位 JSON 语法错误（行号 + 上下文）
import fs from 'fs';

const path = process.argv[2];
const raw = fs.readFileSync(path, 'utf8');
const lines = raw.split('\n');

// 用逐字符扫描找到"应出现逗号却缺失"的位置
const problems = [];
for (let i = 0; i < lines.length - 1; i++) {
  const cur = lines[i];
  const next = lines[i + 1];
  const curTrim = cur.trim();
  const nextTrim = next.trim();
  // 上一行以 } 或 ] 结束，下一行以 "key": 开头 → 缺少逗号
  if ((curTrim.endsWith('}') || curTrim.endsWith(']')) && /^"/.test(nextTrim) && !curTrim.endsWith(',')) {
    problems.push(`第 ${i + 1}→${i + 2} 行：'${curTrim}' 后缺少逗号，下一行 '${nextTrim.slice(0, 60)}'`);
  }
  // 下一行以 ] 或 } 开头但上一行以 , 结尾 → 多余逗号
  if (curTrim.endsWith(',') && /^[\]}]/.test(nextTrim)) {
    problems.push(`第 ${i + 1}→${i + 2} 行：'${curTrim}' 后多余逗号`);
  }
  // 行内出现空字符串键 "" 
  if (/^\s*""\s*:/.test(next)) problems.push(`第 ${i + 2} 行：空字符串键 ""`);
}

console.log(`文件: ${path}  共 ${lines.length} 行`);
if (!problems.length) console.log('未发现常见的逗号/键名问题');
for (const p of problems) console.log('  ! ' + p);

// 打印 55-75 行供人工确认
console.log('\n--- 第 55-75 行原文:');
for (let i = 54; i < Math.min(75, lines.length); i++) console.log(String(i + 1).padStart(4) + ': ' + lines[i]);
