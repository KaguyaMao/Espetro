// patch-fort-overwrite.mjs — 工事放置不再被地图方块拒绝：落地直接顶掉占位方块
import fs from 'node:fs';

const FM = 'src/main/java/org/espetro/bastion/FortificationManager.java';
const FC = 'src/main/java/org/espetro/client/FortificationPlacementController.java';

function patchFile(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label] of edits) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次`); process.exitCode = 1; return false; }
    text = text.replace(f, to.replace(/\r?\n/g, eol));
    console.log(`  ✓ [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
  return true;
}

console.log('== 1) 服务端：确认放置时不再看地图方块 ==');
patchFile(FM, [
  [`    private static boolean spaceIsClear(ServerLevel level, List<WorldSlot> slots, ServerPlayer placer) {
        for (WorldSlot slot : slots) {
            BlockState state = level.getBlockState(slot.pos);
            if (!isReplaceable(state)) return false;
            if (!level.getEntities((Entity) null, new AABB(slot.pos), entity -> entity != placer
                && entity instanceof LivingEntity && entity.isAlive()).isEmpty()) return false;
        }
        return true;
    }`,
   `    /**
     * 放置空间是否可用。
     *
     * <p>地图方块不再阻止放置：工事落地时会直接顶掉占位方块（见 {@link #placeFinalBlocks}），
     * 因此这里只拦活体实体（放置者本人除外）。</p>
     */
    private static boolean spaceIsClear(ServerLevel level, List<WorldSlot> slots, ServerPlayer placer) {
        for (WorldSlot slot : slots) {
            if (!level.getEntities((Entity) null, new AABB(slot.pos), entity -> entity != placer
                && entity instanceof LivingEntity && entity.isAlive()).isEmpty()) return false;
        }
        return true;
    }`,
   'spaceIsClear 只看实体'],
  [`    private static boolean completionSpaceAvailable(ServerLevel level, Construction c) {
        for (WorldSlot slot : c.finalSlots) {
            BlockState state = level.getBlockState(slot.pos);
            boolean foundation = c.footprint.contains(slot.pos) && state.is(BastionItems.ON_BUILDING_BLOCK);
            if (!foundation && !isReplaceable(state)) return false;
            if (!level.getEntities((Entity) null, new AABB(slot.pos), entity -> entity instanceof LivingEntity
                && entity.isAlive()).isEmpty()) return false;
        }
        return true;
    }`,
   `    /** 完工空间是否可用：同样忽略地图方块，只拦活体实体。 */
    private static boolean completionSpaceAvailable(ServerLevel level, Construction c) {
        for (WorldSlot slot : c.finalSlots) {
            if (!level.getEntities((Entity) null, new AABB(slot.pos), entity -> entity instanceof LivingEntity
                && entity.isAlive()).isEmpty()) return false;
        }
        return true;
    }`,
   'completionSpaceAvailable 只看实体'],
  [`        for (BlockPos pos : c.footprint) {
            BlockState old = level.getBlockState(pos);
            if (!isReplaceable(old)) {
                restore(level, snapshots);
                return false;
            }
            snapshots.add(snapshot(level, pos));`,
   `        for (BlockPos pos : c.footprint) {
            // 不再因为地图方块而拒绝：直接顶掉占位方块。先做快照，任何失败都整体回滚。
            snapshots.add(snapshot(level, pos));`,
   'placeFoundations 顶掉方块'],
]);

console.log('== 2) 客户端：预览不再因地图方块变红 ==');
patchFile(FC, [
  [`            var state = mc.level.getBlockState(pos);
            // 与服务端 spaceIsClear/isReplaceable 一致：空气、雪层及一切可替换方块
            // （草、花、雪层、地毯等非完整方块）均可被工事覆盖
            if (!state.isAir() && !state.is(Blocks.SNOW) && !state.canBeReplaced()) clear = false;
            if (!mc.level.getEntities((Entity) null, new AABB(pos), entity -> entity != mc.player
                && entity instanceof LivingEntity && entity.isAlive()).isEmpty()) clear = false;`,
   `            // 与服务端一致：地图方块不再阻止放置（落地时直接顶掉占位方块），只拦活体实体
            if (!mc.level.getEntities((Entity) null, new AABB(pos), entity -> entity != mc.player
                && entity instanceof LivingEntity && entity.isAlive()).isEmpty()) clear = false;`,
   '预览只看实体'],
]);

console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');

// 自检：确认 isReplaceable 仍有其它使用者（避免变成死方法）/ 或已无使用者
if (!process.exitCode) {
  const fm = fs.readFileSync(FM, 'utf8');
  const uses = (fm.match(/isReplaceable\(/g) ?? []).length;
  console.log(`isReplaceable 在 FortificationManager 中剩余引用: ${uses} 处（含定义本身）`);
}
