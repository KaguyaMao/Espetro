// inspect-spectator5.mjs — 自动分配循环 / 周期 tick / WaitingStatus 发送 / 团队分配处理名
import fs from 'node:fs';
const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' =====');
  const l = fs.readFileSync(p, 'utf8').split('\n');
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 165));
  }
};
dump(GSM, 336, 372, '自动分配队伍循环');
dump(GSM, 1228, 1252, '周期 tick（中途加入同步）');
dump('src/main/java/org/espetro/client/ClientPacketHandlers.java', 118, 150, 'TeamAssign/MapReveal/FactionReveal 处理');
{
  console.log('===== WaitingStatus 发送方法 =====');
  const files = [];
  (function walk(d) { for (const e of fs.readdirSync(d, { withFileTypes: true })) { const p = d + '/' + e.name; if (e.isDirectory()) walk(p); else if (e.name.endsWith('.java')) files.push(p); } })('src/main/java');
  let n = 0;
  for (const f of files) {
    const l = fs.readFileSync(f, 'utf8').split('\n');
    for (let i = 0; i < l.length; i++) {
      if (/sendWaitingStatus|WaitingStatusPacket\(/.test(l[i])) { console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + l[i].trim().slice(0, 140)); if (++n > 12) break; }
    }
    if (n > 12) break;
  }
}
