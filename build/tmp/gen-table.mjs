// gen-table.mjs — 生成所有编制载具配置表 (Markdown)
import fs from 'fs';
const dir = 'C:/Users/Administrator/Desktop/编制文件/';
const files = fs.readdirSync(dir).filter((f) => f.endsWith('.json')).sort();

function classify(displayName, key) {
	const n = String(displayName ?? '');
	if (n.includes('主战坦克')) return '坦克';
	if (n.includes('突击炮') || n.includes('坦克歼击车') || n.includes('自行火炮')) return '突击炮';
	if (n.includes('步兵战车')) return '步兵战车';
	if (n.includes('装甲输送车')) return '装甲输送车';
	if (n.includes('直升机')) return '运输直升机';
	if (n.includes('卡车')) return '卡车';
	if (n.includes('高机动载具')) return '高机动载具';
	if (key === 'mbt') return '坦克';
	if (key === 'mgs') return '突击炮';
	if (key === 'ifv') return '步兵战车';
	if (key === 'apc' || key === 'acv') return '装甲输送车';
	if (key === 'truck' || key === 'supply_truck') return '卡车';
	if (key === 'transport_helicopter') return '运输直升机';
	return '其他';
}

let out = '# 所有编制载具配置总表\n\n';
out += '| 编制 | 键名 | 载具 | 类型 | 攻击冷却 | 防守冷却 | 价值(票) | 击毁冷却(分) | 补给 | 战斗 |\n';
out += '|---|---|---|---|---|---|---|---|---|---|\n';

const typeStats = {};
for (const f of files) {
	const j = JSON.parse(fs.readFileSync(dir + f, 'utf8'));
	const fname = `${f.replace('.json', '')}`;
	const fdisplay = j.faction?.name ?? '?';
	for (const [k, v] of Object.entries(j.vehicles ?? {})) {
		const cls = classify(v.display_name, k);
		const d = v.initial_deploy_delay_seconds;
		const atk = typeof d === 'object' && d ? (d.attack ?? 0) : 0;
		const def = typeof d === 'object' && d ? (d.defend ?? 0) : 0;
		const atkTxt = atk === 0 ? '立即' : atk + '秒';
		const defTxt = def === 0 ? '立即' : def + '秒';
		const sup = v.supplyveh ? '✓' : '';
		const fight = v.fightveh ? '✓' : '';
		out += `| ${fdisplay} (${fname}) | ${k} | ${v.display_name} | ${cls} | ${atkTxt} | ${defTxt} | ${v.troop_value} | ${v.respawn_minutes} | ${sup} | ${fight} |\n`;
		typeStats[cls] = (typeStats[cls] ?? 0) + 1;
	}
}

out += '\n## 类型汇总\n\n| 类型 | 载具数 | 攻击冷却 | 防守冷却 | 价值 | 击毁冷却 |\n|---|---|---|---|---|---|\n';
const ruleRows = {
	'坦克(非坦克编制)': ['600秒', '600秒', '15票', '15分'],
	'坦克(坦克编制第1辆)': ['立即', '300秒', '15票', '15分'],
	'坦克(坦克编制第2辆)': ['600秒', '900秒', '15票', '15分'],
	'突击炮': ['立即', '600秒', '10票', '10分'],
	'步兵战车': ['立即', '600秒', '10票', '10分'],
	'装甲输送车': ['立即', '立即', '7票', '5分'],
	'卡车': ['立即', '立即', '5票', '3分'],
	'运输直升机': ['600秒', '600秒', '10票', '5分'],
	'高机动载具': ['立即', '立即', '5票', '3分']
};
for (const [t, c] of Object.entries(typeStats)) {
	out += `| ${t} | ${c} | ${ruleRows[t]?.[0] ?? '-'} | ${ruleRows[t]?.[1] ?? '-'} | ${ruleRows[t]?.[2] ?? '-'} | ${ruleRows[t]?.[3] ?? '-'} |\n`;
}

fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/vehicle-table.md', out, 'utf8');
console.log('written');
