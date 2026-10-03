// fix-vehtypes.mjs — 确保每个文件的 VehTypes 包含所有 vehicles 键
import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
for (const f of files) {
	const p = dir + f;
	const j = JSON.parse(fs.readFileSync(p, 'utf8'));
	const vt = Array.isArray(j.VehTypes) ? [...j.VehTypes] : [];
	const vehKeys = Object.keys(j.vehicles ?? {});
	let added = 0;
	for (const k of vehKeys) {
		if (!vt.includes(k)) {
			vt.push(k);
			added++;
		}
	}
	if (added > 0 || vt.length !== (Array.isArray(j.VehTypes) ? j.VehTypes.length : 0)) {
		j.VehTypes = vt;
		fs.writeFileSync(p, JSON.stringify(j, null, 2), 'utf8');
		console.log(`${f}: VehTypes 补全 ${added} 个 -> [${vt.join(', ')}]`);
	} else {
		console.log(`${f}: VehTypes 已完整`);
	}
}
