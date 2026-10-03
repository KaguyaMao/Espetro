// veh-angle-apply.mjs — 给所有载具的 DamageModifiers 追加角度伤害条目
// 用法: node veh-angle-apply.mjs <beforeDir> <afterDir> [multiplier=0.35] [skipList=name,name]
import fs from 'node:fs';
import path from 'node:path';

const BEFORE = process.argv[2] ?? 'veh-angle/before';
const AFTER = process.argv[3] ?? 'veh-angle/after';
const MULT = process.argv[4] ?? '0.35';
const SKIP = new Set((process.argv[5] ?? 'dragonrise__sx1_a.json').split(',').filter(Boolean));
const ENTRY = `$entity.getSourceAngle(source, ${MULT}) * damage`;

fs.mkdirSync(AFTER, { recursive: true });

/** 找到 "DamageModifiers" 数组的 [..] 范围 */
function findArray(text, key) {
  const ki = text.indexOf(`"${key}"`);
  if (ki < 0) return null;
  const open = text.indexOf('[', ki);
  if (open < 0) return null;
  let depth = 0, inStr = false, esc = false;
  for (let i = open; i < text.length; i++) {
    const c = text[i];
    if (inStr) { if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; continue; }
    if (c === '[') depth++;
    else if (c === ']') { depth--; if (depth === 0) return { open, close: i }; }
  }
  return null;
}

function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1'));
}

const files = fs.readdirSync(BEFORE).filter(f => f.endsWith('.json') && f !== 'manifest.json').sort();
let done = 0, skipped = [], already = [], emptyArr = [];

for (const f of files) {
  const src = path.join(BEFORE, f);
  const text = fs.readFileSync(src, 'utf8');
  if (SKIP.has(f)) { skipped.push(f); continue; }
  if (text.includes('getSourceAngle')) { already.push(f); fs.writeFileSync(path.join(AFTER, f), text, 'utf8'); continue; }

  const range = findArray(text, 'DamageModifiers');
  if (!range) { skipped.push(`${f}(无字段)`); fs.writeFileSync(path.join(AFTER, f), text, 'utf8'); continue; }

  const inner = text.slice(range.open + 1, range.close);
  const isEmpty = inner.trim() === '';
  // 前一个条目的缩进 & 闭合括号缩进
  const beforeClose = text.slice(0, range.close);
  const lastNl = beforeClose.lastIndexOf('\n');
  const closingIndent = text.slice(lastNl + 1, range.close).match(/^[ \t]*/)[0];
  const entryIndentMatch = inner.match(/\n([ \t]+)"/g);
  const entryIndent = entryIndentMatch ? entryIndentMatch[entryIndentMatch.length - 1].slice(1, -1) : closingIndent + '  ';

  const head = text.slice(0, range.open + 1);
  const body = text.slice(range.open + 1, lastNl).replace(/\s+$/, '');
  const tail = text.slice(lastNl);
  const needComma = !isEmpty && !body.endsWith(',');
  const out = `${head}${body}${needComma ? ',' : ''}\n${entryIndent}"${ENTRY}"${tail}`;

  // 校验
  let a, b;
  try {
    a = lenientParse(text); b = lenientParse(out);
  } catch (e) { console.log(`!! 解析失败 ${f}: ${e.message}`); skipped.push(`${f}(解析失败)`); continue; }
  const dm = b.DamageModifiers;
  if (!Array.isArray(dm) || dm[dm.length - 1] !== ENTRY) { console.log(`!! 校验失败 ${f}`); continue; }
  if (dm.length !== (a.DamageModifiers || []).length + 1) { console.log(`!! 条数不符 ${f}`); continue; }
  // 除 DamageModifiers 外必须完全一致
  const aRest = { ...a }, bRest = { ...b };
  delete aRest.DamageModifiers; delete bRest.DamageModifiers;
  if (JSON.stringify(aRest) !== JSON.stringify(bRest)) { console.log(`!! 其他字段被改动 ${f}`); continue; }

  if (isEmpty) emptyArr.push(f);
  fs.writeFileSync(path.join(AFTER, f), out, 'utf8');
  done++;
}

console.log(`已写入 ${done}/${files.length} 个文件 → ${AFTER}`);
if (already.length) console.log('已存在该条目(未重复添加): ' + already.join(', '));
if (emptyArr.length) console.log('原本空中括号: ' + emptyArr.join(', '));
if (skipped.length) console.log('跳过: ' + skipped.join(', '));
