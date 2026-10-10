import fs from 'node:fs';
import path from 'node:path';

// tacz-stats2.mjs — 提取 tacz/<枪包>/data/<ns>/data/guns/*_data.json 的武器数据（宽松解析）
// 用法: node tacz-stats2.mjs <tacz目录> <gunId...>
const taczDir = process.argv[2];
const wanted = process.argv.slice(3);

function lenientParse(text) {
  // 去注释
  let out = '', inStr = false, esc = false;
  for (let i = 0; i < text.length; i++) {
    const c = text[i];
    if (inStr) {
      out += c;
      if (esc) esc = false; else if (c === '\\') esc = true; else if (c === '"') inStr = false;
      continue;
    }
    if (c === '"') { inStr = true; out += c; continue; }
    if (c === '/' && text[i + 1] === '/') { while (i < text.length && text[i] !== '\n') i++; out += '\n'; continue; }
    out += c;
  }
  // 尾随逗号
  out = out.replace(/,(\s*[}\]])/g, '$1');
  // 形如 ": +0.15"
  out = out.replace(/:\s*\+(\d)/g, ': $1');
  // 空数组元素 ,,
  return JSON.parse(out);
}

const packs = fs.readdirSync(taczDir)
  .map(n => path.join(taczDir, n))
  .filter(p => { try { return fs.statSync(p).isDirectory(); } catch { return false; } })
  .map(p => {
    let ns = null;
    try { ns = JSON.parse(fs.readFileSync(path.join(p, 'gunpack.meta.json'), 'utf8')).namespace; } catch { }
    return { dir: p, name: path.basename(p), ns };
  }).filter(p => p.ns);

const gunData = new Map();
const gunIndex = new Map();
const langs = new Map();  // ns -> {zh:{}, en:{}}

for (const pack of packs) {
  const dataRoot = path.join(pack.dir, 'data');
  if (fs.existsSync(dataRoot)) {
    for (const nsDir of fs.readdirSync(dataRoot)) {
      const ns = nsDir;
      const gd = path.join(dataRoot, ns, 'data', 'guns');
      if (fs.existsSync(gd)) {
        for (const f of fs.readdirSync(gd).filter(x => x.endsWith('.json'))) {
          const gun = f.replace(/_data\.json$/, '').replace(/\.json$/, '');
          const id = `${ns}:${gun}`;
          try {
            const j = lenientParse(fs.readFileSync(path.join(gd, f), 'utf8'));
            const prev = gunData.get(id);
            gunData.set(id, { data: j, pack: pack.name, file: path.join(gd, f), overridden: prev ? prev.pack : null });
          } catch (e) { /* 忽略解析失败的枪 */ }
        }
      }
      const idx = path.join(dataRoot, ns, 'index', 'guns');
      if (fs.existsSync(idx)) {
        for (const f of fs.readdirSync(idx).filter(x => x.endsWith('.json'))) {
          try { gunIndex.set(`${ns}:${f.replace(/\.json$/, '')}`, lenientParse(fs.readFileSync(path.join(idx, f), 'utf8'))); } catch { }
        }
      }
      // 语言文件
      for (const lang of ['zh_cn', 'en_us']) {
        const lp = path.join(pack.dir, 'assets', ns, 'lang', `${lang}.json`);
        if (fs.existsSync(lp)) {
          if (!langs.has(ns)) langs.set(ns, {});
          const store = langs.get(ns);
          if (!store[lang]) { try { store[lang] = lenientParse(fs.readFileSync(lp, 'utf8')); } catch { store[lang] = {}; } }
        }
      }
    }
  }
}

function nameOf(idStr) {
  const [ns] = idStr.split(':');
  const key = gunIndex.get(idStr)?.name;
  const store = langs.get(ns) || {};
  if (key) return store.zh_cn?.[key] || store.en_us?.[key] || key;
  return '';
}

console.log(`扫描 ${packs.length} 个枪包，共 ${gunData.size} 把枪\n`);

for (const id of wanted) {
  const entry = gunData.get(id);
  if (!entry) { console.log(`✘ 缺少数据: ${id}`); continue; }
  const d = entry.data;
  const b = d.bullet || {}, ex = b.extra_damage || {}, inc = d.inaccuracy || {}, semi = d.fire_mode_adjust?.semi || {};
  const nm = nameOf(id);
  console.log(`═══ ${id}${nm ? '  「' + nm + '」' : ''}${entry.overridden ? `   [包: ${entry.pack}（覆盖了 ${entry.overridden}）]` : `   [包: ${entry.pack}]`}`);
  console.log(`   伤害 ${b.damage}${semi.damage !== undefined ? `（半自动 ${(b.damage ?? 0) + semi.damage}）` : ''}   爆头×${ex.head_shot_multiplier}   无视护甲 ${ex.armor_ignore}   穿透 ${b.pierce}`);
  if (Array.isArray(ex.damage_adjust)) console.log(`   距离衰减 ${ex.damage_adjust.map(x => `${x.distance}→${x.damage}`).join('  ')}`);
  console.log(`   瞄准时间(ADS) ${d.aim_time}s   出枪 ${d.draw_time}s   收枪 ${d.put_away_time}s   冲刺 ${d.sprint_time}s`);
  console.log(`   扩散 站${inc.stand} 移${inc.move} 蹲${inc.sneak} 卧${inc.lie} 开镜${inc.aim}${semi.aim_inaccuracy !== undefined ? `  (半自动开镜修正 ${semi.aim_inaccuracy})` : ''}`);
  console.log(`   射速 ${d.rpm} RPM   弹速 ${b.speed}   弹匣 ${d.ammo_amount}${Array.isArray(d.extended_mag_ammo_amount) ? `（扩容 ${d.extended_mag_ammo_amount.join('/')}）` : ''}   重量 ${d.weight}   模式 ${(d.fire_mode || []).join('/')}`);
  console.log(`   弹药 ${d.ammo}   换弹 战术${d.reload?.feed?.tactical}s/空仓${d.reload?.feed?.empty}s   后坐 垂直${JSON.stringify(d.recoil?.pitch?.[0]?.value)} 水平${JSON.stringify(d.recoil?.yaw?.[0]?.value)}   移动速度(开镜) ${d.movement_speed?.aim}`);
  console.log('');
}
