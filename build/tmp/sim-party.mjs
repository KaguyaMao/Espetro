// sim-party.mjs — 复现旧算法的 bug 并验证新算法保持同队
function run(version, parties, soloCount) {
  // parties: [[m1,m2,...], ...]
  const result = new Map();
  let unassigned = [];
  const groups = parties.map((p) => p.slice());
  groups.sort((a, b) => b.length - a.length);
  let attack = 0, defend = 0;
  const partyOf = new Map();
  groups.forEach((g, gi) => g.forEach((m) => partyOf.set(m, gi)));

  for (const g of groups) {
    if (attack <= defend) { g.forEach((m) => result.set(m, 'ATTACK')); attack += g.length; }
    else { g.forEach((m) => result.set(m, 'DEFEND')); defend += g.length; }
  }
  const solos = [];
  for (let i = 0; i < soloCount; i++) solos.push('solo' + (i + 1));

  const remaining = new Set(groups.map((_, i) => i));
  if (version === 'new') {
    for (const s of solos) {
      if (attack <= defend) { result.set(s, 'ATTACK'); attack++; } else { result.set(s, 'DEFEND'); defend++; }
    }
  } else {
    unassigned.push(...solos);
  }

  let guard = 0;
  while (Math.abs(attack - defend) > 2 && remaining.size > 0 && guard++ < 100) {
    const over = attack > defend ? 'ATTACK' : 'DEFEND';
    let biggest = -1, best = -1;
    for (const gi of remaining) {
      const g = groups[gi];
      const onOver = g.some((m) => over === result.get(m));
      if (!onOver) continue;
      if (g.length > best) { best = g.length; biggest = gi; }
    }
    if (biggest < 0) break;
    for (const m of groups[biggest]) {
      if (version === 'new') {
        const prev = result.get(m);
        result.delete(m);
        if (prev == null) continue;
        unassigned.push(m);
        if (prev === 'ATTACK') attack--; else defend--;
      } else {
        result.delete(m);            // 旧算法：先删
        unassigned.push(m);
        if ('ATTACK' === (result.get(m) ?? null)) attack--;      // 再读 → 永远 null
        else if ('DEFEND' === (result.get(m) ?? null)) defend--;
      }
    }
    remaining.delete(biggest);
    if (version === 'old') { attack = 0; defend = 0; for (const t of result.values()) { if (t === 'ATTACK') attack++; else defend++; } }
  }
  for (const u of unassigned) {
    if (attack <= defend) { result.set(u, 'ATTACK'); attack++; } else { result.set(u, 'DEFEND'); defend++; }
  }
  // 统计每个队伍是否被拆
  const verdict = groups.map((g) => {
    const teams = new Set(g.map((m) => result.get(m)));
    return teams.size === 1 ? '同队✓' : '被拆✗(' + [...g.map((m) => result.get(m))].join('/') + ')';
  });
  const a = [...result.values()].filter((v) => v === 'ATTACK').length;
  const d = [...result.values()].filter((v) => v === 'DEFEND').length;
  return { verdict, attack: a, defend: d };
}

const cases = [
  { label: '5人：组队3 + 散人2', parties: [['A1', 'A2', 'A3']], solos: 2 },
  { label: '7人：组队4 + 散人3', parties: [['A1', 'A2', 'A3', 'A4']], solos: 3 },
  { label: '8人：组队3 + 组队2 + 散人3', parties: [['A1', 'A2', 'A3'], ['B1', 'B2']], solos: 3 },
  { label: '8人：组队6 + 散人2', parties: [['A1', 'A2', 'A3', 'A4', 'A5', 'A6']], solos: 2 },
  { label: '10人：组队4 + 组队3 + 散人3', parties: [['A1', 'A2', 'A3', 'A4'], ['B1', 'B2', 'B3']], solos: 3 },
];
for (const c of cases) {
  const oldR = run('old', c.parties, c.solos);
  const newR = run('new', c.parties, c.solos);
  console.log('=== ' + c.label + ' ===');
  console.log('  旧: 攻' + oldR.attack + '/守' + oldR.defend + '  队伍: ' + oldR.verdict.join(' | '));
  console.log('  新: 攻' + newR.attack + '/守' + newR.defend + '  队伍: ' + newR.verdict.join(' | '));
}
