import fs from 'fs';
const LD = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
const SD = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
function allVelos(obj, path, out) {
	if (obj && typeof obj === 'object') {
		for (const [k, v] of Object.entries(obj)) {
			const p = path + '/' + k;
			if (k.toLowerCase().includes('velocity')) out.push([p, v]);
			allVelos(v, p, out);
		}
	}
}
for (const f of ['zbd05.json', 'ztd05.json', 'zlt11.json']) {
	const l = JSON.parse(fs.readFileSync(LD + f, 'utf8'));
	const s = JSON.parse(fs.readFileSync(SD + f, 'utf8'));
	const lv = []; allVelos(l, '', lv);
	const sv = []; allVelos(s, '', sv);
	console.log(`===== ${f} =====`);
	const smap = new Map(sv.map(([p, v]) => [p, JSON.stringify(v)]));
	for (const [p, v] of lv) {
		const sval = smap.get(p);
		const mark = sval === undefined ? '服务端无此路径' : (sval === JSON.stringify(v) ? '相同' : `服务端=${sval}`);
		console.log(`  ${p} 本地=${JSON.stringify(v)}  ${mark}`);
	}
}