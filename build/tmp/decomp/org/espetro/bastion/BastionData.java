/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.bastion;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.espetro.api.EspetroAPI;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.StructureKind;

public class BastionData {
    private final UUID bastionId;
    private final String team;
    private String name;
    private final BlockPos position;
    private final ServerLevel level;
    private StructureKind kind = StructureKind.RADIO;
    private UUID armorStandId;
    private int bastionNumber = -1;
    private float coreHealth;
    @Nullable
    private BlockPos armorStandPosition;
    @Nullable
    private BlockPos shulkerPos;
    private boolean active;
    private int constructionSupplies;
    private int ammunitionSupplies;
    private boolean habBuilt;
    private boolean ammoCrateBuilt;
    private long habAvailableAt;
    private long habDisabledUntil;
    private boolean legacyCombined;
    private transient boolean habCoveredCache = true;

    public boolean isHabCoveredCache() {
        return this.habCoveredCache;
    }

    public void setHabCoveredCache(boolean habCoveredCache) {
        if (this.habCoveredCache == habCoveredCache) {
            return;
        }
        this.habCoveredCache = habCoveredCache;
        BastionData.markTacticalDirty();
    }

    public BastionData(String team, String name, BlockPos position, ServerLevel level) {
        this(UUID.randomUUID(), team, name, position, level, StructureKind.RADIO);
    }

    public BastionData(String team, String name, BlockPos position, ServerLevel level, StructureKind kind) {
        this(UUID.randomUUID(), team, name, position, level, kind);
    }

    public BastionData(UUID bastionId, String team, String name, BlockPos position, ServerLevel level) {
        this(bastionId, team, name, position, level, StructureKind.RADIO);
    }

    public BastionData(UUID bastionId, String team, String name, BlockPos position, ServerLevel level, StructureKind kind) {
        this.bastionId = bastionId;
        this.team = team;
        this.name = name;
        this.position = position;
        this.level = level;
        this.kind = kind == null ? StructureKind.RADIO : kind;
        this.armorStandPosition = position.m_7494_();
        this.active = true;
        this.coreHealth = BastionManager.getInstance().getArmorStandHealth();
        if (this.kind == StructureKind.HAB) {
            this.habBuilt = true;
        }
    }

    public StructureKind getKind() {
        return this.kind;
    }

    public void setKind(StructureKind kind) {
        StructureKind resolved;
        StructureKind structureKind = resolved = kind == null ? StructureKind.RADIO : kind;
        if (this.kind == resolved) {
            return;
        }
        this.kind = resolved;
        BastionData.markTacticalDirty();
    }

    public boolean isRadio() {
        return this.kind == StructureKind.RADIO;
    }

    public boolean isHab() {
        return this.kind == StructureKind.HAB;
    }

    public boolean isLegacyCombined() {
        return this.legacyCombined;
    }

    public void setLegacyCombined(boolean legacyCombined) {
        this.legacyCombined = legacyCombined;
    }

    public UUID getBastionId() {
        return this.bastionId;
    }

