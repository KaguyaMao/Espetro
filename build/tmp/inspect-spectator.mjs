// inspect-spectator.mjs — 查清观战改动所需的 5 个点
import fs from 'node:fs';
const read = (p) => fs.readFileSync(p, 'utf8').split('\n');
const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' (' + p.split('/').pop() + ' ' + a + '-' + b + ') =====');
  const l = read(p);
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 160));
  }
};
const grep = (p, re, label, limit = 18) => {
  console.log('===== ' + label + ' =====');
  const l = read(p);
  let n = 0;
  for (let i = 0; i < l.length; i++) {
    if (re.test(l[i])) { console.log((i + 1) + ': ' + l[i].trim().slice(0, 150)); if (++n >= limit) break; }
  }
  if (!n) console.log('  (无)');
};

const CMD = 'src/main/java/org/espetro/command/EspetroCommand.java';
const TSS = 'src/main/java/org/espetro/client/gui/TeamSelectionScreen.java';
const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
const NM = 'src/main/java/org/espetro/network/NetworkManager.java';

grep(CMD, /literal\("espetro"\)|\.requires\(|register\(/, '/espetro 根命令与权限', 10);
dump(CMD, 1, 40, '命令注册头部');
dump(TSS, 140, 200, '选边面板按钮与点击');
grep(TSS, /NetworkManager\.|SelectTeam|TeamSelect|onPress|clicked|sendTeam/, '选边面板发包点', 14);
grep(GSM, /自动分配队伍完成|teamSelectedPlayers\.add|waitingForTeam\.add|assignTeam|randomAssign|assignPlayers/, '队伍分配池', 18);
grep(NM, /BuildFortificationPacket|registerMessage|SpectateRequest|sendCloseModScreens/, 'NetworkManager 注册/发包', 16);
dump('src/main/java/org/espetro/network/BuildFortificationPacket.java', 1, 60, 'C2S 包模板');
