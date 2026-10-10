// inspect-spectator3.mjs — 找编制选择/揭示/地图揭示的发包点与中途加入面板打开点
import fs from 'node:fs';
const files = [];
(function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = d + '/' + e.name;
    if (e.isDirectory()) walk(p); else if (e.name.endsWith('.java')) files.push(p);
  }
})('src/main/java');

const pats = [
  [/FactionSelectionPacket|FactionRevealPacket|MapRevealPacket|TeamSelectStatePacket|CommanderVotePacket|MapVoteStatePacket/, '投票/揭示相关包的使用'],
  [/sendFaction|sendReveal|openFactionSelection|openFactionReveal|showFaction/, '编制界面发包方法'],
];
for (const [re, label] of pats) {
  console.log('===== ' + label + ' =====');
  let n = 0;
  for (const f of files) {
    const l = fs.readFileSync(f, 'utf8').split('\n');
    for (let i = 0; i < l.length; i++) {
      if (re.test(l[i])) { console.log(f.replace('src/main/java/', '') + ':' + (i + 1) + '  ' + l[i].trim().slice(0, 135)); if (++n > 40) break; }
    }
    if (n > 40) break;
  }
}
console.log('\n===== 客户端投票界面打开点（ClientPacketHandlers）=====');
{
  const f = 'src/main/java/org/espetro/client/ClientPacketHandlers.java';
  const l = fs.readFileSync(f, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/Screen|setScreen|handle.*Vote|handle.*Faction|handle.*Reveal|handleTeamSelect/.test(l[i])) {
      console.log((i + 1) + ': ' + l[i].trim().slice(0, 135));
    }
  }
}
