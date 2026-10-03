// 00_esveh_track_steer.js — 一侧履带/车轮被击毁后，禁止朝"该侧反方向"转向
//   左侧履带报废 → D（右转）无效
//   右侧履带报废 → A（左转）无效
//
// 说明：SBW 0.8.9.1 对履带损坏只做动力惩罚（trackEngine：单侧损坏 ×0.975，双侧 ×0.93），
//       转向输入（LEFT_INPUT_DOWN / RIGHT_INPUT_DOWN，即 A/D）不受影响。
//       本脚本每 tick 检查驾驶中的载具：若某侧履带 damaged，则把"需要该侧履带出力"的那个
//       转向输入强制置 false（服务器权威的 SynchedEntityData，客户端会同步到同一状态）。
//
// 开关：把 ESVEH_TRACK_STEER 改成 false 即可整体关闭（文件保留、不生效）

var ESVEH_TRACK_STEER = true
var esvehSteerErrStamp = 0

function esvehLimitSteer(vehicle) {
  var changed = false
  // 左履带报废 → 无法右转（右转需要左侧履带出力）
  if (vehicle.getLeftWheelDamaged() && vehicle.rightInputDown()) {
    vehicle.setRightInputDown(false)
    changed = true
  }
  // 右履带报废 → 无法左转
  if (vehicle.getRightWheelDamaged() && vehicle.leftInputDown()) {
    vehicle.setLeftInputDown(false)
    changed = true
  }
  return changed
}

function esvehSteerTick(player) {
  var ride = player.getVehicle()
  if (!ride) return
  // 非载具会抛错，借此过滤
  ride.getLeftWheelMaxHealth()
  esvehLimitSteer(ride)
}

PlayerEvents.tick(event => {
  if (!ESVEH_TRACK_STEER) return
  if (!event.player) return
  try {
    esvehSteerTick(event.player)
  } catch (err) {
    if (Date.now() - esvehSteerErrStamp > 60000) {
      esvehSteerErrStamp = Date.now()
      console.error('[esveh_steer] ' + err)
    }
  }
})
