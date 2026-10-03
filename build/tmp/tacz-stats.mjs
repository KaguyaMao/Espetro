import fs from 'node:fs';
import path from 'node:path';

// taCZ-stats.mjs — 从 tacz/ 各枪包里提取编制在用武器的服务端数据
// 用法: node taCZ-stats.mjs <tacz目录> [gunId...]
const taczDir = process.argv[2];
const wanted = process.argv.slice(3);

/** 去掉 JSON 里的 // 注释（字符串内不处理） */
function stripComments(text) {
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) {
      out += c;
      if (esc) esc = false;
      else if (c === '\\') esc = true;
      else if (c === '"') inStr = false;
      continue;
    }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    out += c;
  }
  return out;
}

const packs = fs.readdirSync(taczDir)
  .map(n => path.join(taczDir, n))
  .filter(p => { try { return fs.statSync(p).isDirectory(); } catch { return false; } })
  .map(p => {
    let ns = null;
    try { ns = JSON.parse(fs.readFileSync(path.join(p, 'gunpack.meta.json'), 'utf8')).namespace; } catch { }
    return { dir: p, name: path.basename(p), ns };
  }).filter(p => p.ns);

const gunData = new Map();  // "ns:gun" -> {data, pack}
const gunIndex = new Map(); // "ns:gun" -> index json
for (const pack of packs) {
  const dataGuns = path.join(pack.dir, 'data', pack.ns, 'data', 'guns');
  if (fs.existsSync(dataGuns)) {
    for (const f of fs.readdirSync(dataGuns).filter(x => x.endsWith('.json'))) {
      const gun = f.replace(/_data\.json$/, '').replace(/\.json$/, '');
      const id = `${pack.ns}:${gun}`;
      try {
        const j = JSON.parse(stripComments(fs.readFileSync(path.join(dataGuns, f), 'utf8')));
        if (gunData.has(id)) console.log(`  ⚠ 重复定义 ${id}（${gunData.get(id).pack} 与 ${pack.name}）`);
        gunData.set(id, { data: j, pack: pack.name, file: path.join(dataGuns, f) });
      } catch (e) { console.log(`  ⚠ 解析失败 ${id}: ${e.message}`); }
    }
  }
  const idxDir = path.join(pack.dir, 'data', pack.ns, 'index', 'guns');
  if (fs.existsSync(idxDir)) {
    for (const f of fs.readdirSync(idxDir).filter(x => x.endsWith('.json'))) {
      const gun = f.replace(/\.json$/, '');
      try { gunIndex.set(`${pack.ns}:${gun}`, JSON.parse(stripComments(fs.readFileSync(path.join(idxDir, f), 'utf8')))); } catch { }
    }
  }
}

console.log(`扫描 ${packs.length} 个枪包，共 ${gunData.size} 把枪的数据\n`);

const ids = wanted.length ? wanted : [...gunData.keys()].sort();
const rows = [];
for (const id of ids) {
  const entry = gunData.get(id);
  if (!entry) { rows.push({ id, missing: true }); continue; }
  const d = entry.data;
  const b = d.bullet || {};
  const ex = b.extra_damage || {};
  const inc = d.inaccuracy || {};
  const semi = d.fire_mode_adjust?.semi || {};
  const idx = gunIndex.get(id) || {};
  rows.push({
    id,
    name: idx.name || idx.display || '',
    pack: entry.pack,
    dmg: b.damage,
    dmgSemi: semi.damage !== undefined ? (b.damage ?? 0) + semi.damage : null,
    headshot: ex.head_shot_multiplier,
    armorIgnore: ex.armor_ignore,
    falloff: Array.isArray(ex.damage_adjust) ? ex.damage_adjust.map(x => `${x.distance}m→${x.damage}`).join(' ') : '',
    aimTime: d.aim_time,
    drawTime: d.draw_time,
    inaccuracy: `站${inc.stand} 移${inc.move} 蹲${inc.sneak} 卧${inc.lie} 瞄${inc.aim}`,
    semiAimInacc: semi.aim_inaccuracy,
    rpm: d.rpm,
    speed: b.speed,
    pierce: b.pierce,
    mag: d.ammo_amount,
    magExt: Array.isArray(d.extended_mag_ammo_amount) ? d.extended_mag_ammo_amount.join('/') : '',
    weight: d.weight,
    fireModes: (d.fire_mode || []).join('/'),
    ammo: d.ammo,
    recoilPitch: d.recoil?.pitch?.[0]?.value,
    recoilYaw: d.recoil?.yaw?.[0]?.value,
    reloadTactical: d.reload?.feed?.tactical,
    reloadEmpty: d.reload?.feed?.empty,
  });
}

for (const r of rows) {
  if (r.missing) { console.log(`✘ 缺少数据: ${r.id}`); continue; }
  console.log(`═══ ${r.id}${r.name ? '  「' + r.name + '」' : ''}   [包: ${r.pack}]`);
  console.log(`   伤害        ${r.dmg}${r.dmgSemi !== null ? `（半自动 ${r.dmgSemi}）` : ''}   爆头×${r.headshot}   无视护甲 ${r.armorIgnore}`);
  if (r.falloff) console.log(`   距离衰减    ${r.falloff}`);
  console.log(`   瞄准速度    aim_time ${r.aimTime}s   开镜后扩散 ${r.inaccuracy.split(' ').pop()}`.replace('瞄准速度    aim_time ', '瞄准时间(ADS) '));
  console.log(`   扩散        ${r.inaccuracy}${r.semiAimInacc !== undefined ? `  （半自动瞄准修正 ${r.semiAimInacc}）` : ''}`);
  console.log(`   射速/弹速   ${r.rpm} RPM / ${r.speed} m·s⁻¹   弹匣 ${r.mag}${r.magExt ? `（扩容 ${r.magExt}）` : ''}   穿透 ${r.pierce}`);
  console.log(`   重量/模式   ${r.weight}   ${r.fireModes}   弹药 ${r.ammo}   换弹 ${r.reloadTactical}s/空仓 ${r.reloadEmpty}s   后坐 ${JSON.stringify(r.recoilPitch)}/${JSON.stringify(r.recoilYaw)}`);
  console.log('');
}
