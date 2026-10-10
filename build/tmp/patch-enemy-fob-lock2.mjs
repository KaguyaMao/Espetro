// patch-enemy-fob-lock2.mjs — 修正：放行铁镐施工路径；敌方只能拆不能修
import fs from 'node:fs';

function patch(file, pairs) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label] of pairs) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次`); process.exitCode = 1; return; }
    text = text.replace(f, to.replace(/\r?\n/g, eol));
    console.log(`  ✓ [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
}

const FM = 'src/main/java/org/espetro/bastion/FortificationManager.java';
const FH = 'src/main/java/org/espetro/bastion/FortificationEventHandler.java';

console.log('== 1) 交互拦截：铁镐（施工/拆除）路径放行 ==');
patch(FH, [
  [`    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEnemyInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()
            || !(event.getLevel() instanceof ServerLevel level)
            || !(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
            return;
        }`,
   `    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEnemyInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()
            || !(event.getLevel() instanceof ServerLevel level)
            || !(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
            return;
        }
        // 铁镐是施工工具：右键=拆除、左键=修复，交给 guardShovelWork / work 流程处理，
        // 这里不能拦，否则敌方无法拆除己方工事。
        if (player.getMainHandItem().getItem() == Items.IRON_SHOVEL) {
            return;
        }`,
   '铁镐放行'],
]);

console.log('== 2) 服务端施工：敌方只能拆(build=false)、不能修/建(build=true) ==');
patch(FM, [
  [`        UUID constructionId = positionIndex.get(posKey(level, target));
        Construction construction = constructionId == null ? null : constructions.get(constructionId);
        if (construction == null) return;
        applyWork(player, level, construction, build);`,
   `        UUID constructionId = positionIndex.get(posKey(level, target));
        Construction construction = constructionId == null ? null : constructions.get(constructionId);
        if (construction == null) return;
        if (!canWork(player, construction, build)) return;
        applyWork(player, level, construction, build);`,
   'work 权限判定'],
  [`        Entity entity = level.getEntity(target);
        if (construction == null || entity == null || player.distanceToSqr(entity) > 49.0D
            || !player.hasLineOfSight(entity)) return;
        applyWork(player, level, construction, build);`,
   `        Entity entity = level.getEntity(target);
        if (construction == null || entity == null || player.distanceToSqr(entity) > 49.0D
            || !player.hasLineOfSight(entity)) return;
        if (!canWork(player, construction, build)) return;
        applyWork(player, level, construction, build);`,
   'workEntity 权限判定'],
  [`    private void applyWork(ServerPlayer player, ServerLevel level, Construction construction,
                           boolean build) {`,
   `    /**
     * 施工权限：己方可以修/建也可以拆；敌方只能拆（{@code build == false}）。
     *
     * <p>无队伍玩家（旁观者/管理员）不受限制，便于观战与地图编辑。</p>
     */
    private static boolean canWork(ServerPlayer player, Construction construction, boolean build) {
        if (construction == null) return false;
        if (!build) return true; // 拆除：敌我皆可
        if (!isEnemyOf(player, construction.team)) return true;
        player.displayClientMessage(
            Component.literal("§c这是敌方工事，只能拆除，不能修复或继续建造。"), true);
        return false;
    }

    private void applyWork(ServerPlayer player, ServerLevel level, Construction construction,
                           boolean build) {`,
   'canWork 定义'],
]);

console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
