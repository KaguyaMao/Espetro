// Temporary offline UI probe. Remove this file after the rendering test.
var ProbeVote = Java.loadClass('org.espetro.client.gui.CommanderVoteScreen');
var ProbeHost = Java.loadClass('org.espetro.client.aui.AuiScreen');
var ProbeState = Java.loadClass('org.espetro.client.gui.ClientGameState');
var ProbeShot = Java.loadClass('net.minecraft.client.Screenshot');
var ProbeList = Java.loadClass('java.util.ArrayList');
var ProbeEvents = Java.loadClass('dev.architectury.event.events.client.ClientTickEvent');
var ProbeListener = Java.loadClass('dev.architectury.event.events.client.ClientTickEvent$Client');
var ProbeTitle = Java.loadClass('net.minecraft.client.gui.screens.TitleScreen');
var probeStage = 0;
var probeTicks = 0;
function takeProbe(name, mc) {
  ProbeShot.grab(mc.gameDirectory, name, mc.getMainRenderTarget(), message => {});
  
}
function makeVote(team, count) {
  var names = new ProbeList();
  for (var i = 0; i < count; i++) names.add('TestPlayer' + (i + 1));
  var screen = new ProbeVote(team, names, 30, null, null, 0);
  screen.setTutorialPreviewMode(true);
  return screen;
}
ProbeEvents.CLIENT_POST.register(new JavaAdapter(ProbeListener, {tick: function(mc) {
  if (probeStage >= 3) return;
  try {
    if (probeStage == 0) {
      if (mc.screen instanceof ProbeTitle && mc.getOverlay() == null) {
        ProbeState.setCurrentMapFolder('CREATE_PLUS');
        ProbeHost.openWithFade(makeVote('ATTACK', 8), mc.screen);
        probeStage = 1; probeTicks = 0;
        
      }
      return;
    }
    probeTicks++;
    if (probeStage == 1) {
      if (probeTicks == 2) takeProbe('espetro-motion-enter.png', mc);
      if (probeTicks == 12) takeProbe('espetro-motion-full.png', mc);
      if (probeTicks == 16) {
        ProbeHost.openWithFade(makeVote('DEFEND', 1), mc.screen);
        probeStage = 2; probeTicks = 0;
      }
    } else if (probeStage == 2) {
      if (probeTicks == 1) takeProbe('espetro-motion-exit.png', mc);
      if (probeTicks == 12) takeProbe('espetro-motion-single.png', mc);
      if (probeTicks == 18) { probeStage = 3;}
    }
  } catch (failure) {probeStage = 99;console.error('[ESPECTRO_MOTION_PROBE] FAILED ' + failure);}
}}));
