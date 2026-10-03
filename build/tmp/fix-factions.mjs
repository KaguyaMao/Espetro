// fix-factions.mjs — 本地修复三个编制文件
import fs from 'fs';
const tmp = 'D:/minecraft/modp/Espetro/build/tmp/';

// 1. pla_118th: VehTypes 尾逗号 + fightvehc + vehicles 结尾多逗号
let c1 = fs.readFileSync(tmp + 'local-pla_118th_brigade.json', 'utf8');
c1 = c1.replace('    "supply_truck",\n  ],', '    "supply_truck"\n  ],');
c1 = c1.replace('"fightvehc": true', '"fightveh": true');
c1 = c1.replace(
	'"supplyveh": true,\n      "respawn_minutes": 10,\n      "troop_value": 5\n    },\n  },\n  "classes"',
	'"supplyveh": true,\n      "respawn_minutes": 10,\n      "troop_value": 5\n    }\n  },\n  "classes"'
);
fs.writeFileSync(tmp + 'fix-pla_118th_brigade.json', c1, 'utf8');

// 2. plamc_5th: transport_helicopter.entity 尾逗号
let c2 = fs.readFileSync(tmp + 'local-plamc_5th.json', 'utf8');
c2 = c2.replace('"dragonrise_reforge:z20",\n      ],', '"dragonrise_reforge:z20"\n      ],');
fs.writeFileSync(tmp + 'fix-plamc_5th.json', c2, 'utf8');

// 3. pla_195th: VehTypes 加 acv
let c3 = fs.readFileSync(tmp + 'local-pla_195th.json', 'utf8');
c3 = c3.replace('    "supply_truck"\n  ],', '    "supply_truck",\n    "acv"\n  ],');
fs.writeFileSync(tmp + 'fix-pla_195th.json', c3, 'utf8');

// validate
for (const f of ['fix-pla_118th_brigade.json', 'fix-plamc_5th.json', 'fix-pla_195th.json']) {
	try {
		JSON.parse(fs.readFileSync(tmp + f, 'utf8'));
		console.log(f + ' => VALID');
	} catch (e) {
		console.log(f + ' => INVALID: ' + e.message);
	}
}
// sanity checks
console.log('pla_118th has fightvehc: ' + fs.readFileSync(tmp + 'fix-pla_118th_brigade.json', 'utf8').includes('fightvehc'));
console.log('pla_195th has acv in VehTypes: ' + fs.readFileSync(tmp + 'fix-pla_195th.json', 'utf8').includes('    "acv"'));
