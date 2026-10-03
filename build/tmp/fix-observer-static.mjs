// fix-observer-static.mjs — 静态方法内不能直接读非静态 currentPhase
import fs from 'node:fs';
const F = 'src/main/java/org/espetro/team/GameStateManager.java';
let text = fs.readFileSync(F, 'utf8');
const eol = text.includes('\r\n') ? '\r\n' : '\n';
const from = [
  '        GameStateManager manager = getInstance();',
  '        if (manager == null || !manager.isObserver(player.getUUID())) {',
  '            return false;',
  '        }',
  '        return currentPhase == GamePhase.BATTLE || currentPhase == GamePhase.ROUND_END;',
].join(eol);
const to = [
  '        GameStateManager manager = getInstance();',
  '        if (manager == null || !manager.isObserver(player.getUUID())) {',
  '            return false;',
  '        }',
  '        GamePhase phase = manager.currentPhase;',
  '        return phase == GamePhase.BATTLE || phase == GamePhase.ROUND_END;',
].join(eol);
if (text.split(from).length - 1 !== 1) { console.error('❌ 锚点未唯一匹配'); process.exit(1); }
fs.writeFileSync(F, text.replace(from, to), 'utf8');
console.log('✓ 已修正（改用 manager.currentPhase）');
