import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
for (const f of ['us_2nd_stryker.json', 'us_redone.json', 'us_1th_ar.json', 'us_1th_ri.json']) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	const st = j.vehicles?.supply_truck;
	const tk = j.vehicles?.truck;
	console.log(`${f}: truck.entity=${JSON.stringify(tk?.entity)} | supply_truck.entity=${JSON.stringify(st?.entity)}`);
}