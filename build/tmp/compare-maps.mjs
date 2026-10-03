// compare-maps.mjs — 三张地图：兵票 / FOB(电台)范围 / 准备时间 / 战局限时 全量对比
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const https = require('https');
const PANEL = 'https://www.derpydoge.fun:20000';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const MAPS = ['server_battlefield', '越南', 'CREATE_PLUS'];
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
function req(method, urlStr, headers, body, getCookie) {
  return new Promise((resolve, reject) => {
    const url = new URL(urlStr);
    const r = https.request({ method, hostname: url.hostname, port: url.port || 443, path: url.pathname + url.search, headers, rejectUnauthorized: false }, (res) => {
      let d = ''; res.on('data', (c) => (d += c));
      res.on('end', () => resolve({ status: res.statusCode, body: d, cookie: getCookie ? (res.headers['set-cookie'] ?? []).map((l) => l.split(';')[0]).join('; ') : undefined }));
    });
    r.on('error', reject); if (body !== undefined) r.write(body); r.end();
  });
}
const lr = await req('POST', `${PANEL}/api/auth/login`, { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, JSON.stringify({ username: 'boy', password: 'boY1145141919810Fuck' }), true);
const token = String(JSON.parse(lr.body).data ?? '');
const cookie = lr.cookie ?? '';
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
async function read(target) {
  for (let a = 0; a < 4; a++) {
    const r = await req('PUT', `${PANEL}/api/files?${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, JSON.stringify({ target }));
    try { const b = JSON.parse(r.body); if (b.status === 200) return String(b.data ?? ''); } catch { }
    await sleep(2600);
  }
  return null;
}
async function list(target) {
  const r = await req('GET', `${PANEL}/api/files/list?page=0&page_size=200&file_name=&target=${encodeURIComponent(target)}&${q}`, { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie });
  try { return (JSON.parse(r.body).data?.items ?? []).map((i) => String(i.name)); } catch { return []; }
}
const J = (t) => { try { return JSON.parse(t); } catch { return null; } };
const out = {};
for (const map of MAPS) {
  const base = `EsWorld/${map}/EsConfig`;
  const rec = { presets: {} };
  rec.game = J(await read(`${base}/game.json`)); await sleep(2600);
  rec.logistics = J(await read(`${base}/logistics.json`)); await sleep(2600);
  rec.bastion = J(await read(`${base}/bastion.json`)); await sleep(2600);
  rec.legacy = J(await read(`${base}/CapturePoints.json`)); await sleep(2600);
  const pfiles = (await list(`EsWorld/${map}/Points`)).filter((n) => n.endsWith('.json'));
  await sleep(2600);
  for (const p of pfiles) {
    rec.presets[p] = J(await read(`EsWorld/${map}/Points/${p}`));
    await sleep(2600);
  }
  out[map] = rec;
}
fs.writeFileSync('map-compare.json', JSON.stringify(out, null, 2), 'utf8');
console.log('原始数据已存 map-compare.json\n');

for (const map of MAPS) {
  const r = out[map];
  console.log('════════ ' + map + ' ════════');
  const g = r.game ?? {};
  const t = g.troops ?? {};
  console.log('【兵票 game.json】初始 攻' + t.initial_attack + ' / 守' + t.initial_defend
    + '，指挥官阵亡扣 ' + t.commander_death_penalty);
  const lg = r.logistics?.logistics ?? r.logistics ?? {};
  const radio = lg.radio ?? {};
  console.log('【FOB/电台范围】建造半径 ' + (lg.radio_build_radius ?? '?')
    + '，排除半径 ' + (lg.radio_exclusion_radius ?? '?')
    + '，deposit_radius ' + (lg.deposit_radius ?? '?')
    + '，队友半径 ' + (lg.radio_teammate_radius ?? '?'))
  console.log('              radio.*: ' + JSON.stringify(radio));
  const b = r.bastion?.bastion ?? r.bastion ?? {};
  console.log('【兵站 bastion】' + JSON.stringify(b));
  const gg = g.game ?? {};
  console.log('【准备/阶段时间】选边 ' + gg.team_select_seconds + 's，部署 ' + gg.deploy_timeout_seconds
    + 's（剩余 ' + gg.deploy_warning_seconds + 's 警告），攻方指挥官投票 ' + gg.attack_commander_vote_seconds
    + 's / 守方 ' + gg.defend_commander_vote_seconds + 's，攻方阵营选择 ' + gg.attack_faction_select_seconds
    + 's / 守方 ' + gg.defend_faction_select_seconds + 's，阵营揭示 ' + gg.faction_reveal_seconds
    + 's，结算 ' + gg.round_end_seconds + 's');
  const others = Object.entries(gg).filter(([k]) => !/^(team_select|deploy_|attack_commander|defend_commander|attack_faction|defend_faction|faction_reveal|round_end)/.test(k));
  if (others.length) console.log('              其它 game 键: ' + JSON.stringify(Object.fromEntries(others)));
  const tro = Object.entries(g).filter(([k]) => k !== 'game' && k !== 'troops');
  if (tro.length) console.log('              其它段: ' + tro.map(([k, v]) => k + '=' + JSON.stringify(v)).join('  '));
  // 兵票：预设
  console.log('【兵票 CapturePoints 预设】');
  for (const [name, j] of Object.entries(r.presets)) {
    if (!j) { console.log('   ' + name + ': 解析失败'); continue; }
    console.log('   ' + name.padEnd(24) + '兵力 ' + JSON.stringify(j.teamReinforcements)
      + '  批次奖励 ' + (j.attackBatchCompletionReinforcement ?? '(未设)')
      + '  批次数 ' + j.totalBatches + '  结束 ' + j.endBehavior + '  点位数 ' + (j.plannedPoints?.length ?? 0));
  }
  if (r.legacy) console.log('   ' + 'EsConfig/CapturePoints.json'.padEnd(24) + '兵力 ' + JSON.stringify(r.legacy.teamReinforcements)
    + '  批次奖励 ' + (r.legacy.attackBatchCompletionReinforcement ?? '(未设)')
    + '  批次数 ' + r.legacy.totalBatches + '  结束 ' + r.legacy.endBehavior);
  else console.log('   EsConfig/CapturePoints.json: 缺失');
  console.log('');
}
