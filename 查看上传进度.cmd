@echo off
chcp 65001 >nul
title McPatch 1.tar upload progress
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "build\tmp\mcpatch\progress.ps1" %*
echo.
pause
