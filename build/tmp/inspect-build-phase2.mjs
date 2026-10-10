// inspect-build-phase2.mjs — fortifications.json 是否还有阶段键 + BastionSelectionPacket 的阶段门禁
import fs from 'node:fs';
const cfg = 'build/tmp/fortifications-before.json';
if (fs.existsSync(cfg)) {
  const t = fs.readFileSync(cfg, 'utf8');
  console.log('===== fortifications.json 里的阶段相关键 =====');
  const lines = t.split('\n');
  let n = 0;
  for (let i = 0; i < lines.length; i++) {
    if (/allowed_phase|allowedPhase|phase/i.test(lines[i])) { console.log((i + 1) + ': ' + lines[i].trim()); if (++n > 20) break; }
  }
  if (!n) console.log('  (没有任何阶段键 → 阶段门禁只在 logistics.json 的 radio.allowed_phases)');
} else console.log('(缺少 fortifications-before.json)');

const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' =====');
  const l = fs.readFileSync(p, 'utf8').split('\n');
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 150));
  }
};
dump('src/main/java/org/espetro/network/BastionSelectionPacket.java', 90, 120, 'BastionSelectionPacket 阶段门禁');
dump('src/main/java/org/espetro/bastion/BastionCommand.java', 70, 90, 'BastionCommand 阶段门禁');
console.log('===== 三图 logistics.json 当前 allowed_phases =====');
for (const m of ['server_battlefield', '越南', 'CREATE_PLUS']) {
  const f = 'build/tmp/maps-edit/before/' + m + '__EsConfig__logistics.json';
  if (!fs.existsSync(f)) { console.log('  ' + m + ': (缺本地副本)'); continue; }
  const j = JSON.parse(fs.readFileSync(f, 'utf8')).logistics;
  console.log('  ' + m + ': ' + JSON.stringify(j.radio.allowed_phases));
}
