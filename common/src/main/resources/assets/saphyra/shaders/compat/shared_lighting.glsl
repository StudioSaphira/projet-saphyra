// Shared blend and stable screen-derivative normal for packs lacking a world normal.
float saphyra_peak(vec3 value) { return max(value.r, max(value.g, value.b)); }
vec3 saphyra_merge(vec3 original, vec4 field, vec3 replacement) {
    return mix(original, replacement, clamp(field.a * 15.0, 0.0, 1.0));
}
vec3 saphyra_surface_normal(vec3 position) {
    vec3 n = cross(dFdx(position), dFdy(position));
    return n * inversesqrt(max(dot(n,n), 1e-30));
}
