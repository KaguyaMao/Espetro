// kjs-skill-edit.mjs — 生成四个修改后的 KubeJS 技能脚本（基于服务端 live 副本）
// 1) 火炮：释放后 30 秒才开始射击（其余节奏不变）
// 2) 无人机：热禁用（server 回调直接拒绝）+ startup 加 .disabled()（下次重启后从轮盘消失）
import fs from 'node:fs';
import path from 'node:path';

const LIVE_SRV = 'kjs-server-live';
const LIVE_START = 'kjs-startup';
const OUT = 'kjs-edit';

function edit(srcFile, dstFile, edits) {
  let text = fs.readFileSync(srcFile, 'utf8');
  for (const [from, to, label] of edits) {
    if (!text.includes(from)) {
      throw new Error(`${dstFile}: 未找到锚点「${label}」\n---\n${from}\n---`);
    }
    const count = text.split(from).length - 1;
    if (count !== 1) throw new Error(`${dstFile}: 锚点「${label}」出现 ${count} 次，需唯一`);
    text = text.replace(from, to);
  }
  fs.mkdirSync(path.dirname(dstFile), { recursive: true });
  fs.writeFileSync(dstFile, text, 'utf8');
  console.log(`OK ${dstFile}  ${text.length} B`);
}

// ---------- 1) 火炮实现 ----------
edit(path.join(LIVE_SRV, '00_espetro_artillery_155.js'), path.join(OUT, 'server_scripts/00_espetro_artillery_155.js'), [
  [
    "var EspetroArtilleryScriptVersion = 'scheduled-waves-20260727'",
    "var EspetroArtilleryScriptVersion = 'scheduled-waves-30s-delay-20260925'\n"
    + "// 释放后到第一发之间的延迟（tick）：30 秒 = 600。改“预警时间”只需要改这一个数。\n"
    + "var EspetroArtilleryStartDelayTicks = 30 * 20",
    '版本号/延迟常量'
  ],
  [
    "    // 第一轮：两发校射。\n"
    + "    espetroScheduleArtilleryWave(server, cfg, 0, 1)\n"
    + "    espetroScheduleArtilleryWave(server, cfg, 50, 1)\n"
    + "\n"
    + "    // 第二轮：20 秒后开始，六轮、每轮四发、间隔四秒。\n"
    + "    for (var wave = 0; wave < 6; wave++) {\n"
    + "      espetroScheduleArtilleryWave(server, cfg, 400 + wave * 80, 4)\n"
    + "    }",
    "    // 释放后先等 EspetroArtilleryStartDelayTicks（默认 30 秒）再开始射击。\n"
    + "    // 第一轮：两发校射（延迟后 0 / 2.5 秒）。\n"
    + "    espetroScheduleArtilleryWave(server, cfg, EspetroArtilleryStartDelayTicks, 1)\n"
    + "    espetroScheduleArtilleryWave(server, cfg, EspetroArtilleryStartDelayTicks + 50, 1)\n"
    + "\n"
    + "    // 第二轮：延迟后 20 秒开始，六轮、每轮四发、间隔四秒。\n"
    + "    for (var wave = 0; wave < 6; wave++) {\n"
    + "      espetroScheduleArtilleryWave(server, cfg, EspetroArtilleryStartDelayTicks + 400 + wave * 80, 4)\n"
    + "    }",
    '排程'
  ],
  [
    "  event.tell('§a火炮支援已确认，共 26 发。')",
    "  event.tell('§a火炮支援已确认：30 秒后开始射击，共 26 发。')",
    '提示文本'
  ],
  [
    "  console.info('[Espetro] artillery_155 scheduled 8 waves / 26 shells in session ' + cfg.sessionId)",
    "  console.info('[Espetro] artillery_155 scheduled 8 waves / 26 shells, first wave in '\n"
    + "    + (EspetroArtilleryStartDelayTicks / 20) + 's, session ' + cfg.sessionId)",
    '日志'
  ],
]);

// ---------- 2) 无人机实现（热禁用） ----------
edit(path.join(LIVE_SRV, '00_espetro_drone_detection.js'), path.join(OUT, 'server_scripts/00_espetro_drone_detection.js'), [
  [
    "EspetroCommanderSkills.on('drone_detection', event => {\n  const range = 100.0",
    "// 【暂时禁用】恢复功能：把下面这行改回 false 并 /reload 即可。\n"
    + "var EspetroDroneDetectionDisabled = true\n"
    + "\n"
    + "EspetroCommanderSkills.on('drone_detection', event => {\n"
    + "  if (EspetroDroneDetectionDisabled) {\n"
    + "    event.tell('§c无人机侦测已暂时停用。')\n"
    + "    return false\n"
    + "  }\n"
    + "  const range = 100.0",
    '禁用开关'
  ],
]);

// ---------- 3) 无人机注册（下次重启后彻底移除） ----------
edit(path.join(LIVE_START, '00_espetro_drone_detection.js'), path.join(OUT, 'startup_scripts/00_espetro_drone_detection.js'), [
  [
    "  .cooldownSeconds(60)\n  .register()",
    "  .cooldownSeconds(60)\n"
    + "  // 【暂时禁用】恢复时删除下一行的 .disabled()（改这里需要重启服务端才生效）\n"
    + "  .disabled()\n"
    + "  .register()",
    'disabled'
  ],
]);

// ---------- 4) 火炮注册（补上 30 秒预警的说明，重启后生效） ----------
edit(path.join(LIVE_START, '00_espetro_artillery_155.js'), path.join(OUT, 'startup_scripts/00_espetro_artillery_155.js'), [
  [
    ".stats('§8两轮炮击 | 冷却: 180秒')",
    ".stats('§8释放后30秒开始 | 两轮炮击 | 冷却: 180秒')",
    'stats'
  ],
]);
