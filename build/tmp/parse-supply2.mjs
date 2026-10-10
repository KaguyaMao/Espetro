import fs from 'fs';
const c = fs.readFileSync('D:/minecraft/modp/Espetro/build/tmp/supply-default.json', 'utf8');
let out = ''; let i = 0; let inStr = false;
while (i < c.length) {
	const ch = c[i];
	if (inStr) { out += ch; if (ch === '\\') { out += c[i+1] ?? ''; i += 2; continue; } if (ch === '"') inStr = false; i++; continue; }
	if (ch === '"') { inStr = true; out += ch; i++; continue; }
	if (ch === '/' && c[i+1] === '*') { const end = c.indexOf('*/', i + 2); if (end === -1) break; i = end + 2; continue; }
	if (ch === '/' && c[i+1] === '/') { const end = c.indexOf('\n', i + 2); i = end === -1 ? c.length : end; continue; }
	out += ch; i++;
}
const j = JSON.parse(out);
for (const k of ['dragonrise_reforge:t90mh', 'dragonrise_reforge:zbd04a', 'dragonrise_reforge:zlt11', 'dragonrise_reforge:zbl08', 'fcp:btr82', 'fcp:gaz_tigr_rws', 'dragonrise_reforge:zsl10']) {
	const v = j.VehicleOverrides?.[k];
	if (!v) { console.log(`===== ${k} (无配置) =====`); continue; }
	console.log(`===== ${k} Mode=${v.Mode} Heal=${v.HealPercent} =====`);
	console.log(JSON.stringify(v.AmmoOverrides ?? {}, null, 1));
}