# extract-veh-json.ps1 — 从所有 mods jar 中提取 data/*/sbw/vehicles/*.json
param(
  [string]$Mods = "D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods",
  [string]$Out  = "D:\minecraft\modp\Espetro\build\tmp\vehjars"
)
Add-Type -AssemblyName System.IO.Compression.FileSystem
if (Test-Path $Out) { Remove-Item $Out -Recurse -Force }
New-Item -ItemType Directory -Force -Path $Out | Out-Null

$total = 0
foreach ($jar in Get-ChildItem $Mods -Filter *.jar) {
  $zip = $null
  try { $zip = [System.IO.Compression.ZipFile]::OpenRead($jar.FullName) } catch { Write-Output ("SKIP " + $jar.Name + " (打开失败)"); continue }
  $hits = @()
  foreach ($e in $zip.Entries) {
    if ($e.FullName -match '^data/([^/]+)/sbw/vehicles/(.+\.json)$') { $hits += $e }
  }
  if ($hits.Count -eq 0) { $zip.Dispose(); continue }
  $dir = Join-Path $Out $jar.Name
  New-Item -ItemType Directory -Force -Path $dir | Out-Null
  foreach ($e in $hits) {
    # 展平：ns__文件名.json
    $flat = ($e.FullName -replace '^data/', '' -replace '/sbw/vehicles/', '__')
    $dest = Join-Path $dir $flat
    [System.IO.Compression.ZipFileExtensions]::ExtractToFile($e, $dest, $true)
  }
  $zip.Dispose()
  Write-Output ($jar.Name + " -> " + $hits.Count + " 个载具 JSON")
  $total += $hits.Count
}
Write-Output ("合计 " + $total + " 个 -> " + $Out)
