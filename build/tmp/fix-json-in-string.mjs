import fs from 'node:fs';
import path from 'node:path';

/**
 * fix-json-in-string.mjs — 把"被写成字符串的 JSON"还原成真正的 JSON（修 SBW 的 invalid item id 刷屏）
 *
 * 目标字段由 --keys 指定（默认 AmmoType），会处理 Weapons.*.<key>（也支持任意同名键）。
 * 用法: node fix-json-in-string.mjs [--keys=AmmoType] [--apply] <文件...>
 */
const args = process.argv.slice(2);
let apply = false, keys = ['AmmoType'];
const files = [];
for (const a of args) {
  if (a.startsWith('--keys=')) keys = a.slice(7).split(',').filter(Boolean);
  else if (a === '--apply') apply = true;
  else files.push(a);
}

/** 从 raw 的 pos 处（指向字符串起始引号）找到字符串字面量结束位置（返回含引号的 [start,end) ） */
function stringLiteralEnd(raw, start) {
  if (raw[start] !== '"') return -1;
  for (let i = start + 1; i < raw.length; i++) {
    const c = raw[i];
    if (c === '\\') { i++; continue; }
    if (c === '"') return i + 1;
  }
  return -1;
}

let total = 0;
for (const f of files) {
  let raw = fs.readFileSync(f, 'utf8');
  const before = JSON.parse(raw);
  const changes = [];

  for (const key of keys) {
    let searchFrom = 0;
    // 反复查找 "key" : "<json 文本>"
    for (;;) {
      const re = new RegExp(`"${key}"\\s*:\\s*"`, 'g');
      re.lastIndex = searchFrom;
      const m = re.exec(raw);
      if (!m) break;
      const quoteStart = m.index + m[0].length - 1;
      const quoteEnd = stringLiteralEnd(raw, quoteStart);
      if (quoteEnd < 0) break;
      const inner = raw.slice(quoteStart + 1, quoteEnd - 1);
      let decoded = null, parsed = null;
      try { decoded = JSON.parse('"' + inner + '"'); } catch { /* 不是合法转义 → 跳过 */ }
      if (decoded && /^\s*[[{]/.test(decoded)) {
        try { parsed = JSON.parse(decoded); } catch { parsed = null; }
      }
      if (parsed === null) { searchFrom = quoteEnd; continue; }

      // 计算缩进：取该字段所在行的行首空白 + 2 空格
      const lineStart = raw.lastIndexOf('\n', m.index) + 1;
      const indent = (raw.slice(lineStart, m.index).match(/^\s*/) || [''])[0];
      const pretty = JSON.stringify(parsed, null, 2).split('\n')
        .map((l, i) => (i === 0 ? l : indent + l)).join('\n');
      raw = raw.slice(0, quoteStart) + pretty + raw.slice(quoteEnd);
      changes.push(`${key}: 字符串(${inner.length} 字符) → ${Array.isArray(parsed) ? `数组(${parsed.length})` : '对象'}`);
      searchFrom = quoteStart + pretty.length;
      total++;
    }
  }

  if (!changes.length) { console.log(`  [无需修改] ${path.basename(f)}`); continue; }
  try { JSON.parse(raw); } catch (e) { console.log(`  [失败] ${path.basename(f)} 改写后非法: ${e.message}`); continue; }
  // 安全检查：除目标字段外其它字段必须不变
  const after = JSON.parse(raw);
  const strip = (o) => JSON.parse(JSON.stringify(o, (k, v) => (k === 'AmmoType' ? '<X>' : v)));
  const same = JSON.stringify(strip(before)) === JSON.stringify(strip(after));
  console.log(`  ${apply ? '[已写入]' : '[预演]'} ${path.basename(f)}  ${changes.join('; ')}  其它字段未变=${same}`);
  if (apply) fs.writeFileSync(f, raw, 'utf8');
}
console.log(`\n共处理 ${total} 处${apply ? '（已写入）' : '（预演）'}`);
