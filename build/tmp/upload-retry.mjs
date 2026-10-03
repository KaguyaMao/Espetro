// upload-retry.mjs — 循环重试上传直到 UPLOAD_OK（daemon 上传通道 ~0.14MB/s，偶发 ECONNRESET）
//
// 用法:
//   node upload-retry.mjs                    默认上传 build/libs 下最新的 espetro-*.jar 到 mods
//   node upload-retry.mjs <本地文件> <目标名> [目标目录] [最大尝试次数=12]
//
// 说明: 旧版本把 jar 路径硬编码成 espetro-1.1.3-k.jar，版本更新后会传错文件，故改为参数化 + 自动取最新。
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const { execFileSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const TMP_DIR = 'D:/minecraft/modp/Espetro/build/tmp';
const LIBS_DIR = 'D:/minecraft/modp/Espetro/build/libs';
const UPLOAD_OUT = path.join(TMP_DIR, 'upload-out.txt');
const RETRY_LOG = path.join(TMP_DIR, 'upload-retry-log.txt');

function newestEspetroJar() {
  const files = fs.readdirSync(LIBS_DIR)
    .filter((n) => /^espetro-.*\.jar$/.test(n))
    .map((n) => ({ n, t: fs.statSync(path.join(LIBS_DIR, n)).mtimeMs }))
    .sort((a, b) => b.t - a.t);
  if (!files.length) throw new Error('build/libs 下没有 espetro-*.jar');
  return files[0].n;
}

const LOCAL = process.argv[2] ?? path.join(LIBS_DIR, newestEspetroJar());
const NAME = process.argv[3] ?? path.basename(LOCAL);
const UPLOAD_DIR = process.argv[4] ?? 'mods';
const MAX_ATTEMPTS = Number(process.argv[5] ?? 12);
const log = [];

(async () => {
  log.push(`local=${LOCAL} target=${NAME} dir=${UPLOAD_DIR} maxAttempts=${MAX_ATTEMPTS}`);
  for (let attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
    log.push('===== attempt ' + attempt + ' @ ' + new Date().toLocaleTimeString() + ' =====');
    try {
      execFileSync('node', [path.join(TMP_DIR, 'upload-v2.mjs'), LOCAL, NAME, UPLOAD_DIR], { timeout: 600000 });
    } catch (e) {
      // upload-v2 失败时退出码非 0，细节在其日志里
    }
    let txt = '';
    try { txt = fs.readFileSync(UPLOAD_OUT, 'utf8'); } catch (e) {}
    log.push('result: ' + (txt.split('\n').filter(Boolean).slice(-6).join(' | ') || '(no log)'));
    if (txt.includes('UPLOAD_OK')) {
      log.push('SUCCESS on attempt ' + attempt);
      break;
    }
    await new Promise((r) => setTimeout(r, 4000));
  }
  fs.writeFileSync(RETRY_LOG, log.join('\n'), 'utf8');
  const ok = log.join('\n').includes('SUCCESS');
  console.log(ok ? `UPLOAD SUCCESS -> ${UPLOAD_DIR}/${NAME}` : `UPLOAD FAILED after ${MAX_ATTEMPTS} attempts`);
  process.exit(ok ? 0 : 1);
})();
