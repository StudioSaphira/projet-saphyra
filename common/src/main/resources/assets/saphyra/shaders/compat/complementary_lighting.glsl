// Replace artificial illumination only where Saphyra has a custom light contribution.
// Preserve Complementary's intensity control, sun, shadows, material shading and emission.
vec4 saphyraField = saphyra_light_field(playerPos, worldGeoNormal);
float saphyraBlend = clamp(saphyraField.a * 15.0, 0.0, 1.0);
if (saphyraBlend > 0.0) {
    vec3 saphyraLevel = clamp(saphyraField.rgb, 0.0, 1.0);
    vec3 saphyraCurve;
    if (!noSmoothLighting) {
        vec3 steep = pow(saphyraLevel, vec3(8.0)) * (2.8 - 0.6 * vsBrightness + XLIGHT_CURVE);
        vec3 calm = saphyraLevel * (2.8 + 0.6 * vsBrightness - XLIGHT_CURVE);
        saphyraCurve = pow(max(steep + calm, vec3(0.0)), vec3(2.25));
    } else {
        float curve = XLIGHT_CURVE > 0.999 ? XLIGHT_CURVE : sqrt2(XLIGHT_CURVE);
        saphyraCurve = pow(saphyraLevel, vec3(3.0 * curve)) * 10.0;
    }
    float neutralIntensity = max(blocklightCol.r, max(blocklightCol.g, blocklightCol.b));
    blockLighting = mix(blockLighting, saphyraCurve * neutralIntensity, saphyraBlend);
}
