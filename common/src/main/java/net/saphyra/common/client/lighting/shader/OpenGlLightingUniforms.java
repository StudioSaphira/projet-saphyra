package net.saphyra.common.client.lighting.shader;

import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryStack;

/** Supplies RGB uniforms to an externally managed GL program and restores borrowed texture units. */
public final class OpenGlLightingUniforms implements LightingUniforms {

    private final int program;
    private final boolean externalSamplers;
    private final Map<String, Integer> locations = new HashMap<>();
    private final int[] textures = new int[3], samplers = new int[3];
    private final boolean[] borrowed = new boolean[3];

    public OpenGlLightingUniforms(int program) { this(program, false); }

    /** Iris owns sampler allocation; native Sodium programs borrow and restore units instead. */
    public OpenGlLightingUniforms(int program, boolean externalSamplers) {
        this.program = program;
        this.externalSamplers = externalSamplers;
    }

    private int location(String name) {
        return locations.computeIfAbsent(name, key -> GL20C.glGetUniformLocation(program, key));
    }

    @Override
    public boolean has(String name) {
        return location(name) >= 0;
    }

    @Override
    public void scalar(String name, float value) {
        GL20C.glUniform1f(location(name), value);
    }

    @Override
    public void vector(String name, float x, float y, float z) {
        GL20C.glUniform3f(location(name), x, y, z);
    }


    @Override
    public void matrix(String name, Matrix4f value) {
        int uniform = location(name);
        if (uniform < 0) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL20C.glUniformMatrix4fv(uniform, false, value.get(stack.mallocFloat(16)));
        }
    }

    @Override
    public void sampler(String name, int texture) {
        if (externalSamplers || has("SaphyraIrisManaged")) return;
        int uniform = location(name);
        if (uniform < 0) return;
        int index = switch (name) {
            case "SaphyraLightVolume" -> 0;
            case "SaphyraStaticAtlas" -> 1;
            case "SaphyraStaticPages" -> 2;
            default -> throw new IllegalArgumentException("Unknown lighting sampler: " + name);
        };
        // GL 3.3 guarantees sixteen fragment texture units; Sodium's terrain uses units 0 and 2.
        int unit = 13 + index;
        int active = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
        GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + unit);
        if (!borrowed[index]) {
            textures[index] = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D);
            samplers[index] = GL11C.glGetInteger(GL33C.GL_SAMPLER_BINDING);
            borrowed[index] = true;
        }
        GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, texture);
        GL33C.glBindSampler(unit, 0);
        GL20C.glUniform1i(uniform, unit);
        GL13C.glActiveTexture(active);
    }

    /** Restores textures, samplers and active-unit state before the renderer leaves its program. */
    public void restore() {
        int active = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
        for (int i = 0; i < 3; i++) {
            if (!borrowed[i]) continue;
            GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + 13 + i);
            GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textures[i]);
            GL33C.glBindSampler(13+i, samplers[i]);
            borrowed[i] = false;
        }
        GL13C.glActiveTexture(active);
    }
}
