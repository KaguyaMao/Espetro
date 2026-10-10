/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class SoundDefinition {
    private final List<Sound> sounds = new ArrayList<Sound>();
    private boolean replace = false;
    private String subtitle = null;

    private SoundDefinition() {
    }

    public static SoundDefinition definition() {
        return new SoundDefinition();
    }

    public SoundDefinition replace(boolean replace) {
        this.replace = replace;
        return this;
    }

    public SoundDefinition subtitle(@Nullable String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public SoundDefinition with(Sound sound) {
        this.sounds.add(sound);
        return this;
    }

    public SoundDefinition with(Sound ... sounds) {
        this.sounds.addAll(Arrays.asList(sounds));
        return this;
    }

    List<Sound> soundList() {
        return this.sounds;
    }

    JsonObject serialize() {
        if (this.sounds.isEmpty()) {
            throw new IllegalStateException("Unable to serialize a sound definition that has no sounds!");
        }
        JsonObject object = new JsonObject();
        if (this.replace) {
            object.addProperty("replace", Boolean.valueOf(true));
        }
        if (this.subtitle != null) {
            object.addProperty("subtitle", this.subtitle);
        }
        JsonArray sounds = new JsonArray();
        this.sounds.stream().map(Sound::serialize).forEach(arg_0 -> ((JsonArray)sounds).add(arg_0));
        object.add("sounds", (JsonElement)sounds);
        return object;
    }

    public static enum SoundType {
        SOUND("sound"),
        EVENT("event");

        private final String jsonString;

        private SoundType(String jsonString) {
            this.jsonString = jsonString;
        }
    }

    public static final class Sound {
        private static final SoundType DEFAULT_TYPE = SoundType.SOUND;
        private static final float DEFAULT_VOLUME = 1.0f;
        private static final float DEFAULT_PITCH = 1.0f;
        private static final int DEFAULT_WEIGHT = 1;
        private static final boolean DEFAULT_STREAM = false;
        private static final int DEFAULT_ATTENUATION_DISTANCE = 16;
        private static final boolean DEFAULT_PRELOAD = false;
        private final ResourceLocation name;
        private final SoundType type;
        private float volume = 1.0f;
        private float pitch = 1.0f;
        private int weight = 1;
        private boolean stream = false;
        private int attenuationDistance = 16;
        private boolean preload = false;

        private Sound(ResourceLocation name, SoundType type) {
            this.name = name;
            this.type = type;
        }

        public static Sound sound(ResourceLocation name, SoundType type) {
            return new Sound(name, type);
        }

        public Sound volume(double volume) {
            return this.volume((float)volume);
        }

        public Sound volume(float volume) {
            if (volume <= 0.0f) {
                throw new IllegalArgumentException("Volume must be positive for sound " + String.valueOf(this.name) + ", but instead got " + volume);
            }
            this.volume = volume;
            return this;
        }

        public Sound pitch(double pitch) {
            return this.pitch((float)pitch);
        }

        public Sound pitch(float pitch) {
            if (pitch <= 0.0f) {
                throw new IllegalArgumentException("Pitch must be positive for sound " + String.valueOf(this.name) + ", but instead got " + pitch);
            }
            this.pitch = pitch;
            return this;
        }

        public Sound weight(int weight) {
            if (weight <= 0) {
                throw new IllegalArgumentException("Weight has to be a positive number in sound " + String.valueOf(this.name) + ", but instead got " + weight);
            }
            this.weight = weight;
            return this;
        }

        public Sound stream() {
            return this.stream(true);
        }

        public Sound stream(boolean stream) {
            this.stream = stream;
            return this;
        }

        public Sound attenuationDistance(int attenuationDistance) {
            this.attenuationDistance = attenuationDistance;
            return this;
        }

        public Sound preload() {
            return this.preload(true);
        }

        public Sound preload(boolean preload) {
            this.preload = preload;
            return this;
        }

        ResourceLocation name() {
            return this.name;
        }

        SoundType type() {
            return this.type;
        }

        JsonElement serialize() {
            if (this.canBeInShortForm()) {
                return new JsonPrimitive(this.stripMcPrefix(this.name));
            }
            JsonObject object = new JsonObject();
            object.addProperty("name", this.stripMcPrefix(this.name));
            if (this.type != DEFAULT_TYPE) {
                object.addProperty("type", this.type.jsonString);
            }
            if (this.volume != 1.0f) {
                object.addProperty("volume", (Number)Float.valueOf(this.volume));
            }
            if (this.pitch != 1.0f) {
                object.addProperty("pitch", (Number)Float.valueOf(this.pitch));
            }
            if (this.weight != 1) {
                object.addProperty("weight", (Number)this.weight);
            }
            if (this.stream) {
                object.addProperty("stream", Boolean.valueOf(this.stream));
            }
            if (this.preload) {
                object.addProperty("preload", Boolean.valueOf(this.preload));
            }
            if (this.attenuationDistance != 16) {
                object.addProperty("attenuation_distance", (Number)this.attenuationDistance);
            }
            return object;
        }

        private boolean canBeInShortForm() {
            return this.type == DEFAULT_TYPE && this.volume == 1.0f && this.pitch == 1.0f && this.weight == 1 && !this.stream && this.attenuationDistance == 16 && !this.preload;
        }

        private String stripMcPrefix(ResourceLocation name) {
            return "minecraft".equals(name.m_135827_()) ? name.m_135815_() : name.toString();
        }
    }
}

