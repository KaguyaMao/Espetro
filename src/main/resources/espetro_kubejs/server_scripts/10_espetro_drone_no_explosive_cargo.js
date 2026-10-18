// Espetro：禁止 Superb Warfare 无人机挂载 C4、RPG 弹药、手雷、迫击炮弹、地雷等爆炸物。
//
// 原理：SBW 的无人机（superbwarfare:drone）只有一处“载货”逻辑 —— 玩家手持物品右键无人机时，
//       DroneEntity#interact 会去查挂载数据表（CustomData.DRONE_ATTACHMENT）并消耗手里的物品。
//       这里在实体交互事件（PlayerInteractEvent.EntityInteract）里直接把交互取消，
//       挂载逻辑根本不会执行，物品也不会被消耗。
//
// 注意：
//   1. 已经挂载了弹药的无人机不受影响 —— 空手右键可以逐发把弹药取回，潜行 + 撬棍可以整机拆回。
//   2. 医疗包（superbwarfare:medical_kit）不在禁止列表内，需要一起禁的话把它加进下面的数组即可。
//   3. 改完脚本后执行 /kubejs reload server_scripts 生效（重启服务器同样生效）。

// 无人机实体 id
const DRONE_ENTITY_ID = 'superbwarfare:drone'

// 无人机禁止携带的物品 id 列表（按需增删）
const DRONE_BLOCKED_ITEMS = [
  'superbwarfare:c4_bomb',             // C4 炸弹
  'superbwarfare:rpg_rocket_standard', // RPG 火箭弹（标准）
  'superbwarfare:rpg_rocket_tbg',      // RPG 火箭弹（TBG）
  'superbwarfare:rgo_grenade',         // RGO 手雷
  'superbwarfare:grenade_40mm',        // 40mm 榴弹
  'superbwarfare:mortar_shell',        // 迫击炮弹
  'superbwarfare:tm_62',               // TM-62 反坦克地雷
  'superbwarfare:blu_43_mine'          // BLU-43 蝴蝶雷
]

// 提示消息冷却（毫秒），防止玩家狂点右键刷屏
const DRONE_BLOCK_MESSAGE_COOLDOWN_MS = 3000

ItemEvents.entityInteracted(event => {
  const target = event.target
  if (target == null || String(target.type) !== DRONE_ENTITY_ID) return

  const item = event.item
  if (item == null || item.empty) return

  const itemId = String(item.id)
  if (!DRONE_BLOCKED_ITEMS.includes(itemId)) return

  // 取消交互：SBW 不会挂载，物品也不会被扣掉
  event.cancel()

  // 以下只有音效和提示，出错也不影响拦截本身
  try {
    const player = event.player
    if (player == null) return

    player.playSound('minecraft:entity.villager.no', 0.7, 0.9)

    const data = player.persistentData
    const now = Date.now()
    if (now - data.getLong('espetro_drone_block_msg') < DRONE_BLOCK_MESSAGE_COOLDOWN_MS) return
    data.putLong('espetro_drone_block_msg', now)
    player.tell('§c这架无人机不能携带爆炸物类弹药（C4 / 火箭弹 / 手雷 / 迫击炮弹 / 地雷）。')
  } catch (err) {
    console.warn('[Espetro] drone cargo block feedback failed: ' + err)
  }
})
