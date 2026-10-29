vec4 saphyraField = saphyra_light_field(worldPos, mat3(gbufferModelViewInverse) * normal);
vec3 saphyraLevel = clamp(saphyraField.rgb, 0.0, 1.0);
vec3 saphyraCurve = pow(saphyraLevel, vec3(10.0)) * 1.6 + saphyraLevel * 0.6;
float saphyraIntensity = max(BLOCKLIGHT_R, max(BLOCKLIGHT_G, BLOCKLIGHT_B)) * BLOCKLIGHT_I / 255.0;
blockLighting = saphyra_merge(blockLighting, saphyraField, saphyraCurve * saphyraCurve * saphyraIntensity * saphyraIntensity);
