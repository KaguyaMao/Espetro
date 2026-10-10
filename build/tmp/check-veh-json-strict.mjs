// check-veh-json-strict.mjs — 严格校验载具 JSON 并列出 $ 方向条目
// 用法: node check-veh-json-strict.mjs <file...>
import fs from 'node:fs';

for (const f of process.argv.slice(2)) {
  const t = fs.readFileSync(f, 'utf8');
  try {
    const j = JSON.parse(t);
    const dm = j.DamageModifiers;
    console.log(`${f}  OK  大小=${t.length}  DamageModifiers=${Array.isArray(dm) ? dm.length : '无'}`);
    if (Array.isArray(dm)) {
      const dollar = dm.filter((x) => typeof x === 'string' && x.trim().startsWith('$'));
      console.log(`   $ 条目 (${dollar.length}): ${JSON.stringify(dollar)}`);
      const last = dm.slice(-4);
      console.log(`   末 4 条: ${JSON.stringify(last)}`);
    }
  } catch (e) {
    console.log(`${f}  FAIL  ${e.message}`);
    const m = /position (\d+)/.exec(e.message);
    if (m) {
      const p = Number(m[1]);
      console.log('   上下文: ' + JSON.stringify(t.slice(Math.max(0, p - 200), p + 120)));
    }
  }
}
