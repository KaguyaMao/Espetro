// analyze-factions.mjs — 解析所有编制文件的载具配置
import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();
for (const f of files) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	console.log('==================================================');
	console.log('FILE: ' + f + '  |  ' + (j.faction?.name ?? '?'));
	const vs = j.vehicles ?? {};
	for (const [k, v] of Object.entries(vs)) {
		const d = v.initial_deploy_delay_seconds;
		const dl = typeof d === 'object' && d !== null ? `attack:${d.attack ?? '?'} defend:${d.defend ?? '?'}` : (d !== undefined ? JSON.stringify(d) : 'NONE');
		const ents = Array.isArray(v.entity) ? v.entity.join(' | ') : String(v.entity ?? '?');
		console.log(`  [${k}] ${v.display_name ?? '?'} | entity=${ents} | delay=${dl} | troop_value=${v.troop_value ?? '?'} | respawn_minutes=${v.respawn_minutes ?? '?'} | supplyveh=${v.supplyveh ?? '-'} | fightveh=${v.fightveh ?? '-'}`);
	}
	const cls = j.classes ?? {};
	const troopValues = {};
	for (const [ck, cv] of Object.entries(cls)) {
		const tv = cv.troopValue;
		troopValues[tv] = (troopValues[tv] ?? 0) + 1;
	}
	console.log(`  CLASSES: ${Object.keys(cls).length} 职业, troopValue 分布: ${JSON.stringify(troopValues)}`);
}
