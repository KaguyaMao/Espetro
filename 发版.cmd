@echo off
chcp 65001 >nul
cd /d "%~dp0"
if "%~1"=="" (
  echo 正在自动选择下一个版本号并发布...
  node "build\tmp\mcpatch\release.mjs"
) else (
  node "build\tmp\mcpatch\release.mjs" %1
)
echo.
echo 按任意键关闭窗口...
pause >nul
