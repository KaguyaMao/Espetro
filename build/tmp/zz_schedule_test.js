// zz_schedule_test.js — 临时诊断：scheduleInTicks 长延迟回调是否可靠（用后即删）
var ScheduleTestArmed = false
ServerEvents.tick(function (evt) {
  if (ScheduleTestArmed) return
  ScheduleTestArmed = true
  try {
    var stSrv = evt.server
    stSrv.scheduleInTicks(20, function () { console.info('[ScheduleTest] 20-tick (1s) FIRED') })
    stSrv.scheduleInTicks(200, function () { console.info('[ScheduleTest] 200-tick (10s) FIRED') })
    stSrv.scheduleInTicks(600, function () { console.info('[ScheduleTest] 600-tick (30s) FIRED') })
    stSrv.scheduleInTicks(1400, function () { console.info('[ScheduleTest] 1400-tick (70s) FIRED') })
    console.info('[ScheduleTest] scheduled 20/200/600/1400 ticks')
  } catch (stErr) {
    console.error('[ScheduleTest] scheduling failed: ' + stErr)
  }
})
