import fs from 'node:fs';
import path from 'node:path';

// add-repair-tool.mjs — 给编制的「载具队长 / 载具组员」各变体加上充满电的维修工具
// 用法: node add-repair-tool.mjs [--apply] <目录或文件...>
const ITEM = 'superbwarfare:repair_tool{Energy:100000}';
const TARGET_NAMES = new Set(['载具队长', '载具组员']);
const args = process.argv.slice(2);
const apply = args.includes('--apply');
const targets = args.filter(a => !a.startsWith('--'));

const files = [];
for (const t of targets) {
  const st = fs.statSync(t);
  if (st.isDirectory()) {
    for (const f of fs.readdirSync(t).filter(x => x.endsWith('.json'))) files.push(path.join(t, f));
  } else files.push(t);
}

let changedFiles = 0, added = 0;
for (const f of files.sort()) {
  const text = fs.readFileSync(f, 'utf8');
  let j;
  try { j = JSON.parse(text); } catch (e) { console.log(`  [跳过] ${path.basename(f)}: JSON 解析失败 ${e.message}`); continue; }

  // 先检查格式能否无损往返（决定用整体重写还是文本插入）
  const roundTrip = JSON.stringify(j, null, 2) === text || JSON.stringify(j, null, 2) + '\n' === text;

  const hits = [];
  for (const [key, cls] of Object.entries(j.classes || {})) {
    const isTarget = TARGET_NAMES.has(String(cls.name)) || /_CREW(_Leader)?$/.test(key);
    if (!isTarget) continue;
    for (const [vname, variant] of Object.entries(cls.variants || {})) {
      if (!Array.isArray(variant.commands)) continue;
      if (variant.commands.some(c => /repair_tool/i.test(String(c)))) continue; // 已经有了
      variant.commands.push(ITEM);
      hits.push(`${key}/${vname}`);
      added++;
    }
  }

  if (!hits.length) { console.log(`  [无需修改] ${path.basename(f)}`); continue; }
  changedFiles++;
  const note = roundTrip ? '整体重写(格式一致)' : '⚠ 格式非标准 2 空格缩进，将按 2 空格重写';
  console.log(`  [${apply ? '已写入' : '预演'}] ${path.basename(f)}  +${hits.length} 处  ${note}`);
  for (const h of hits) console.log(`        ${h}`);
  if (apply) fs.writeFileSync(f, roundTrip && text.endsWith('\n') ? JSON.stringify(j, null, 2) + '\n' : JSON.stringify(j, null, 2), 'utf8');
}
console.log(`\n共 ${added} 处、${changedFiles} 个文件${apply ? '（已写入）' : '（预演）'}`);
