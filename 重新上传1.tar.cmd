@echo off
chcp 65001 >nul
title McPatch 1.tar upload
cd /d "%~dp0"
echo Uploading public/1.tar (1381 MB) ... Ctrl+C to abort.
echo.
node build\tmp\mcpatch\upload_big.mjs build\tmp\mcpatch\public\1.tar public/1.tar
echo.
echo Finished. Now run the index swap step.
pause
