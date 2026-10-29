// Match the peak of MakeUp's temperature presets without importing vertex-only color utilities.
#if BLOCKLIGHT_TEMP <= 2
float saphyraIntensity = 0.29975 - 0.025 * float(BLOCKLIGHT_TEMP);
#elif BLOCKLIGHT_TEMP == 3
float saphyraIntensity = 0.22;
#else
float saphyraIntensity = 0.19;
#endif
// Reconstruct camera-relative position for per-fragment lighting rather than interpolating vertex light.
vec4 saphyraView = gbufferProjectionInverse * vec4(gl_FragCoord.xy / vec2(viewWidth, viewHeight) * 2.0 - 1.0, gl_FragCoord.z * 2.0 - 1.0, 1.0);
vec3 saphyraPosition = (gbufferModelViewInverse * vec4(saphyraView.xyz / saphyraView.w, 1.0)).xyz;
vec4 saphyraField = saphyra_light_field(saphyraPosition, saphyra_surface_normal(saphyraPosition));
vec3 saphyraLevel = clamp(saphyraField.rgb, 0.0, 1.0);
vec3 saphyraCurve = saphyraLevel * sqrt(saphyraLevel) + pow(saphyraLevel * 1.17, vec3(6.0));
finalCandleColor = saphyra_merge(finalCandleColor, saphyraField, min(saphyraCurve * saphyraIntensity, vec3(4.0)));
