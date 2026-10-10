// add-crew-leader.mjs — 参照 PLA 为俄/美编制添加"载具队长"职业
import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const bakDir = 'D:/minecraft/modp/Espetro/build/tmp/crewleader-backup/';
fs.mkdirSync(bakDir, { recursive: true });

const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();

for (const f of files) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	if (j.faction?.faction_id === 'PLA') {
		console.log(`${f}: PLA 跳过`);
		continue;
	}
	const cmdId = Object.keys(j.classes ?? {}).find((id) => id.endsWith('_COMMANDER'));
	if (!cmdId) {
		console.log(`${f}: 无 COMMANDER 职业，跳过`);
		continue;
	}
	const prefix = cmdId.replace('_COMMANDER', '');
	const leaderId = prefix + '_CREW_Leader';
	if (j.classes[leaderId]) {
		console.log(`${f}: 已有 ${leaderId}，跳过`);
		continue;
	}
	const commander = j.classes[cmdId];
	const leader = structuredClone(commander);
	leader.strict_count = false;
	leader.name = '载具队长';
	leader.icon = 'lead_crewman';
	leader.vehicle_crew = true;
	leader.description = '装甲载具指挥官';
	leader.role = '载具操作与指挥';
	leader.maxPlayers = 1;
	leader.team_count = true;
	leader.max_per_squad = 1;
	leader.troopValue = 1;
	leader.row = 1;
	leader.unlock_per_n = 0;
	leader.unlock_min_squad = 0;
	leader.leader_only = true;
	leader.IconImage = '/home/shu/图片/Icon/lead_crewman.png';
	for (const vv of Object.values(leader.variants ?? {})) {
		if (Array.isArray(vv.commands)) {
			vv.commands = vv.commands.filter((c) => !c.includes('hand_grenade'));
		}
		if (vv.resupply && Array.isArray(vv.resupply.items)) {
			vv.resupply.items = vv.resupply.items.filter((it) => !String(it.id ?? '').includes('hand_grenade'));
		}
	}
	fs.copyFileSync(dir + f, bakDir + f);
	// 重建 classes 保持顺序：CREW_Leader 插在 COMMANDER 之后
	const newClasses = {};
	for (const [k, v] of Object.entries(j.classes)) {
		newClasses[k] = v;
		if (k === cmdId) newClasses[leaderId] = leader;
	}
	j.classes = newClasses;
	fs.writeFileSync(dir + f, JSON.stringify(j, null, 2), 'utf8');
	console.log(`${f}: 添加 ${leaderId} (基于 ${cmdId})`);
}

console.log('\n===== 验证 =====');
for (const f of files) {
	try {
		const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
		const leaders = Object.keys(j.classes ?? {}).filter((id) => id.endsWith('_CREW_Leader'));
		console.log(`${f}: VALID, CREW_Leader=${leaders.join(',') || '无'}, classes=${Object.keys(j.classes ?? {}).length}`);
	} catch (e) {
		console.log(`${f}: INVALID ${e.message}`);
	}
}
