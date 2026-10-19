// 把世界里已有的 SBW 载具覆盖文件（世界数据包）里的三个动力字段改成重标定后的值。
// 用法：node tools/patch_world_vehicle_speed.mjs
// 数据来源：build/tmp/veh-wt/out/<ns>/<id>.json（重标定产物）
// 目标：build/tmp/veh-speed/wdpacks/<ns>/<id>.json（从服务端世界数据包下载的副本）
import fs from 'node:fs';
import path from 'node:path';

const ROOT = path.resolve(import.meta.dirname, '..');
const OUT = path.join(ROOT, 'build/tmp/veh-wt/out');
const WDP = path.join(ROOT, 'build/tmp/veh-speed/wdpacks');
const FIELDS = ['MaxForwardSpeedRate', 'MaxBackwardSpeedRate', 'Increment'];

const targets = [
  ['dragonrise_reforge', 't72b3'],
  ['dragonrise_reforge', 'ztz96a'],
  ['dragonrise_reforge', 'ztz99a'],
];

function fieldOf(text, name) {
  const re = new RegExp(`"${name}"\\s*:\\s*(-?[0-9.]+)`);
  const m = text.match(re);
  return m ? m[1] : null;
}

let changed = 0;
for (const [ns, id] of targets) {
  const src = path.join(OUT, ns, `${id}.json`);
  const dst = path.join(WDP, ns, `${id}.json`);
  if (!fs.existsSync(src)) throw new Error(`缺少重标定文件 ${src}`);
  if (!fs.existsSync(dst)) throw new Error(`缺少世界数据包副本 ${dst}`);
  const srcText = fs.readFileSync(src, 'utf8');
  let text = fs.readFileSync(dst, 'utf8');
  const report = [];
  for (const f of FIELDS) {
    const v = fieldOf(srcText, f);
    if (v === null) throw new Error(`${src} 里找不到 ${f}`);
    const re = new RegExp(`("${f}"\\s*:\\s*)-?[0-9.]+`);
    if (!re.test(text)) throw new Error(`${dst} 里找不到 ${f}`);
    text = text.replace(re, `$1${v}`);
    report.push(`${f}=${v}`);
  }
  fs.writeFileSync(dst, text, 'utf8');
  changed++;
  console.log(`✓ ${ns}/${id}  ${report.join('  ')}`);
}
console.log(`完成：${changed} 个文件`);
