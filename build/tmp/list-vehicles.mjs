import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
const map = new Map(); // entity -> {display, units:Set}
for (const f of files) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	const unit = `${j.faction?.name ?? f}(${f.replace('.json','')})`;
	for (const v of Object.values(j.vehicles ?? {})) {
		const ents = Array.isArray(v.entity) ? v.entity : [v.entity];
		for (const e of ents) {
			if (!e) continue;
			if (!map.has(e)) map.set(e, { display: v.display_name ?? e, units: new Set() });
			map.get(e).units.add(unit);
		}
	}
}
// 按显示名分组输出
const byDisplay = new Map();
for (const [ent, info] of map) {
	if (!byDisplay.has(info.display)) byDisplay.set(info.display, []);
	byDisplay.get(info.display).push({ ent, units: info.units });
}
let i = 0;
for (const [display, items] of byDisplay) {
	i++;
	const ents = items.map(x => x.ent).join(' / ');
	const units = [...new Set([...items.flatMap(x => [...x.units])])];
	console.log(`${i}. ${display}`);
	console.log(`   实体: ${ents}`);
	console.log(`   编制(${units.length}): ${units.join('、')}`);
}
console.log(`\n共 ${byDisplay.size} 种载具型号, ${map.size} 个实体条目`);