// dump-menu-screen.mjs — 看 EspetroMenuScreen 的输入处理与菜单结构
import fs from 'node:fs';
const P = 'src/main/java/org/espetro/client/gui/EspetroMenuScreen.java';
console.log('行数=' + fs.readFileSync(P, 'utf8').split('\n').length);
const l = fs.readFileSync(P, 'utf8').split('\n');
for (let i = 0; i < l.length; i++) {
  const s = l[i];
  if (/class |protected void init|public void init|keyPressed|charTyped|mouseClicked|mouseReleased|mouseScrolled|addRenderableWidget|removeWidget|clearWidgets|super\.|GuiElement root|buildMenuRoot|render\(|renderBeforeMenu|AuiScreen|AuraTip|isPauseScreen|extends/.test(s)) {
    console.log((i + 1) + ': ' + s.trim().slice(0, 165));
  }
}
