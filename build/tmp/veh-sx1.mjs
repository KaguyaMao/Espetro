import fs from 'node:fs';
const lp = (t) => JSON.parse(t.replace(/,(\s*[}\]])/g, '$1'));
const j = JSON.parse(fs.readFileSync('veh-angle/before/dragonrise__sx1.json', 'utf8').replace(/,(\s*[}\]])/g, '$1'));
console.log('sx1 HP=', j.MaxHealth, 'Type=', j.Type);
console.log('sx1 DamageModifiers:');
for (const m of (j.DamageModifiers || [])) console.log('   ', m);
const a = fs.readFileSync('veh-angle/before/dragonrise__sx1_a.json', 'utf8');
console.log('\nsx1_a 是否含 DamageModifiers:', a.includes('DamageModifiers'));
console.log('sx1_a HP/Type:', JSON.parse(a.replace(/,(\s*[}\]])/g, '$1')).MaxHealth, JSON.parse(a.replace(/,(\s*[}\]])/g, '$1')).Type);
// 看 sx1 文件里 DamageModifiers 段落的原文，供复制
const t = fs.readFileSync('veh-angle/before/dragonrise__sx1.json', 'utf8');
const i = t.indexOf('"DamageModifiers"');
const end = t.indexOf('\n  ],', i);
console.log('\nsx1 原文片段:\n' + t.slice(i, end + 5));
