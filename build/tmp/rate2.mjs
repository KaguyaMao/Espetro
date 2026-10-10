import fs from 'fs';
const j = JSON.parse(fs.readFileSync('D:/minecraft/modp/Espetro/build/tmp/server-vehicles-fixed/ztz99a.json', 'utf8'));
const mg = j.Weapons.MachineGun;
console.log('MachineGun 字段:');
for (const [k, v] of Object.entries(mg)) {
	if (typeof v !== 'object') console.log(`  ${k}: ${v}`);
}