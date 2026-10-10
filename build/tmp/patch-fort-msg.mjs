// patch-fort-msg.mjs — 修正放置失败提示（方块已不再阻止，只有实体会）
import fs from 'node:fs';
const F = 'src/main/java/org/espetro/bastion/FortificationManager.java';
let text = fs.readFileSync(F, 'utf8');
const eol = text.includes('\r\n') ? '\r\n' : '\n';
const from = 'if (!spaceIsClear(player.serverLevel(), finalSlots, player)) return "§c红色范围内存在方块或实体。";';
const to = 'if (!spaceIsClear(player.serverLevel(), finalSlots, player)) return "§c红色范围内存在实体（工事可以顶掉方块，但不能压在玩家/生物身上）。";';
const n = text.split(from).length - 1;
if (n !== 1) { console.error(`❌ 匹配 ${n} 次`); process.exit(1); }
fs.writeFileSync(F, text.replace(from, to), 'utf8');
console.log('✓ 提示文字已更新');
