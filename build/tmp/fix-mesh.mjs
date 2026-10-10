// fix-mesh.mjs — 修复 pla_112th_brigade_mesh.json: VehTypes 加 acv (最后一项是 transport_helicopter)
import fs from 'fs';
const tmp = 'D:/minecraft/modp/Espetro/build/tmp/';
let c = fs.readFileSync(tmp + 'local-pla_112th_brigade_mesh.json', 'utf8');
const before = c;
c = c.replace('    "transport_helicopter"\n  ],', '    "transport_helicopter",\n    "acv"\n  ],');
console.log('replaced: ' + (c !== before));
fs.writeFileSync(tmp + 'fix-pla_112th_brigade_mesh.json', c, 'utf8');
try {
	const j = JSON.parse(c);
	console.log('VALID; VehTypes = ' + JSON.stringify(j.VehTypes));
} catch (e) {
	console.log('INVALID: ' + e.message);
}
