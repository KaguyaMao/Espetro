import fs from 'node:fs';

// show-gun-fields.mjs — 打印若干 m4a1 数据文件的关键字段，用于判断哪份是"生效"的
function lenientParse(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) { out += c; if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false; continue; }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    if (c === '/' && text[i + 1] === '*') { const e = text.indexOf('*/', i + 2); i = e < 0 ? text.length : e + 1; out += '\n'; continue; }
    out += c;
  }
  return JSON.parse(out.replace(/,(\s*[}\]])/g, '$1').replace(/:\s*\+(\d)/g, ': $1'));
}

for (const f of process.argv.slice(2)) {
  if (!fs.existsSync(f)) { console.log(`${f}: (不存在)`); continue; }
  const j = lenientParse(fs.readFileSync(f, 'utf8'));
  const ex = j.bullet?.extra_damage || {};
  console.log(`${f}`);
  console.log(`   aim_time=${j.aim_time}  重量=${j.weight}  RPM=${j.rpm}  伤害=${j.bullet?.damage}`);
  console.log(`   爆头×${ex.head_shot_multiplier}  无视护甲=${ex.armor_ignore}  衰减=${JSON.stringify(ex.damage_adjust)}`);
  console.log(`   attachment_types=${JSON.stringify(j.allow_attachment_types)}`);
}
