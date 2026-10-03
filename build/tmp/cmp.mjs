import fs from 'fs';
const LD = 'D:/minecraft/modp/GScode/src/main/resources/data/dragonrise_reforge/sbw/vehicles/';
const SD = 'D:/minecraft/modp/Espetro/build/tmp/server-vehicles/';
for (const f of ['zbd05.json', 'ztd05.json', 'zlt11.json']) {
	const l = JSON.parse(fs.readFileSync(LD + f, 'utf8'));
	const s = JSON.parse(fs.readFileSync(SD + f, 'utf8'));
	console.log(`===== ${f} =====`);
	console.log('本地武器: ' + Object.keys(l.Weapons ?? {}).join(', '));
	console.log('服务端武器: ' + Object.keys(s.Weapons ?? {}).join(', '));
	// 本地每个武器的 velocity 路径
	for (const [wn, w] of Object.entries(l.Weapons ?? {})) {
		const velos = [];
		if (w && typeof w === 'object') {
			for (const [k, v] of Object.entries(w)) {
				if (k.toLowerCase().includes('velocity')) velos.push(k + '=' + JSON.stringify(v));
			}
		}
		if (velos.length) console.log('  本地 ' + wn + ': ' + velos.join('; '));
	}
}