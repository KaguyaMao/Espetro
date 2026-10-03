// modify-crew-seats.mjs — 防弹车/装甲输送车 vehicle_crew_seats = 0（无需载具组员职业）
import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const bakDir = 'D:/minecraft/modp/Espetro/build/tmp/crewseats-backup/';
fs.mkdirSync(bakDir, { recursive: true });

const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
let total = 0;

for (const f of files) {
	const path = dir + f;
	fs.copyFileSync(path, bakDir + f);
	const j = JSON.parse(fs.readFileSync(path, 'utf8'));
	const changed = [];
	for (const [k, v] of Object.entries(j.vehicles ?? {})) {
		const name = String(v.display_name ?? '');
		const isApc = name.includes('装甲输送车');
		const isBulletproof = k === 'car';
		if (isApc || isBulletproof) {
			if (v.vehicle_crew_seats !== 0) {
				v.vehicle_crew_seats = 0;
				changed.push(`${k}(${name})`);
			}
		}
	}
	if (changed.length > 0) {
		fs.writeFileSync(path, JSON.stringify(j, null, 2), 'utf8');
		console.log(`${f}: ${changed.join('; ')}`);
		total += changed.length;
	}
}
console.log(`\n共设置 ${total} 个条目`);
