import fs from 'node:fs';
import path from 'node:path';

/**
 * add-1p68.mjs — 给俄编制里"单倍光瞄(红点)"的 AK-74M 补上 1P68 瞄准镜
 *
 * 规则（数据驱动）：
 *   遍历每个变体的所有 commands，找到 GunId:"ccrp:ak74m" 且 **没有** AttachmentSCOPE 的条目；
 *   如果该变体的 名称/描述 里出现「机瞄」，说明本来就是机械瞄具 → 跳过；
 *   否则视为缺少 1 倍光瞄 → 在 AttachmentEXTENDED_MAG 之后插入
 *   AttachmentSCOPE:{Count:1b,id:"tacz:attachment",tag:{AttachmentId:"huinuo:1p68"}},
 *
 * 用法: node add-1p68.mjs [--apply] <目录或文件...>
 */
const SCOPE_SNBT = 'AttachmentSCOPE:{Count:1b,id:"tacz:attachment",tag:{AttachmentId:"huinuo:1p68"}}';
const args = process.argv.slice(2);
const apply = args.includes('--apply');
const targets = args.filter(a => !a.startsWith('--'));

const files = [];
for (const t of targets) {
  const st = fs.statSync(t);
  if (st.isDirectory()) for (const f of fs.readdirSync(t).filter(x => x.endsWith('.json'))) files.push(path.join(t, f));
  else files.push(t);
}

/** 在 SNBT 里找到某个键的 {...} 结束位置（返回结束大括号的下标） */
function findKeyObjectEnd(s, keyStart) {
  const open = s.indexOf('{', keyStart);
  if (open < 0) return -1;
  let depth = 0;
  for (let i = open; i < s.length; i++) {
    const c = s[i];
    if (c === '"') { i = s.indexOf('"', i + 1); if (i < 0) return -1; continue; }
    if (c === '{') depth++;
    else if (c === '}') { depth--; if (depth === 0) return i; }
  }
  return -1;
}

function addScope(cmd) {
  if (!/GunId:"ccrp:ak74m"/.test(cmd) || /AttachmentSCOPE:/.test(cmd)) return null;
  const extKey = cmd.indexOf('AttachmentEXTENDED_MAG:');
  let insertAt;
  if (extKey >= 0) {
    const end = findKeyObjectEnd(cmd, extKey);
    if (end < 0) return null;
    insertAt = cmd[end + 1] === ',' ? end + 2 : end + 1;
  } else {
    insertAt = cmd.indexOf('{') + 1;
  }
  return cmd.slice(0, insertAt) + SCOPE_SNBT + ',' + cmd.slice(insertAt);
}

let changedFiles = 0, added = 0, skipped = 0;
for (const f of files.sort()) {
  const text = fs.readFileSync(f, 'utf8');
  let j;
  try { j = JSON.parse(text); } catch (e) { console.log(`  [跳过] ${path.basename(f)}: ${e.message}`); continue; }
  const roundTrip = JSON.stringify(j, null, 2) === text || JSON.stringify(j, null, 2) + '\n' === text;

  const hits = [], skips = [];
  for (const [ck, cls] of Object.entries(j.classes || {})) {
    for (const [vk, variant] of Object.entries(cls.variants || {})) {
      const isIronSight = /机瞄/.test(String(variant.name || '') + String(variant.description || ''));
      (variant.commands || []).forEach((cmd, i) => {
        const s = String(cmd);
        if (!/GunId:"ccrp:ak74m"/.test(s)) return;
        if (/AttachmentSCOPE:/.test(s)) return;              // 已有瞄准镜
        if (isIronSight) { skips.push(`${ck}/${vk}（描述含"机瞄"，保持机械瞄具）`); return; }
        const next = addScope(s);
        if (next === null) return;
        variant.commands[i] = next;
        hits.push(`${ck}/${vk}  [${String(variant.description || variant.name)}]`);
        added++;
      });
    }
  }

  if (!hits.length && !skips.length) { console.log(`  [无需修改] ${path.basename(f)}`); continue; }
  if (hits.length) changedFiles++;
  console.log(`  [${apply ? '已写入' : '预演'}] ${path.basename(f)}  +${hits.length} 处${roundTrip ? '' : '  ⚠格式非标准2空格'}`);
  for (const h of hits) console.log(`        + 1P68  ${h}`);
  for (const s of skips) console.log(`        - 跳过   ${s}`);
  if (apply && hits.length) fs.writeFileSync(f, roundTrip && text.endsWith('\n') ? JSON.stringify(j, null, 2) + '\n' : JSON.stringify(j, null, 2), 'utf8');
}
console.log(`\n共新增 1P68 ${added} 处、改动 ${changedFiles} 个文件${apply ? '（已写入）' : '（预演）'}`);