    public String getTeam() {
        return this.team;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (Objects.equals(this.name, name)) {
            return;
        }
        this.name = name;
        BastionData.markTacticalDirty();
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public UUID getArmorStandId() {
        return this.armorStandId;
    }

    public void setArmorStandId(UUID armorStandId) {
        this.armorStandId = armorStandId;
    }

    public float getCoreHealth() {
        return this.coreHealth;
    }

    public void setCoreHealth(float coreHealth) {
        this.coreHealth = coreHealth;
    }

    public void resetMissingEntityTicks() {
    }

    public int getBastionNumber() {
        return this.bastionNumber;
    }

    public void setBastionNumber(int bastionNumber) {
        this.bastionNumber = bastionNumber;
    }

    @Nullable
    public BlockPos getArmorStandPosition() {
        return this.armorStandPosition;
    }

    public void setArmorStandPosition(@Nullable BlockPos armorStandPosition) {
        this.armorStandPosition = armorStandPosition;
    }

    public void clearArmorStandPosition() {
        this.armorStandPosition = null;
    }

    @Nullable
    public BlockPos getShulkerPos() {
        return this.shulkerPos;
    }

    public void setShulkerPos(BlockPos shulkerPos) {
        this.shulkerPos = shulkerPos;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        BastionData.markTacticalDirty();
    }

    public int getConstructionSupplies() {
        return this.constructionSupplies;
    }

    public int getAmmunitionSupplies() {
        return this.ammunitionSupplies;
    }

    public void addConstructionSupplies(int amount, int maximum) {
        int updated = Math.max(0, Math.min(maximum, this.constructionSupplies + amount));
        if (updated == this.constructionSupplies) {
            return;
        }
        this.constructionSupplies = updated;
        BastionData.markTacticalDirty();
    }

    public void addAmmunitionSupplies(int amount, int maximum) {
        int updated = Math.max(0, Math.min(maximum, this.ammunitionSupplies + amount));
        if (updated == this.ammunitionSupplies) {
            return;
        }
        this.ammunitionSupplies = updated;
        BastionData.markTacticalDirty();
    }

    public boolean consumeConstructionSupplies(int amount) {
        if (amount < 0 || this.constructionSupplies < amount) {
            return false;
        }
        if (amount == 0) {
            return true;
        }
        this.constructionSupplies -= amount;
        BastionData.markTacticalDirty();
        return true;
    }

    public boolean consumeAmmunitionSupplies(int amount) {
        if (amount < 0 || this.ammunitionSupplies < amount) {
            return false;
        }
        if (amount == 0) {
            return true;
        }
        this.ammunitionSupplies -= amount;
        BastionData.markTacticalDirty();
        return true;
    }

    public boolean isHabBuilt() {
        return this.habBuilt;
    }

    public void setHabBuilt(boolean habBuilt) {
        if (this.habBuilt == habBuilt) {
            return;
        }
        this.habBuilt = habBuilt;
        BastionData.markTacticalDirty();
    }

    public boolean isAmmoCrateBuilt() {
        return this.ammoCrateBuilt;
    }

    public void setAmmoCrateBuilt(boolean ammoCrateBuilt) {
        if (this.ammoCrateBuilt == ammoCrateBuilt) {
            return;
        }
        this.ammoCrateBuilt = ammoCrateBuilt;
        BastionData.markTacticalDirty();
    }

    public long getHabAvailableAt() {
        return this.habAvailableAt;
    }

    public void setHabAvailableAt(long habAvailableAt) {
        if (this.habAvailableAt == habAvailableAt) {
            return;
        }
        this.habAvailableAt = habAvailableAt;
        BastionData.markTacticalDirty();
    }

    public long getHabDisabledUntil() {
        return this.habDisabledUntil;
    }

    public void setHabDisabledUntil(long habDisabledUntil) {
        if (this.habDisabledUntil == habDisabledUntil) {
            return;
        }
        this.habDisabledUntil = habDisabledUntil;
        BastionData.markTacticalDirty();
    }

    private static void markTacticalDirty() {
        EspetroAPI.markTacticalMapStateDirty();
    }

    public boolean checkArmorStand() {
        ArmorStand armorStand;
        if (!this.isChunkLoaded()) {
            return false;
        }
        if (this.kind == StructureKind.RADIO && !this.legacyCombined) {
            if (BastionItems.RADIO_BLOCK != null && this.level.m_8055_(this.position).m_60713_(BastionItems.RADIO_BLOCK)) {
                this.armorStandPosition = this.position;
                this.resetMissingEntityTicks();
                return true;
            }
            return false;
        }
        if (this.armorStandId == null) {
            return false;
        }
        Entity entity = this.level.m_8791_(this.armorStandId);
        if (entity instanceof ArmorStand && (armorStand = (ArmorStand)entity).m_6084_()) {
            BastionManager.getInstance().syncCoreArmorStand(armorStand);
            this.armorStandPosition = entity.m_20183_();
            this.coreHealth = armorStand.m_21223_();
            this.resetMissingEntityTicks();
            return true;
        }
        return false;
    }

    public boolean isChunkLoaded() {
        return this.level.m_46805_(this.armorStandPosition != null ? this.armorStandPosition : this.position);
    }

    public float getArmorStandHealth() {
        if (this.kind == StructureKind.RADIO && !this.legacyCombined) {
            return this.coreHealth;
        }
        if (this.armorStandId == null) {
            return 0.0f;
        }
        Entity entity = this.level.m_8791_(this.armorStandId);
        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand)entity;
            return armorStand.m_21223_();
        }
        return 0.0f;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.m_128362_("bastionId", this.bastionId);
        tag.m_128359_("team", this.team);
        tag.m_128359_("name", this.name);
        tag.m_128359_("kind", this.kind.name());
        tag.m_128379_("legacyCombined", this.legacyCombined);
        tag.m_128405_("x", this.position.m_123341_());
        tag.m_128405_("y", this.position.m_123342_());
        tag.m_128405_("z", this.position.m_123343_());
        tag.m_128405_("bastionNumber", this.bastionNumber);
        tag.m_128350_("coreHealth", this.coreHealth);
        if (this.armorStandPosition != null) {
            tag.m_128405_("armorStandX", this.armorStandPosition.m_123341_());
            tag.m_128405_("armorStandY", this.armorStandPosition.m_123342_());
            tag.m_128405_("armorStandZ", this.armorStandPosition.m_123343_());
        }
        if (this.armorStandId != null) {
            tag.m_128362_("armorStandId", this.armorStandId);
        }
        if (this.shulkerPos != null) {
            tag.m_128405_("sx", this.shulkerPos.m_123341_());
            tag.m_128405_("sy", this.shulkerPos.m_123342_());
            tag.m_128405_("sz", this.shulkerPos.m_123343_());
        }
        tag.m_128379_("active", this.active);
        tag.m_128405_("constructionSupplies", this.constructionSupplies);
        tag.m_128405_("ammunitionSupplies", this.ammunitionSupplies);
        tag.m_128379_("habBuilt", this.habBuilt);
        tag.m_128379_("ammoCrateBuilt", this.ammoCrateBuilt);
        tag.m_128356_("habAvailableAt", this.habAvailableAt);
        tag.m_128356_("habDisabledUntil", this.habDisabledUntil);
        return tag;
    }

    public static BastionData load(CompoundTag tag, ServerLevel level) {
        UUID bastionId = tag.m_128403_("bastionId") ? tag.m_128342_("bastionId") : UUID.randomUUID();
        String team = tag.m_128461_("team");
        String name = tag.m_128461_("name");
        int x = tag.m_128451_("x");
        int y = tag.m_128451_("y");
        int z = tag.m_128451_("z");
        BlockPos pos = new BlockPos(x, y, z);
        boolean hasKind = tag.m_128441_("kind");
        StructureKind kind = StructureKind.fromStorage(hasKind ? tag.m_128461_("kind") : null);
        BastionData data = new BastionData(bastionId, team, name, pos, level, kind);
        if (tag.m_128441_("bastionNumber")) {
            data.setBastionNumber(tag.m_128451_("bastionNumber"));
        }
        if (tag.m_128441_("coreHealth")) {
            data.setCoreHealth(tag.m_128457_("coreHealth"));
        }
        if (tag.m_128441_("armorStandX")) {
            data.setArmorStandPosition(new BlockPos(tag.m_128451_("armorStandX"), tag.m_128451_("armorStandY"), tag.m_128451_("armorStandZ")));
        } else {
            data.setArmorStandPosition(pos.m_7494_());
        }
        if (tag.m_128403_("armorStandId")) {
            data.setArmorStandId(tag.m_128342_("armorStandId"));
        }
        if (tag.m_128441_("sx")) {
            data.setShulkerPos(new BlockPos(tag.m_128451_("sx"), tag.m_128451_("sy"), tag.m_128451_("sz")));
        }
        data.setActive(tag.m_128471_("active"));
        data.constructionSupplies = Math.max(0, tag.m_128451_("constructionSupplies"));
        data.ammunitionSupplies = Math.max(0, tag.m_128451_("ammunitionSupplies"));
        data.habBuilt = !tag.m_128441_("habBuilt") || tag.m_128471_("habBuilt");
        data.ammoCrateBuilt = !tag.m_128441_("ammoCrateBuilt") || tag.m_128471_("ammoCrateBuilt");
        data.habAvailableAt = tag.m_128454_("habAvailableAt");
        data.habDisabledUntil = tag.m_128454_("habDisabledUntil");
        if (!hasKind) {
            data.setKind(StructureKind.RADIO);
            if (data.habBuilt) {
                data.setLegacyCombined(true);
            }
        } else if (tag.m_128441_("legacyCombined")) {
            data.setLegacyCombined(tag.m_128471_("legacyCombined"));
        }
        if (data.isHab()) {
            data.habBuilt = true;
        }
        return data;
    }
}

