# 离线 UI 渲染检查

临时脚本仅用于真实客户端的截图检查，已从客户端 `kubejs/client_scripts` 移走，不属于发布模组。

最终日志：`build/ui-motion-runtime-5.log`。截图：`design/ui-motion-2026-11-14/`。

这里的 `crash-2026-11-14_10.56.58-client.txt` 是第三次测试脚本误用 SRG 名称引发的错误。后续改为 Rhino 暴露的官方映射 API 并移除冲突的 console.info 后，真实客户端启动、页面切换和截图检查通过。正式 jar 不包含该脚本。
