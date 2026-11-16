package org.espetro.vehicle;

import org.espetro.Espetro;

/**
 * 抢滩登陆「前哨载具出生点」的全局开关。
 *
 * <p>地图在 {@code VehSpawn.json} 里用 {@code outpost_spawn_points} 声明前哨点，并用
 * {@code outpost_activate_after_batch}（默认 1）声明「第几批据点被完全占领后」启用。
 * 首批据点完成后由 EsPoints 通过 {@code EspetroAPI.onCaptureBatchCompleted} 回调，
 * 这里只记一个批次号；{@link org.espetro.vehicle.VehicleManager} 在选出生点时查询。</p>
 */
public final class OutpostSpawnGate {

    /** 已解锁的最大批次号；0 表示尚未解锁任何前哨点。 */
    private static volatile int unlockedBatch;

    private OutpostSpawnGate() {
    }

    /** 开图 / 换图时清空。 */
    public static void reset() {
        unlockedBatch = 0;
    }

    /** 某一批据点全部被占领。 */
    public static void onBatchCompleted(int batch) {
        if (batch <= 0) {
            return;
        }
        int previous = unlockedBatch;
        unlockedBatch = Math.max(unlockedBatch, batch);
        if (unlockedBatch != previous) {
            Espetro.LOGGER.info("抢滩前哨载具出生点已解锁: 批次 {}", unlockedBatch);
        }
    }

    public static boolean isUnlocked(int requiredBatch) {
        return unlockedBatch >= Math.max(1, requiredBatch);
    }

    public static int unlockedBatch() {
        return unlockedBatch;
    }
}
