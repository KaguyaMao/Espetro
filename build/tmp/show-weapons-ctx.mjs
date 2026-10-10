// show-weapons-ctx.mjs — 打印文件里所有 "Weapons" 出现处的上下文，判断哪些是武器定义表
import fs from 'node:fs';
const f = process.argv[2];
const t = fs.readFileSync(f, 'utf8');
const re = /"Weapons"\s*:/g;
let m, n = 0;
while ((m = re.exec(t)) !== null) {
  n++;
  const after = t.slice(m.index + m[0].length).replace(/^\s*/, '');
  const kind = after.startsWith('{') ? '对象(武器表?)' : after.startsWith('[') ? '数组(座位武器名列表)' : '其它';
  // 判断对象里是否含武器特征字段
  const seg = t.slice(m.index, m.index + 400);
  const isDef = /"AmmoType"|"DefaultZoom"|"RPM"|"Spread"|"Damage"/.test(seg);
  const line = t.slice(0, m.index).split('\n').length;
  console.log(`#${n} 行 ${line}  ${kind}  含武器字段=${isDef}`);
  console.log('   ' + seg.replace(/\s+/g, ' ').slice(0, 220));
}
console.log(`共 ${n} 处 "Weapons"`);
