// modify-factions.mjs — 按用户规则批量修改编制载具配置
// 规则:
//   坦克(非坦克编制): delay 600/600, troop 15, respawn 15
//   坦克编制: 第1辆(现有attack delay小者) 0/300, 第2辆 600/900, troop 15, respawn 15
//   突击炮(突击炮/坦克歼击车/自行火炮): 0/600, 10, 10
//   步兵战车(含轮式): 0/600, 10, 10
//   装甲输送车(含补给型): 0/0, 7, 5
//   卡车(任何卡车): 0/0, 5, 3
//   运输直升机: 600/600, 10, 5
//   高机动载具(car): 保持现状(0/0, 5, 3)
//   步兵: 所有职业 troopValue = 1
import fs from 'fs';

const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const bakDir = 'D:/minecraft/modp/Espetro/build/tmp/desktop-backup/';
fs.mkdirSync(bakDir, { recursive: true });

const TANK_UNITS = new Set(['pla_195th.json', 'ru_6th_tank.json', 'usmc.json', 'us_1th_ar.json']);

function classify(displayName, key) {
	const n = String(displayName ?? '');
	if (n.includes('主战坦克')) return 'tank';
	if (n.includes('突击炮') || n.includes('坦克歼击车') || n.includes('自行火炮')) return 'assault_gun';
	if (n.includes('步兵战车')) return 'ifv';
	if (n.includes('装甲输送车')) return 'apc';
	if (n.includes('直升机')) return 'heli';
	if (n.includes('卡车')) return 'truck';
	if (n.includes('高机动载具')) return 'car';
	// key fallback
	if (key === 'mbt') return 'tank';
	if (key === 'mgs') return 'assault_gun';
	if (key === 'ifv') return 'ifv';
	if (key === 'apc' || key === 'acv') return 'apc';
	if (key === 'truck' || key === 'supply_truck') return 'truck';
	if (key === 'transport_helicopter') return 'heli';
	return 'car';
}

const RULES = {
	tank:        { delay: [600, 600], troop: 15, respawn: 15 },
	assault_gun: { delay: [0, 600], troop: 10, respawn: 10 },
	ifv:         { delay: [0, 600], troop: 10, respawn: 10 },
	apc:         { delay: [0, 0], troop: 7, respawn: 5 },
	truck:       { delay: [0, 0], troop: 5, respawn: 3 },
	heli:        { delay: [600, 600], troop: 10, respawn: 5 },
	car:         { delay: null, troop: null, respawn: null } // 保持现状
};

const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
const summary = [];

for (const f of files) {
	const src = dir + f;
	fs.copyFileSync(src, bakDir + f);
	const j = JSON.parse(fs.readFileSync(src, 'utf8'));
	const lines = [];
	const vs = j.vehicles ?? {};
	const entries = Object.entries(vs);

	// 坦克编制: 确定坦克条目的第一/第二辆 (按现有 attack delay 升序)
	let tankKeys = [];
	if (TANK_UNITS.has(f)) {
		tankKeys = entries
			.filter(([, v]) => classify(v.display_name, '') === 'tank')
			.map(([k, v]) => {
				const d = v.initial_deploy_delay_seconds;
				const atk = typeof d === 'object' && d ? Number(d.attack ?? 0) : 0;
				return { k, atk };
			})
			.sort((a, b) => a.atk - b.atk);
	}

	for (const [k, v] of entries) {
		const cls = classify(v.display_name, k);
		let rule = null;
		let role = cls;
		if (cls === 'tank') {
			if (TANK_UNITS.has(f) && tankKeys.length >= 2) {
				const idx = tankKeys.findIndex((t) => t.k === k);
				rule = idx === 0
					? { delay: [0, 300], troop: 15, respawn: 15 }
					: { delay: [600, 900], troop: 15, respawn: 15 };
				role = idx === 0 ? 'tank#1' : 'tank#2';
			} else {
				rule = RULES.tank;
			}
		} else {
			rule = RULES[cls];
		}
		if (!rule || rule.delay === null) continue; // car 等保持现状
		const oldDelay = (() => {
			const d = v.initial_deploy_delay_seconds;
			if (typeof d === 'object' && d) return `a:${d.attack ?? '?'} d:${d.defend ?? '?'}`;
			return d === undefined ? 'NONE' : JSON.stringify(d);
		})();
		// apply
		v.initial_deploy_delay_seconds = { attack: rule.delay[0], defend: rule.delay[1] };
		v.troop_value = rule.troop;
		v.respawn_minutes = rule.respawn;
		lines.push(`  [${k}] ${v.display_name} -> ${role} delay ${rule.delay[0]}/${rule.delay[1]} troop ${rule.troop} respawn ${rule.respawn} (was: ${oldDelay})`);
	}

	// classes: 全部 troopValue = 1
	let classCount = 0;
	for (const cv of Object.values(j.classes ?? {})) {
		if (typeof cv === 'object' && cv !== null && 'troopValue' in cv) {
			cv.troopValue = 1;
			classCount++;
		}
	}

	fs.writeFileSync(src, JSON.stringify(j, null, 2), 'utf8');
	summary.push(`FILE ${f} (${j.faction?.name}):\n` + lines.join('\n') + `\n  职业 troopValue->1: ${classCount} 个`);
}

fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/modify-summary.txt', summary.join('\n\n'), 'utf8');
console.log('DONE, summary written to modify-summary.txt');
