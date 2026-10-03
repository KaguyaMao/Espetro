/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  org.lwjgl.opengl.GL20
 */
package com.redabysslucia.dragonrise_reforge.client.outline.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.lwjgl.opengl.GL20;

public class OutlineShader
implements AutoCloseable {
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private Map<String, Integer> uniformLocations = new HashMap<String, Integer>();
    private Map<String, Integer> textureUnits = new HashMap<String, Integer>();
    private int nextTextureUnit = 0;

    public OutlineShader(String name) throws IOException {
        this(name, "sobel");
    }

    public OutlineShader(String fragmentName, String vertexName) throws IOException {
        System.out.println("[OutlineShader] Loading shader: " + fragmentName + " (vertex: " + vertexName + ")");
        String vertexSource = this.loadShaderSource("shaders/outline/" + vertexName + ".vsh");
        System.out.println("[OutlineShader] Loaded vertex shader: " + vertexName + " (" + vertexSource.length() + " chars)");
        String fragmentSource = this.loadShaderSource("shaders/outline/" + fragmentName + ".fsh");
        System.out.println("[OutlineShader] Loaded fragment shader: " + fragmentName + " (" + fragmentSource.length() + " chars)");
        this.compile(vertexSource, fragmentSource);
        System.out.println("[OutlineShader] Compiled shader: " + fragmentName);
    }

    private String loadShaderSource(String path) throws IOException {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath((String)"dragonrise_reforge", (String)path);
        Resource resource = (Resource)Minecraft.m_91087_().m_91098_().m_213713_(location).orElseThrow();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.m_215507_(), StandardCharsets.UTF_8));){
            String string = reader.lines().collect(Collectors.joining("\n"));
            return string;
        }
    }

    private void compile(String vertexSource, String fragmentSource) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.vertexShaderId = GL20.glCreateShader((int)35633);
        GL20.glShaderSource((int)this.vertexShaderId, (CharSequence)vertexSource);
        GL20.glCompileShader((int)this.vertexShaderId);
        if (GL20.glGetShaderi((int)this.vertexShaderId, (int)35713) == 0) {
            String log = GL20.glGetShaderInfoLog((int)this.vertexShaderId);
            throw new RuntimeException("Failed to compile vertex shader:\n" + log);
        }
        this.fragmentShaderId = GL20.glCreateShader((int)35632);
        GL20.glShaderSource((int)this.fragmentShaderId, (CharSequence)fragmentSource);
        GL20.glCompileShader((int)this.fragmentShaderId);
        if (GL20.glGetShaderi((int)this.fragmentShaderId, (int)35713) == 0) {
            String log = GL20.glGetShaderInfoLog((int)this.fragmentShaderId);
            throw new RuntimeException("Failed to compile fragment shader:\n" + log);
        }
        this.programId = GL20.glCreateProgram();
        GL20.glAttachShader((int)this.programId, (int)this.vertexShaderId);
        GL20.glAttachShader((int)this.programId, (int)this.fragmentShaderId);
        GL20.glBindAttribLocation((int)this.programId, (int)0, (CharSequence)"Position");
        GL20.glBindAttribLocation((int)this.programId, (int)1, (CharSequence)"TexCoord");
        GL20.glBindAttribLocation((int)this.programId, (int)1, (CharSequence)"TexCoord0");
        GL20.glLinkProgram((int)this.programId);
        if (GL20.glGetProgrami((int)this.programId, (int)35714) == 0) {
            String log = GL20.glGetProgramInfoLog((int)this.programId);
            throw new RuntimeException("Failed to link shader program:\n" + log);
        }
    }

    public void use() {
        RenderSystem.assertOnRenderThreadOrInit();
        GL20.glUseProgram((int)this.programId);
    }

    public void setUniform(String name, float value) {
        int location = this.getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform1f((int)location, (float)value);
        }
    }

    public void setUniform(String name, int value) {
        int location = this.getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform1i((int)location, (int)value);
        }
    }

    public void setUniform(String name, float v1, float v2) {
        int location = this.getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform2f((int)location, (float)v1, (float)v2);
        }
    }

    public void setUniform(String name, float v1, float v2, float v3) {
        int location = this.getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform3f((int)location, (float)v1, (float)v2, (float)v3);
        }
    }

    public void setUniform(String name, float v1, float v2, float v3, float v4) {
        int location = this.getUniformLocation(name);
        if (location != -1) {
            GL20.glUniform4f((int)location, (float)v1, (float)v2, (float)v3, (float)v4);
        }
    }

    public void setTexture(String name, int textureId) {
        int unit = this.textureUnits.computeIfAbsent(name, k -> this.nextTextureUnit++);
        int location = this.getUniformLocation(name);
        if (location != -1) {
            RenderSystem.activeTexture((int)(33984 + unit));
            GlStateManager._bindTexture((int)textureId);
            GL20.glUniform1i((int)location, (int)unit);
        }
    }

    private int getUniformLocation(String name) {
        return this.uniformLocations.computeIfAbsent(name, n -> GL20.glGetUniformLocation((int)this.programId, (CharSequence)n));
    }

    @Override
    public void close() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.programId != 0) {
            GL20.glDeleteProgram((int)this.programId);
            this.programId = 0;
        }
        if (this.vertexShaderId != 0) {
            GL20.glDeleteShader((int)this.vertexShaderId);
            this.vertexShaderId = 0;
        }
        if (this.fragmentShaderId != 0) {
            GL20.glDeleteShader((int)this.fragmentShaderId);
            this.fragmentShaderId = 0;
        }
        this.uniformLocations.clear();
        this.textureUnits.clear();
    }
}

