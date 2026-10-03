import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
for (const f of files) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	const hits = [];
	for (const [k, v] of Object.entries(j.vehicles ?? {})) {
		const name = String(v.display_name ?? '');
		const isApc = k === 'acv' || k === 'apc' || name.includes('装甲输送车');
		const isBulletproof = k === 'car' || k.startsWith('matv') || name.includes('高机动载具') || name.includes('防弹') || name.includes('防雷');
		if (isApc || isBulletproof) {
			hits.push(`${k}(${name}) crew=${v.vehicle_crew_seats ?? '未设置'}`);
		}
	}
	if (hits.length) console.log(`${f}: ${hits.join('; ')}`);
}