package net.saphyra.common.client.lighting.shader;

import org.joml.Matrix4f;

/** Renderer-neutral sink for the same static and dynamic RGB fields. */
public interface LightingUniforms {
    boolean has(String name);
    void scalar(String name, float value);
    void vector(String name, float x, float y, float z);
    void matrix(String name, Matrix4f value);
    void sampler(String name, int texture);
}
