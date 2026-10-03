// report-veh-rules.mjs — 基于 veh-rules-effective.json + 服务端 kubejs 文件，输出方向抗性数据报告
import fs from 'node:fs';

const eff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8'));
const jarDir = 'vehjars/dragonrise_reforge-1.5.0-beta-20260927.jar';
const jarEff = JSON.parse(fs.readFileSync('veh-rules-effective.json', 'utf8')); // effective 里 jar 源即 jar 数据

// 1) jar 内置原始规则（直接从 vehjars 重算，取每条 raw）
function jarRaw(ns, name) {
  const p = `${jarDir}/${ns}__${name}.json`;
  if (!fs.existsSync(p)) return null;
  const t = fs.readFileSync(p, 'utf8').replace(/,(\s*[\]\}])/g, '$1');
  let j; try { j = JSON.parse(t); } catch { return null; }
  return (j.DamageModifiers ?? []).filter((x) => typeof x === 'string' && x.trim().startsWith('$'));
}

// 2) 按 m 分组（生效集）
const byM = {};
for (const [k, v] of Object.entries(eff.effective)) {
  for (const a of v.angle ?? []) (byM[a.m] ??= []).push(k);
}
console.log('===== 生效规则按 m 值分组 =====');
for (const m of Object.keys(byM).map(Number).sort((a, b) => b - a)) {
  const list = byM[m];
  console.log(`\nm=${m} (${list.length} 辆，前向倍率 ${Math.max(1 - m, 0.5)} / 背向 ${(1 + m).toFixed(2)})`);
  console.log('   ' + list.join(', '));
}

// 3) 丢失规则的 14 辆：给出 jar 里的原始 $ 条目
console.log('\n===== 被 kubejs 覆盖丢掉方向规则的载具（jar 原始条目）=====');
for (const k of eff.summary.lost) {
  const [ns, name] = k.split(':');
  const raw = jarRaw(ns, name);
  const kjs = eff.effective[k];
  console.log(`  ${k}  覆盖后条目数=${kjs.modCount}  jar 原始 $ 条目=${JSON.stringify(raw)}`);
}

// 4) 21 架飞机的血量门写法
console.log('\n===== 带血量门的载具（原始 $ 条目）=====');
const seen = new Set();
for (const [k, v] of Object.entries(eff.effective)) {
  for (const a of v.angle ?? []) {
    if (!a.gate) continue;
    const sig = a.raw;
    if (seen.has(sig)) continue;
    seen.add(sig);
    console.log(`  ${sig}`);
  }
}

// 5) 无规则的地面载具是否包含常出场的
const noRule = eff.summary.noRule;
const groundGuess = noRule.filter((k) => !/j10|j11|j15|j16|j20|j35|j8|jf17|f14|f15|f16|f18|fa18|av8|ac130|q5|jas39|refale|syy|tjgc|ah64|z20|mi28|a10|ah6|ju87|drone|heli|helicopter|_h$|ghast|kirov|plane|_air/i.test(k));
console.log(`\n无方向规则载具共 ${noRule.length} 架/辆（其中疑似地面 ${groundGuess.length}）`);
console.log('   地面部分: ' + groundGuess.join(', '));
