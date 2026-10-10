// patch-enemy-fob-lock.mjs — 敌方不得与己方 FOB 的方块/实体做任何交互（拆除除外）
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

console.log('== 1) FortificationManager：归属查询 + 敌我判定 ==');
patch(FM, [[
  `    public boolean containsEntity(UUID entityId) {
        return entityId != null && entityIndex.containsKey(entityId);
    }`,
  `    public boolean containsEntity(UUID entityId) {
        return entityId != null && entityIndex.containsKey(entityId);
    }

    /**
     * 该位置工事的归属队伍（已是归一化写法）；不属于任何工事时返回 {@code null}。
     */
    @Nullable
    public String teamAt(@Nullable ServerLevel level, @Nullable BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }
        UUID id = positionIndex.get(posKey(level, pos));
        Construction c = id == null ? null : constructions.get(id);
        return c == null ? null : c.team;
    }

    /**
     * 该实体所属工事的队伍：结构实体查索引，工事生成的载具补给站读 PersistentData。
     * 无归属时返回 {@code null}。
     */
    @Nullable
    public String teamOfEntity(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }
        UUID id = entityIndex.get(entity.getUUID());
        Construction c = id == null ? null : constructions.get(id);
        if (c != null) {
            return c.team;
        }
        CompoundTag data = entity.getPersistentData();
        if (data.contains(VehicleManager.SUPPLY_STATION_TEAM_KEY)) {
            String team = data.getString(VehicleManager.SUPPLY_STATION_TEAM_KEY);
            return team == null || team.isBlank() ? null : team;
        }
        return null;
    }

    /**
     * 玩家是否是给定工事队伍以外的敌方。
     *
     * <p>无队伍的玩家（旁观者/管理员）一律按"非敌方"处理，避免影响观战与地图编辑。</p>
     */
    public static boolean isEnemyOf(@Nullable ServerPlayer player, @Nullable String ownerTeam) {
        if (player == null || ownerTeam == null || ownerTeam.isBlank()) {
            return false;
        }
        String myTeam = normalizeTeam(Espetro.getPlayerTeam(player));
        return myTeam != null && !myTeam.equals(ownerTeam);
    }`,
  '归属查询 + isEnemyOf',
]]);

console.log('== 2) FortificationEventHandler：拦截敌方一切交互 ==');
patch(FH, [[
  `    @SubscribeEvent
    public static void onBastionBuilt(BastionLifecycleEvent.Built event) {`,
  `    /**
     * 敌方不得与己方工事的<strong>方块</strong>做任何交互（右键使用、开关、容器等都拦掉）。
     *
     * <p>拆除不算交互：破坏方块走 {@link #onBlockBroken} 的完整度伤害流程，
     * 铁镐的左/右键施工由 {@link #guardShovelWork} 处理，都不受这里影响。</p>
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEnemyInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()
            || !(event.getLevel() instanceof ServerLevel level)
            || !(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
            return;
        }
        String owner = FortificationManager.getInstance().teamAt(level, event.getPos());
        if (!FortificationManager.isEnemyOf(player, owner)) {
            return;
        }
        denyEnemyInteraction(event, player);
    }

    /** 敌方不得与己方工事的<strong>实体</strong>交互（结构实体、载具补给站等）。 */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEnemyInteractEntity(PlayerInteractEvent.EntityInteract event) {
        guardEnemyEntityInteraction(event, event.getTarget());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEnemyInteractEntitySpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        guardEnemyEntityInteraction(event, event.getTarget());
    }

    private static void guardEnemyEntityInteraction(PlayerInteractEvent event,
                                                    net.minecraft.world.entity.Entity target) {
        if (event.getLevel().isClientSide()
            || !(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) {
            return;
        }
        String owner = FortificationManager.getInstance().teamOfEntity(target);
        if (!FortificationManager.isEnemyOf(player, owner)) {
            return;
        }
        denyEnemyInteraction(event, player);
    }

    private static void denyEnemyInteraction(PlayerInteractEvent event,
                                             net.minecraft.server.level.ServerPlayer player) {
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
            "§c这是敌方工事，无法交互（只可拆除）。"), true);
    }

    @SubscribeEvent
    public static void onBastionBuilt(BastionLifecycleEvent.Built event) {`,
  '敌方交互拦截',
]]);

console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
