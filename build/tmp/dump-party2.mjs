// dump-party2.mjs — 补读客户端与管理界面 + 发包/收包
import fs from 'node:fs';
const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' =====');
  const l = fs.readFileSync(p, 'utf8').split('\n');
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 170));
  }
};
const PG = 'src/main/java/org/espetro/client/gui/PartyScreen.java';
const CPH = 'src/main/java/org/espetro/client/ClientPacketHandlers.java';
const NM = 'src/main/java/org/espetro/network/NetworkManager.java';
dump(PG, 1, 95, 'PartyScreen 头部与 update');
dump(PG, 300, 370, 'ManagePartyScreen');
console.log('===== handlePartyList =====');
{
  const l = fs.readFileSync(CPH, 'utf8').split('\n');
  const i = l.findIndex((x) => /handlePartyList/.test(x));
  for (let k = i; k < i + 10; k++) if (l[k] !== undefined && l[k].trim()) console.log((k + 1) + ': ' + l[k].trim().slice(0, 160));
}
console.log('===== NetworkManager 里的队伍发包 =====');
{
  const l = fs.readFileSync(NM, 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/sendParty|broadcastPartyList|sendPartyListTo/.test(l[i])) console.log((i + 1) + ': ' + l[i].trim().slice(0, 150));
  }
}
