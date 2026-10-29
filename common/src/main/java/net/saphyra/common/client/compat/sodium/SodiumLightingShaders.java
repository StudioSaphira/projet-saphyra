package net.saphyra.common.client.compat.sodium;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.resources.ResourceLocation;

/** Injects Saphyra's shared RGB sampling into Sodium 0.8.13's existing terrain programs. */
public final class SodiumLightingShaders {

    private SodiumLightingShaders() {}

    /** Leaves unrelated shaders intact, including programs supplied by external shader packs. */
    public static String patch(ResourceLocation id, String source) {
        if (!id.getNamespace().equals("sodium")) return source;
        if (id.getPath().equals("blocks/block_layer_opaque.vsh")) {
            String anchor = "v_Color = _vert_color * texture(u_LightTex, _vert_tex_light_coord);";
            require(source, anchor);
            return source.replace("void main() {", "out vec3 v_SaphyraPosition;\nout vec2 v_SaphyraLightUV;\n\nvoid main() {")
                    .replace(anchor,"v_Color = _vert_color;\n    v_SaphyraPosition = position;\n    v_SaphyraLightUV = _vert_tex_light_coord;");
        }
        if (id.getPath().equals("blocks/block_layer_opaque.fsh")) {
            // Derivative magnitudes depend on distance, resolution and surface angle.
            // Normalize even tiny nonzero vectors: a screen-space cutoff would switch the
            // shared sampler's surface offset on/off and draw visible lighting bands.
            String anchor = "color *= v_Color;";
            require(source, anchor);
            String prefix = "#define SAPHYRA_EXTERNAL_TRANSFORM\n" + sharedSource()
                    + "\nin vec3 v_SaphyraPosition;\nin vec2 v_SaphyraLightUV;\nuniform sampler2D u_LightTex;\n";
            return source.replace("void main() {", prefix + "\nvoid main() {")
                    .replace(anchor,"vec3 normal = cross(dFdx(v_SaphyraPosition), dFdy(v_SaphyraPosition));\n"
                            + "    normal *= inversesqrt(max(dot(normal, normal), 1e-30));\n"
                            + "    color *= v_Color * saphyra_lightmap_relative(u_LightTex, v_SaphyraLightUV, v_SaphyraPosition, normal);");
        }
        return source;
    }

    private static void require(String source, String anchor) {
        if (!source.contains(anchor)) throw new IllegalStateException("Saphyra: unsupported Sodium terrain shader layout");
    }

    private static String sharedSource() {
        String path = "/assets/minecraft/shaders/include/saphyra_lighting.glsl";
        try (var input = SodiumLightingShaders.class.getResourceAsStream(path)) {
            if (input == null) throw new IllegalStateException("Missing Saphyra RGB shader include");
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException failure) {
            throw new IllegalStateException("Could not load Saphyra RGB shader include", failure);
        }
    }
}
