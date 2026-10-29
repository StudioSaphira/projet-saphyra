vec4 saphyraField = saphyra_light_field(playerPos, saphyra_surface_normal(playerPos));
vec3 saphyraLevel = clamp(saphyraField.rgb, 0.0, 1.0);
vec3 saphyraCurve = 1.0 - sqrt(1.0 - saphyraLevel);
blockLight = saphyra_merge(blockLight, saphyraField, saphyraCurve * saphyraCurve * 2.0 * saphyra_peak(lightColor));
