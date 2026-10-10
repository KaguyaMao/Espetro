// search-dirarmor.mjs — 搜索用 N 条 getSourceAngle 规则（可乘常数因子）能否精确命中目标倍率
// 规则：f_i(dot) = C_i * max(1 - m_i*dot, 0.5)，总体 = Π f_i
const TARGETS = [
  { name: '0.5 / 1.1 / 1.5 (你最新要的)', front: 0.5, side: 1.1, back: 1.5 },
  { name: '0.5 / 1.0 / 1.5 (纯 m=0.5)', front: 0.5, side: 1.0, back: 1.5 },
];

function f(mList, C, dot) {
  let r = C;
  for (const m of mList) r *= Math.max(1 - m * dot, 0.5);
  return r;
}
function cost(mList, C, t) {
  return (f(mList, C, 1) - t.front) ** 2 + (f(mList, C, 0) - t.side) ** 2 + (f(mList, C, -1) - t.back) ** 2;
}

for (const t of TARGETS) {
  console.log(`\n=== 目标 ${t.name} ===`);
  // 单条规则：m 与 C 扫描
  let best = { c: Infinity };
  for (let m = -2; m <= 2; m += 0.001) {
    for (let C = 0.5; C <= 1.6; C += 0.005) {
      const c = cost([m], C, t);
      if (c < best.c) best = { c, m, C, mList: [m] };
    }
  }
  console.log(`  1 条规则最优: 残差=${best.c.toFixed(6)} m=${best.m.toFixed(4)} C=${best.C.toFixed(3)} → 正面 ${f(best.mList, best.C, 1).toFixed(4)} / 侧面 ${f(best.mList, best.C, 0).toFixed(4)} / 背面 ${f(best.mList, best.C, -1).toFixed(4)}`);

  // 两条规则：粗扫 + 局部细化
  let best2 = { c: Infinity };
  for (let m1 = -2; m1 <= 2; m1 += 0.01) {
    for (let m2 = -2; m2 <= 2; m2 += 0.01) {
      // C 由侧面锁定
      const C = t.side;
      const c = cost([m1, m2], C, t);
      if (c < best2.c) best2 = { c, mList: [m1, m2], C };
    }
  }
  for (let iter = 0; iter < 3; iter++) {
    const step = [0.002, 0.0005, 0.0001][iter];
    let improved = true;
    while (improved) {
      improved = false;
      for (const i of [0, 1]) {
        for (const d of [-step, step]) {
          const cand = [...best2.mList];
          cand[i] += d;
          const c = cost(cand, t.side, t);
          if (c < best2.c - 1e-15) { best2 = { c, mList: cand, C: t.side }; improved = true; }
        }
      }
    }
  }
  console.log(`  2 条规则最优: 残差=${best2.c.toFixed(9)} m=[${best2.mList.map((x) => x.toFixed(6)).join(', ')}] C=${best2.C} → 正面 ${f(best2.mList, best2.C, 1).toFixed(4)} / 侧面 ${f(best2.mList, best2.C, 0).toFixed(4)} / 背面 ${f(best2.mList, best2.C, -1).toFixed(4)}`);
}
