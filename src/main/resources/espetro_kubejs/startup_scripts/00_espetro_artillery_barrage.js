// 徐进弹幕炮击：在战术地图上按住右键拖出直线，弹幕沿该线逐轮推进。
// 本文件由 Espetro 首次启动时写入；可按 KubeJS startup_scripts 规则修改。
EspetroCommanderSkills.create('artillery_barrage')
  .displayName('徐进弹幕炮击')
  .description('在战术地图上按住右键拖出直线，弹幕沿直线逐轮推进')
  .stats('§8沿直线覆盖 | 冷却: 240秒')
  .icon('espetro:textures/gui/commander_skills/artillery_155.png')
  .usableBy('commander')
  .barrageTarget()
  .cooldownSeconds(240)
  .register()
