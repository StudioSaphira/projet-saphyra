package net.saphyra.common.client.lighting.shader;

import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

/** Adapts Minecraft's native shader interface without changing its sampler lifecycle. */
public record VanillaLightingUniforms(ShaderInstance shader) implements LightingUniforms {
    @Override
    public boolean has(String name) {
        return shader.getUniform(name) != null;
    }

    @Override
    public void scalar(String name, float value) {
        shader.safeGetUniform(name).set(value);
    }

    @Override
    public void vector(String name, float x, float y, float z) {
        shader.safeGetUniform(name).set(x, y, z);
    }

    @Override
    public void matrix(String name, Matrix4f value) {
        shader.safeGetUniform(name).set(value);
    }

    @Override
    public void sampler(String name, int texture) {
        shader.setSampler(name, texture);
    }

}
