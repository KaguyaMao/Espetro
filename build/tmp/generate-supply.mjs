// generate-supply.mjs — 为未配置载具生成补给配置（文本插入，保留注释）
import fs from 'fs';
const FILE = 'D:/minecraft/modp/Espetro/build/tmp/supply-default.json';

// 解析（剥离注释）
function stripComments(c) {
	let out = ''; let i = 0; let inStr = false;
	while (i < c.length) {
		const ch = c[i];
		if (inStr) { out += ch; if (ch === '\\') { out += c[i+1] ?? ''; i += 2; continue; } if (ch === '"') inStr = false; i++; continue; }
		if (ch === '"') { inStr = true; out += ch; i++; continue; }
		if (ch === '/' && c[i+1] === '*') { const end = c.indexOf('*/', i + 2); if (end === -1) break; i = end + 2; continue; }
		if (ch === '/' && c[i+1] === '/') { const end = c.indexOf('\n', i + 2); i = end === -1 ? c.length : end; continue; }
		out += ch; i++;
	}
	return out;
}

const raw = fs.readFileSync(FILE, 'utf8');
const j = JSON.parse(stripComments(raw));
const V = j.VehicleOverrides ?? {};

// 模板
const t90 = V['dragonrise_reforge:t90mh'].AmmoOverrides;
const zbd04a = V['dragonrise_reforge:zbd04a'].AmmoOverrides;
const zbl08 = V['dragonrise_reforge:zbl08'].AmmoOverrides;
const zlt11 = V['dragonrise_reforge:zlt11'].AmmoOverrides;
const btr82 = V['fcp:btr82'].AmmoOverrides;

function ammo(overrides) {
	const parts = [];
	for (const [k, v] of Object.entries(overrides)) {
		parts.push(`      "${k}": {\n        "Mode": "FIXED",\n        "FixedAmount": ${v.FixedAmount}\n      }`);
	}
	return parts.join(',\n');
}

// 新条目定义
const entries = [
	{ id: 'dragonrise_reforge:t72b3', ao: t90, note: '参照t90mh' },
	{ id: 'dragonrise_reforge:bmp3', ao: zbd04a, note: '参照zbd04a' },
	{ id: 'dragonrise_reforge:zbd05', ao: zbl08, note: '参照zbl08' },
	{ id: 'dragonrise_reforge:ztd05', ao: zlt11, note: '参照zlt11' },
	{ id: 'dragonrise_reforge:m113', ao: { 'superbwarfare:heavy_ammo': { FixedAmount: 1000 } }, note: '重型子弹1000' },
	{ id: 'dragonrise_reforge:mv3_armed', ao: { 'superbwarfare:heavy_ammo': { FixedAmount: 1000 } }, note: '重型子弹1000' },
	{ id: 'fcp:gaz_tigr_gl', ao: { 'superbwarfare:grenade_40mm': { FixedAmount: 100 } }, note: '榴弹100' },
	{ id: 'fcp:gaz_tigr_mg', ao: { 'superbwarfare:heavy_ammo': { FixedAmount: 1000 } }, note: '重型子弹1000' },
	{ id: 'fcp:matv', ao: { 'superbwarfare:heavy_ammo': { FixedAmount: 1000 } }, note: '重型子弹1000' },
	{ id: 'fcp:matv_9in1', ao: { 'superbwarfare:heavy_ammo': { FixedAmount: 1000 } }, note: '重型子弹1000' },
	{ id: 'fcp:matv_crow', ao: { 'superbwarfare:heavy_ammo': { FixedAmount: 1000 } }, note: '重型子弹1000' },
	{ id: 'fcp:matv_tow', ao: { 'superbwarfare:medium_anti_ground_missile': { FixedAmount: 6 } }, note: '线控导弹6' },
	{ id: 'fcp:bmp1am', ao: btr82, note: '参照btr82' },
	{ id: 'fcp:bmp2', ao: {
		'superbwarfare:small_shell_ap': { FixedAmount: 200 },
		'superbwarfare:small_shell_he': { FixedAmount: 300 },
		'superbwarfare:rifle_ammo': { FixedAmount: 500 },
		'superbwarfare:medium_anti_ground_missile': { FixedAmount: 3 }
	}, note: '200穿甲/300高爆/500步枪/3导弹' },
	{ id: 'fcp:bmp2m', ao: {
		'superbwarfare:small_shell_ap': { FixedAmount: 200 },
		'superbwarfare:small_shell_he': { FixedAmount: 300 },
		'superbwarfare:rifle_ammo': { FixedAmount: 500 },
		'superbwarfare:medium_anti_ground_missile': { FixedAmount: 4 }
	}, note: '200穿甲/300高爆/500步枪/4导弹' }
];

// 构造插入文本
let insert = '';
for (const e of entries) {
	if (V[e.id]) { console.log(`${e.id}: 已存在，跳过`); continue; }
	insert += `\n  "${e.id}": {\n    "Mode": "FIXED",\n    "HealPercent": 50,\n    "AmmoOverrides": {\n${ammo(e.ao)}\n    }\n  },`;
	console.log(`${e.id}: 已生成（${e.note}）`);
}

if (insert.length > 0) {
	// 在 "VehicleOverrides": { 之后插入
	const marker = '"VehicleOverrides": {';
	const idx = raw.indexOf(marker);
	if (idx === -1) { console.log('找不到 VehicleOverrides 标记'); process.exit(1); }
	const pos = idx + marker.length;
	const newRaw = raw.slice(0, pos) + insert + '\n' + raw.slice(pos);
	// 验证修改后 JSON 仍可解析
	try {
		JSON.parse(stripComments(newRaw));
		console.log('JSON 验证通过');
	} catch (e) {
		console.log('JSON 验证失败: ' + e.message);
		process.exit(1);
	}
	fs.writeFileSync(FILE, newRaw, 'utf8');
	console.log('已写入');
}
