// verify-dirarmor.mjs — 校验生成的方向抗性文件：严格 JSON、规则正确、其余条目未变
import fs from 'node:fs';
import path from 'node:path';

const OUT = process.argv[2] ?? 'dirarmor';
const man = JSON.parse(fs.readFileSync(path.join(OUT, 'manifest.json'), 'utf8'));
const RULE = man.rule;
const ANGLE = /getSourceAngle/;

function dm(text) {
  const j = JSON.parse(text);
  return { list: j.DamageModifiers ?? [], json: j };
}
const isAngle = (s) => typeof s === 'string' && s.trim().startsWith('$') && ANGLE.test(s);

let ok = 0, bad = 0;
const newOverrides = [];
for (const f of man.files) {
  if (f.status !== 'ok') { console.log(`SKIP ${f.id} (${f.status})`); continue; }
  const before = fs.readFileSync(path.join(OUT, 'before', f.outName), 'utf8');
  const after = fs.readFileSync(path.join(OUT, 'after', f.outName), 'utf8');
  const problems = [];
  let a, b;
  try { a = dm(after); } catch (e) { problems.push('after 不是合法 JSON: ' + e.message); }
  try { b = dm(before.replace(/,(\s*\])/g, '$1')); } catch (e) { problems.push('before 非法 JSON: ' + e.message); }
  if (a && b) {
    const angleA = a.list.filter(isAngle);
    const angleB = b.list.filter(isAngle);
    const expect = f.note === '恢复 jar 原值' ? null : (man.rules ?? [RULE]);   // csk181 等恢复 jar 原值的文件例外
    if (expect) {
      if (JSON.stringify(angleA) !== JSON.stringify(expect)) {
        problems.push(`after 方向规则不符\n   实际=${JSON.stringify(angleA)}\n   期望=${JSON.stringify(expect)}`);
      }
    } else if (angleA.length !== 1) {
      problems.push(`恢复类文件方向规则数 = ${angleA.length}（应为 1）`);
    }
    if (angleB.length > 0 && f.note === '恢复 jar 原值' && angleB[0] !== angleA[0]) {
      problems.push(`恢复的规则与 jar 原值不同: ${JSON.stringify(angleA[0])} vs ${JSON.stringify(angleB[0])}`);
    }
    const restA = a.list.filter((x) => !isAngle(x));
    const restB = b.list.filter((x) => !isAngle(x));
    if (JSON.stringify(restA) !== JSON.stringify(restB)) {
      problems.push(`其余 DamageModifiers 条目发生变化: before=${restB.length} after=${restA.length}`);
    }
    // 其它字段逐键比对（允许 drone 这类原本没有 DamageModifiers 的文件新增该字段）
    const keysA = Object.keys(a.json).sort();
    const keysB = Object.keys(b.json).sort();
    const added = keysA.filter((k) => !keysB.includes(k));
    const removed = keysB.filter((k) => !keysA.includes(k));
    const allowedAdd = added.length === 1 && added[0] === 'DamageModifiers' && !('DamageModifiers' in b.json);
    if (removed.length) problems.push(`顶层字段被删除: ${removed.join(', ')}`);
    if (added.length && !allowedAdd) problems.push(`顶层字段新增: ${added.join(', ')}`);
  }
  if (f.newOverride) newOverrides.push(f);
  if (problems.length) { bad++; console.log(`[BAD] ${f.id}\n   - ` + problems.join('\n   - ')); }
  else { ok++; console.log(`[OK]  ${f.id.padEnd(32)} 规则数=${(f.rules ?? []).length} 源=${f.source}`); }
}
console.log(`\n校验通过 ${ok} 个，异常 ${bad} 个`);
if (newOverrides.length) {
  console.log(`\n新建的覆盖文件（服务端原本没有 → 回滚=删除）共 ${newOverrides.length}:`);
  for (const f of newOverrides) console.log(`   ${f.id}  -> ${f.outName}`);
}
