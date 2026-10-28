package net.saphyra.common.api.lighting;

/** RGB emission in sRGB with vanilla-compatible intensity from zero to fifteen. */
public record LightEmission(int rgb, int intensity) {

    public static final LightEmission NONE = new LightEmission(0xFFFFFF, 0);

    public LightEmission {
        if ((rgb & 0xFF000000) != 0 || intensity < 0 || intensity > 15) {
            throw new IllegalArgumentException("Expected RGB 0x000000..0xFFFFFF and intensity 0..15");
        }
    }

    public static LightEmission white(int intensity) {
        return new LightEmission(0xFFFFFF, Math.clamp(intensity, 0, 15));
    }
}
