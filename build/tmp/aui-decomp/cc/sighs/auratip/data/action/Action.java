/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data.action;

import cc.sighs.auratip.data.action.ActionRegistry;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public interface Action {
    public static final Codec<Action> CODEC = ActionRegistry.codec();

    default public <T> T accept(ActionVisitor<T> visitor) {
        return visitor.visitCustom(this);
    }

    public static interface ActionVisitor<T> {
        public T visitRunCommand(RunCommand var1);

        public T visitSimulateKey(SimulateKey var1);

        default public T visitScript(ScriptAction action) {
            return null;
        }

        default public T visitCustom(Action action) {
            return null;
        }
    }

    public record ScriptAction(ResourceLocation type, Map<String, Dynamic<?>> params) implements Action
    {
        @Override
        public <T> T accept(ActionVisitor<T> visitor) {
            return visitor.visitScript(this);
        }
    }

    public record SimulateKey(int keyCode) implements Action
    {
        public static final Codec<SimulateKey> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.INT.fieldOf("key_code").forGetter(SimulateKey::keyCode)).apply((Applicative)inst, SimulateKey::new));

        @Override
        public <T> T accept(ActionVisitor<T> visitor) {
            return visitor.visitSimulateKey(this);
        }
    }

    public record RunCommand(String command) implements Action
    {
        public static final Codec<RunCommand> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)Codec.STRING.fieldOf("command").forGetter(RunCommand::command)).apply((Applicative)inst, RunCommand::new));

        @Override
        public <T> T accept(ActionVisitor<T> visitor) {
            return visitor.visitRunCommand(this);
        }
    }
}

