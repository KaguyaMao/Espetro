// dump-party.mjs — 输出组队相关实现
import fs from 'node:fs';
const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' (' + p.split('/').pop() + ' ' + a + '-' + b + ') =====');
  const l = fs.readFileSync(p, 'utf8').split('\n');
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 165));
  }
};
const PN = 'src/main/java/org/espetro/network/';
const PG = 'src/main/java/org/espetro/client/gui/PartyScreen.java';
const PM = 'src/main/java/org/espetro/team/PartyManager.java';
dump(PN + 'PartyListPacket.java', 1, 114, 'PartyListPacket 全文');
dump(PN + 'PartyActionPacket.java', 1, 141, 'PartyActionPacket 全文');
dump(PM, 39, 200, 'PartyManager 主体');
dump(PG, 95, 140, 'PartyScreen 列表与加入按钮');
dump(PG, 169, 250, 'CreatePartyScreen');
dump(PG, 249, 300, 'JoinPartyScreen');
dump(PG, 300, 360, 'ManagePartyScreen');
