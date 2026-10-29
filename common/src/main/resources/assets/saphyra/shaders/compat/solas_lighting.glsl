vec4 saphyraField = saphyra_light_field(worldPos, worldNormal);
vec3 saphyraLevel = clamp(saphyraField.rgb, 0.0, 1.0);
vec3 saphyraCurve = pow(saphyraLevel, vec3(12.0)) * 2.0 + max(saphyraLevel - 0.05, 0.0);
blockLighting = saphyra_merge(blockLighting, saphyraField, saphyraCurve * saphyraCurve * 0.5 * saphyra_peak(blockLightCol) * (1.0 - emission));
