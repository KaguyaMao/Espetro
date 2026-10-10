// sbw-port-preview.mjs — 生成"移植对照表"（不改任何文件，仅预览）
import fs from 'fs';

const D = 'D:/minecraft/modp/Espetro/build/tmp/sbw-veh/';
const PAIRS = [
  { fcp: 'fcp/stryker_m2.json', dr: 'dr/m1126.json', weapons: [['Coax', 'MachineGun']] },
  { fcp: 'fcp/stryker_mgs.json', dr: 'dr/m1128.json', weapons: [['Cannon', 'Cannon'], ['Coax', 'MachineGun'], ['PassengerMachineGun', 'PassengerMachineGun']] },
  { fcp: 'fcp/stryker_dragoon.json', dr: 'dr/m1296.json', weapons: [['Cannon', 'Cannon'], ['Coax', 'MachineGun']] }
];

const TIER1 = ['RPM', 'Damage', 'BypassesArmor', 'ExplosionRadius', 'ExplosionDamage'];
const TIER2 = ['Velocity', 'Spread', 'HeatPerShoot', 'NaturalCooldown', 'RecoilTime', 'RecoilForce', 'Magazine', 'EmptyReloadTime', 'SoundRadius'];
const AMMO_NUM = ['Velocity', 'Damage', 'ExplosionDamage', 'ExplosionRadius', 'Spread', 'RecoilTime', 'RecoilForce', 'RPM'];

const g = (p) => JSON.parse(fs.readFileSync(D + p, 'utf8'));

for (const pair of PAIRS) {
  const F = g(pair.fcp), R = g(pair.dr);
  console.log(`\n██████ ${pair.fcp.split('/').pop()}  →  ${pair.dr.split('/').pop()}   (${F.ID} → ${R.ID})`);
  console.log(`  血量 MaxHealth: ${R.MaxHealth} → ${F.MaxHealth}`);
  console.log(`  车毁爆炸 DestroyInfo: dr ${JSON.stringify(R.DestroyInfo)} → fcp ${JSON.stringify(F.DestroyInfo)}`);
  const dmF = F.DamageModifiers ?? [], dmR = R.DamageModifiers ?? [];
  const setF = new Set(dmF), setR = new Set(dmR);
  console.log(`  抗性 DamageModifiers: dr ${dmR.length} 条 → fcp ${dmF.length} 条`);
  console.log(`      fcp 有 dr 无 (${dmF.filter((x) => !setR.has(x)).length}): ${dmF.filter((x) => !setR.has(x)).join(' | ') || '无'}`);
  console.log(`      dr 有 fcp 无 (${dmR.filter((x) => !setF.has(x)).length}): ${dmR.filter((x) => !setF.has(x)).join(' | ') || '无'}`);

  for (const [fw, rw] of pair.weapons) {
    const a = F.Weapons[fw], b = R.Weapons[rw];
    if (!a || !b) { console.log(`  !! 武器缺失 fcp.${fw}=${!!a} dr.${rw}=${!!b}`); continue; }
    console.log(`  ── 武器 fcp.${fw} → dr.${rw}`);
    for (const k of [...TIER1, ...TIER2]) {
      if (a[k] === undefined && b[k] === undefined) continue;
      const mark = String(a[k]) === String(b[k]) ? '  ' : '≠ ';
      console.log(`     ${mark}${k.padEnd(16)} ${String(b[k] ?? '(无)').padStart(12)} → ${String(a[k] ?? '(无)')}   [${TIER1.includes(k) ? '一级' : '二级'}]`);
    }
    // 弹种 override 对比
    const ammoA = Array.isArray(a.AmmoType) ? a.AmmoType : null;
    const ammoB = Array.isArray(b.AmmoType) ? b.AmmoType : null;
    if (ammoA || ammoB) {
      console.log(`     弹种条目: dr ${ammoB ? ammoB.length : 0} 条 / fcp ${ammoA ? ammoA.length : 0} 条`);
      const norm = (arr) => (arr ?? []).map((e) => typeof e === 'string' ? { Ammo: e } : e);
      const A = norm(ammoA), B = norm(ammoB);
      for (let i = 0; i < Math.max(A.length, B.length); i++) {
        const x = A[i], y = B[i];
        console.log(`       [${i}] dr=${y ? (y.Ammo ?? '') + (y.Override ? ' +override' : '') : '(无)'}   fcp=${x ? (x.Ammo ?? '') + (x.Override ? ' +override' : '') : '(无)'}`);
        if (x?.Override && y?.Override) {
          for (const k of AMMO_NUM) {
            if (x.Override[k] === undefined && y.Override[k] === undefined) continue;
            const mark = String(x.Override[k]) === String(y.Override[k]) ? '  ' : '≠ ';
            console.log(`           ${mark}${k.padEnd(16)} ${String(y.Override[k] ?? '(无)').padStart(10)} → ${String(x.Override[k] ?? '(无)')}`);
          }
        } else if (x?.Override && !y) {
          console.log(`           + fcp 独有条目 ${JSON.stringify(x.Override).slice(0, 120)}`);
        } else if (!x && y?.Override) {
          console.log(`           - dr 独有条目 ${JSON.stringify(y.Override).slice(0, 120)}`);
        }
      }
    }
  }
}
