// solve-dirarmor.mjs — 解析求解：用 N 条 getSourceAngle 规则（乘积）精确命中三向倍率
//
// 设每条规则 i 在正面的因子 a_i = max(1-m_i, 0.5)、背面因子 b_i = max(1+m_i, 0.5)，侧面恒为 1。
// 目标 (front, side, back) 需要：
//   side = C（常数，用「恒真血量门」实现）
//   Π a_i = front / C ,  Π b_i = back / C
// 本脚本扫描 m3，解析求 m1/m2，再数值复核，输出可用解。
const FRONT = Number(process.argv[2] ?? 0.5);
const SIDE = Number(process.argv[3] ?? 1.1);
const BACK = Number(process.argv[4] ?? 1.5);

const C = SIDE;
const needA = FRONT / C, needB = BACK / C;
const a = (m) => Math.max(1 - m, 0.5);
const b = (m) => Math.max(1 + m, 0.5);
const evalRules = (ms) => ms.reduce((p, m) => p * a(m), C) && ms.reduce((p, m) => p * a(m), C);
function tri(ms) {
  return {
    front: C * ms.reduce((p, m) => p * a(m), 1),
    side: C,
    back: C * ms.reduce((p, m) => p * b(m), 1),
  };
}

const sols = [];
for (let m3 = -3; m3 <= 3; m3 += 0.0005) {
  const A = needA / a(m3);
  const B = needB / b(m3);
  if (A <= 0 || B <= 0) continue;
  if (Math.abs(a(m3) - 0.5) < 1e-12 && m3 < 0.5) continue; // 夹紧自洽性
  const S = (4 + A - B) / 2;           // a1 + a2
  const disc = S * S - 4 * A;          // 判别式
  if (disc < 0) continue;
  const r = Math.sqrt(disc);
  for (const sgn of [1, -1]) {
    const a1 = (S + sgn * r) / 2, a2 = (S - sgn * r) / 2;
    if (a1 < 0.5 - 1e-9 || a2 < 0.5 - 1e-9) continue;
    const m1 = 1 - a1, m2 = 1 - a2;
    // 夹紧一致性：a(m) 必须等于我们用的值
    if (Math.abs(a(m1) - a1) > 1e-9 || Math.abs(a(m2) - a2) > 1e-9) continue;
    const t = tri([m1, m2, m3]);
    if (Math.abs(t.front - FRONT) > 1e-6 || Math.abs(t.back - BACK) > 1e-6) continue;
    sols.push({ ms: [m1, m2, m3], t, spread: Math.max(...[m1, m2, m3].map(Math.abs)) });
  }
}
sols.sort((x, y) => x.spread - y.spread);
console.log(`目标: 正面 ${FRONT} / 侧面 ${SIDE} / 背面 ${BACK}   （常数 C=${C}，needA=${needA.toFixed(8)} needB=${needB.toFixed(8)}）`);
console.log(`可行解 ${sols.length} 组，按 |m| 最大值从小到大取前 8：`);
for (const s of sols.slice(0, 8)) {
  console.log(`  m=[${s.ms.map((x) => x.toFixed(10)).join(', ')}]  →  ${s.t.front.toFixed(6)} / ${s.t.side.toFixed(6)} / ${s.t.back.toFixed(6)}`);
}
if (sols.length) {
  const best = sols[0];
  console.log(`\n推荐: m=[${best.ms.map((x) => x.toFixed(10)).join(', ')}]，常数 ${C}`);
  console.log('对应 JSON 条目（第 3 条挂常数血量门）：');
  best.ms.forEach((m, i) => {
    const gate = i === best.ms.length - 1 ? ` * (entity.getHealth() > -1 ? ${C} : ${C})` : '';
    console.log(`  "$entity.getSourceAngle(source, ${m.toFixed(5)}) * damage${gate}"`);
  });
}
