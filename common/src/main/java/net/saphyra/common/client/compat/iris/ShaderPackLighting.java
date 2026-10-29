package net.saphyra.common.client.compat.iris;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.LoggerFactory;

/** Shares RGB sampling, source validation and in-memory injection across supported shader packs. */
public final class ShaderPackLighting {

    private static final Set<String> ANNOUNCED = new HashSet<>();
    private static final ShaderLightingProfile COMPLEMENTARY = new ShaderLightingProfile(
            "Complementary r5.9.3", "void DoLighting(", "blockLighting *= XLIGHT_I;", "complementary_lighting");

    private ShaderPackLighting() {}

    private static ShaderLightingProfile profile(String pack) {
        String name = pack.endsWith(".zip") ? pack.substring(0, pack.length() - 4) : pack;
        return switch (name) {
            case "ComplementaryUnbound_r5.9.3", "ComplementaryReimagined_r5.9.3" -> COMPLEMENTARY;
            case "BSL_v10.1.8" -> new ShaderLightingProfile("BSL 10.1.8", "void GetLighting(",
                    "vec3 minLighting = minLightCol * (1.0 - skylightSqr);", "bsl_lighting");
            case "Bliss_v2.1.2_(Chocapic13_Shaders_edit)" -> new ShaderLightingProfile("Bliss 2.1.2",
                    "vec3 doBlockLightLighting(", "blockLight *= autoBrightness;", "bliss_lighting");
            case "MakeUp-UltraFast-9.6" -> new ShaderLightingProfile("MakeUp UltraFast 9.6", "void main() {",
                    "vec3 finalCandleColor = candleColor;", "makeup_lighting");
            case "photon_v1.4" -> new ShaderLightingProfile("Photon 1.4", "vec3 get_block_lighting(",
                    "return lighting;", "photon_lighting");
            case "Solas Shader V3.8" -> new ShaderLightingProfile("Solas 3.8", "void gbuffersLighting(",
                    "//Dim blocklight in sunlight", "solas_lighting");
            default -> null;
        };
    }

    /** Patches only known versions and expected function bodies; unrelated programs stay intact. */
    public static ImmutableList<String> patch(ImmutableList<String> lines, String pack) {
        ShaderLightingProfile profile = profile(pack);
        if (lines == null || profile == null) return lines;
        String source = String.join("\n", lines);
        if (!source.contains("saphyra_light_field")) {
            int function = source.indexOf(profile.function());
            if (function < 0) return lines;
            int opening = source.indexOf('{', function);
            int closing = closingBrace(source, opening);
            if (opening < 0 || closing < 0) return lines;
            String body = source.substring(opening, closing + 1);
            int anchor = body.indexOf(profile.anchor());
            if (anchor < 0) return lines;
            if (anchor != body.lastIndexOf(profile.anchor())) {
                LoggerFactory.getLogger("Saphyra/Iris").warn("Ambiguous {} source; RGB injection skipped", profile.family());
                return lines;
            }
            String injection = resource("/assets/saphyra/shaders/compat/" + profile.resource() + ".glsl");
            // MakeUp declares its artificial-light accumulator at the insertion point.
            String replacement = profile.resource().equals("makeup_lighting")
                    ? profile.anchor() + "\n" + injection : injection + "\n" + profile.anchor();
            body = body.replace(profile.anchor(), replacement);
            source = source.substring(0, function) + sharedSource() + "\n"
                    + source.substring(function, opening) + body + source.substring(closing + 1);
            synchronized (ANNOUNCED) {
                if (ANNOUNCED.add(profile.family())) LoggerFactory.getLogger("Saphyra/Iris")
                        .info("Enabled in-memory RGB bridge for {}", profile.family());
            }
        }
        // Integer atlas addressing needs GLSL 1.30. Iris upgrades these compatibility programs further.
        source = source.replaceAll("(?m)^#version 120\\b", "#version 130");
        return ImmutableList.copyOf(source.split("\n", -1));
    }

    /** Finds the end of a GLSL function without interpreting braces inside comments. */
    private static int closingBrace(String source, int opening) {
        if (opening < 0) return -1;
        int depth = 0;
        boolean lineComment = false, blockComment = false;
        for (int i = opening; i < source.length(); i++) {
            char c = source.charAt(i), next = i + 1 < source.length() ? source.charAt(i + 1) : 0;
            if (lineComment) { if (c == '\n') lineComment = false; continue; }
            if (blockComment) { if (c == '*' && next == '/') { blockComment = false; i++; } continue; }
            if (c == '/' && next == '/') { lineComment = true; i++; continue; }
            if (c == '/' && next == '*') { blockComment = true; i++; continue; }
            if (c == '{') depth++;
            if (c == '}' && --depth == 0) return i;
        }
        return -1;
    }

    private static String sharedSource() {
        String source = resource("/assets/minecraft/shaders/include/saphyra_lighting.glsl");
        source = source.substring(0, source.indexOf("vec4 saphyra_lightmap_relative"));
        return "uniform float SaphyraIrisManaged;\n" + source.replace("SaphyraLightEnabled < 0.5",
                "SaphyraLightEnabled < 0.5 || SaphyraIrisManaged < 0.5")
                + resource("/assets/saphyra/shaders/compat/shared_lighting.glsl");
    }

    private static String resource(String name) {
        try (var input = ShaderPackLighting.class.getResourceAsStream(name)) {
            if (input == null) throw new IllegalStateException("Missing lighting resource: " + name);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException failure) {
            throw new IllegalStateException("Cannot read lighting resource: " + name, failure);
        }
    }
}
