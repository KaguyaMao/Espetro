import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
let out = '# 编制总表\n\n';
out += '| # | 编制 | 文件 | 阵营 | 载具 | 职业 | 类型 |\n|---|---|---|---|---|---|---|\n';
let n = 0;
for (const f of files) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	n++;
	const team = j.faction?.team === 'ATTACK' ? '🇨🇳 进攻' : '🇷🇺/🇺🇸 防守';
	const vs = Object.entries(j.vehicles ?? {});
	const veh = vs.map(([k, v]) => `${v.display_name}`).join('、');
	const cls = Object.keys(j.classes ?? {}).length;
	const hasLeader = Object.keys(j.classes ?? {}).some((id) => id.endsWith('_CREW_Leader'));
	const type = f.includes('tank') || j.faction?.name?.includes('坦克') ? '坦克编制' : (f.includes('plamc') ? '海军陆战队' : '常规');
	out += `| ${n} | ${j.faction?.name ?? f} | ${f.replace('.json','')} | ${team} | ${veh} | ${cls} | ${type}${hasLeader ? '（含载具队长）' : ''} |\n`;
}
fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/faction-table.md', out, 'utf8');
console.log('done, ' + files.length + ' factions');