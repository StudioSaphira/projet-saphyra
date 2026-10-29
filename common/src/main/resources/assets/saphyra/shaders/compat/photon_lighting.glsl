// Photon textured effects do not provide a complete material/fog interface.
#ifndef PROGRAM_GBUFFERS_TEXTURED
vec4 saphyraField = saphyra_light_field(scene_pos, flat_normal);
vec3 saphyraLevel = clamp(saphyraField.rgb, 0.0, 1.0);
vec3 saphyraCurve = vec3(
    get_blocklight_falloff(saphyraLevel.r, light_levels.y, ao),
    get_blocklight_falloff(saphyraLevel.g, light_levels.y, ao),
    get_blocklight_falloff(saphyraLevel.b, light_levels.y, ao));
lighting = saphyra_merge(lighting, saphyraField, saphyraCurve * directional_lighting * blocklight_scale * saphyra_peak(blocklight_color));

#endif
