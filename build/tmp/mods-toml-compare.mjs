import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');
const { execSync } = require('child_process');

// 提取 jar 内 META-INF/mods.toml 并打印依赖版本范围，用于对比新旧 jar
const jars = process.argv.slice(2);
const tmp = 'D:/minecraft/modp/Espetro/build/tmp/mods-toml-compare';
fs.mkdirSync(tmp, { recursive: true });

for (const jar of jars) {
  const out = `${tmp}/${require('path').basename(jar)}.toml`;
  // 用 PowerShell 的 Expand-Archive 只解出该条目
  const cmd = `powershell -NoProfile -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; ` +
    `$z=[System.IO.Compression.ZipFile]::OpenRead('${jar}'); ` +
    `$e=$z.Entries | Where-Object { $_.FullName -eq 'META-INF/mods.toml' }; ` +
    `[System.IO.Compression.ZipFileExtensions]::ExtractToFile($e, '${out}', $true); $z.Dispose()"`;
  execSync(cmd, { stdio: 'ignore' });
  const text = fs.readFileSync(out, 'utf8');
  const deps = [];
  let current = null;
  for (const line of text.split('\n')) {
    const modId = line.match(/^modId\s*=\s*"([^"]+)"/);
    const range = line.match(/^versionRange\s*=\s*"([^"]+)"/);
    if (line.trim().startsWith('[[') && line.includes('dependencies')) { current = null; }
    if (modId) current = { id: modId[1] };
    if (range && current) { current.range = range[1]; deps.push(current); current = null; }
  }
  const version = (text.match(/^version\s*=\s*"([^"]+)"/m) || [])[1];
  console.log(`═══ ${require('path').basename(jar)}   (espetro version=${version})`);
  for (const d of deps) console.log(`   ${d.id.padEnd(18)} ${d.range}`);
}
