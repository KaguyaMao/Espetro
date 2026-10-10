Espetro 工事模板目录（管理员用『工事选定棒』导出）
----------------------------------------------------------------
* 每个模板是一个标准 Minecraft 结构 NBT（gzip）：<名字>.nbt
* 同名的 <名字>.meta.json 记录作者/时间/选区/方块数/实体数，供 /espetro fort list 显示
* 引用方式：fortifications.json 里 "template": "espetro:fortifications/<名字>"
* 本目录优先于数据包/内置模板；改名或删除不会影响 fortifications.json 里的定义
* 游戏内流程：/espetro fort wand -> 选两角 -> 潜行右键设锚点 -> /espetro fort save <名字> -> /espetro fort reload
