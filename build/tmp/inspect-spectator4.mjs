// inspect-spectator4.mjs — 读注册样板 / 发包样板 / AUI 按钮样板 / 客户端 UI 处理头部
import fs from 'node:fs';
const dump = (p, a, b, label) => {
  console.log('===== ' + label + ' (' + p.split('/').pop() + ' ' + a + '-' + b + ') =====');
  const l = fs.readFileSync(p, 'utf8').split('\n');
  for (let i = a - 1; i < b && i < l.length; i++) {
    const s = (l[i] || '').replace(/\s+$/, '');
    if (s.trim()) console.log((i + 1) + ': ' + s.slice(0, 165));
  }
};
const NM = 'src/main/java/org/espetro/network/NetworkManager.java';
dump(NM, 88, 112, '注册样板');
dump(NM, 630, 645, 'sendFactionSelect 样板');
dump('src/main/java/org/espetro/client/ClientPacketHandlers.java', 20, 60, '客户端 UI 处理头部');
dump('src/main/java/org/espetro/client/ClientPacketHandlers.java', 490, 520, '地图投票/选边状态处理');
console.log('===== EspetroAuiWidgets 提供的控件 =====');
{
  const l = fs.readFileSync('src/main/java/org/espetro/client/gui/EspetroAuiWidgets.java', 'utf8').split('\n');
  for (let i = 0; i < l.length; i++) {
    if (/static .*\(/.test(l[i]) && !/private static final/.test(l[i])) console.log((i + 1) + ': ' + l[i].trim().slice(0, 150));
  }
}
