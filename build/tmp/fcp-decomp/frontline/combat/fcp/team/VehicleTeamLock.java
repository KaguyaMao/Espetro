/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.scores.Team
 *  net.minecraftforge.registries.ForgeRegistries
 */
package frontline.combat.fcp.team;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Team;
import net.minecraftforge.registries.ForgeRegistries;

public final class VehicleTeamLock {
    public static final String TEAM_KEY = "fcp_team";

    private VehicleTeamLock() {
    }

    public static String getTeam(VehicleEntity vehicle) {
        CompoundTag data = vehicle.getPersistentData();
        if (data.m_128425_(TEAM_KEY, 8)) {
            String s = data.m_128461_(TEAM_KEY);
            return s.isEmpty() ? null : s;
        }
        return null;
    }

    public static void setTeam(VehicleEntity vehicle, String teamName) {
        vehicle.getPersistentData().m_128359_(TEAM_KEY, teamName);
    }

    public static void clearTeam(VehicleEntity vehicle) {
        vehicle.getPersistentData().m_128473_(TEAM_KEY);
    }

    public static String teamNameOf(Entity entity) {
        Team team = entity.m_5647_();
        return team == null ? null : team.m_5758_();
    }

    public static boolean isFcpVehicle(VehicleEntity vehicle) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        return key != null && key.m_135827_().equals("fcp");
    }
}

