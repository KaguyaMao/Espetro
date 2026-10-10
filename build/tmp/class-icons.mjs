// class-icons.mjs — 列出配置中所有职业的 名称/icon/role（按编制）
import fs from 'fs';

const CFG = process.argv[2] || 'D:/minecraft/modp/Espetro/build/tmp/fxall/';
for (const f of fs.readdirSync(CFG).filter((x) => x.endsWith('.json')).sort()) {
  const j = JSON.parse(fs.readFileSync(CFG + f, 'utf8'));
  console.log(`\n### ${f}`);
  for (const [cid, cls] of Object.entries(j.classes || {})) {
    const variants = Object.keys(cls.variants || {}).length;
    console.log(`${cid.padEnd(28)} icon=${String(cls.icon).padEnd(20)} role=${String(cls.role || '').padEnd(12)} name=${String(cls.name || '').padEnd(10)} 变体=${variants} img=${cls.IconImage || cls.iconImage || ''}`);
  }
}
