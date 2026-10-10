// dump-class-meta.mjs — 打印类级字段（不含 variants 内容），用于克隆参考
import fs from 'fs';
function meta(f, cid) {
  const j = JSON.parse(fs.readFileSync(`out-${f}.json`, 'utf8'));
  const cls = j.classes[cid];
  if (!cls) { console.log(`(缺 ${f}/${cid})`); return; }
  const copy = { ...cls };
  copy.variants = Object.fromEntries(Object.entries(cls.variants || {}).map(([k, v]) => [k, v.name]));
  console.log(`--- ${f} / ${cid}`);
  console.log(JSON.stringify(copy, null, 1));
}
meta('us_1th_ar', 'US_1th_arma_SQUADMG');
meta('us_1th_ar', 'US_1th_arma_MMG');
meta('pla_112th_brigade', 'PLA_112_BIG_SQUADMG');
meta('pla_112th_brigade', 'PLA_112_SQUADMG');
meta('ru_205th', 'RU_205th_BIG_SQUADMG');
meta('ru_205th', 'RU_205th_SQUADMG');
