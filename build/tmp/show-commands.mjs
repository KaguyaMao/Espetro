// show-commands.mjs — 打印指定职业某变体的 commands
import fs from 'fs';
const CFG = 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
const file = process.argv[2], classId = process.argv[3];
const j = JSON.parse(fs.readFileSync(CFG + file, 'utf8'));
const cls = j.classes[classId];
if (!cls) { console.log('no class ' + classId); process.exit(1); }
for (const [vid, v] of Object.entries(cls.variants || {})) {
  console.log(`\n--- ${vid} (${v.name}) commands=${(v.commands || []).length}`);
  for (const c of v.commands || []) console.log('  ' + c.slice(0, 400));
}
