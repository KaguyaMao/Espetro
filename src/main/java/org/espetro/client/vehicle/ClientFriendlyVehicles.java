package org.espetro.client.vehicle;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 客户端缓存：附近己方载具的 UUID 名单。
 *
 * <p>由服务端 {@code FriendlyVehicleSync} 每 0.5 秒对账下发，
 * 准心提示用它判断“朝向的这辆车是不是我方的”。</p>
 */
public final class ClientFriendlyVehicles {

    private static final Set<UUID> VEHICLES = new HashSet<>();

    private ClientFriendlyVehicles() {
    }

    public static void update(Collection<UUID> ids) {
        VEHICLES.clear();
        if (ids != null) {
            VEHICLES.addAll(ids);
        }
    }

    public static boolean contains(UUID id) {
        return id != null && VEHICLES.contains(id);
    }

    public static void clear() {
        VEHICLES.clear();
    }
}
