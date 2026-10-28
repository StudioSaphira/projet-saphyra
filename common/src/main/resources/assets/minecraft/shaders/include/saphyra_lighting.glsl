// Shared native RGB sampling. Static pages are addressed by world section, never by camera range.
uniform sampler2D SaphyraLightVolume;
uniform float SaphyraLightEnabled;
uniform float SaphyraDynamicEnabled;
uniform vec3 SaphyraCameraOffset;
uniform mat4 SaphyraInverseView;
uniform sampler2D SaphyraStaticAtlas;
uniform sampler2D SaphyraStaticPages;
uniform float SaphyraStaticEnabled;
uniform vec3 SaphyraStaticOffset;
uniform vec3 SaphyraStaticSize;
uniform float SaphyraStaticColumns;

vec4 saphyra_voxel(ivec3 p) {
    if (any(lessThan(p, ivec3(0))) || any(greaterThanEqual(p, ivec3(64)))) return vec4(0.0);
    return texelFetch(SaphyraLightVolume, ivec2((p.z % 8) * 64 + p.x, (p.z / 8) * 64 + p.y), 0);
}

vec4 saphyra_static_voxel(ivec3 p) {
    ivec3 section = ivec3(floor(vec3(p) / 16.0));
    if (any(lessThan(section, ivec3(0))) || any(greaterThanEqual(section, ivec3(SaphyraStaticSize)))) return vec4(0.0);
    ivec3 encoded = ivec3(round(texelFetch(SaphyraStaticPages, ivec2(section.x, section.y + int(SaphyraStaticSize.y) * section.z), 0).rgb * 255.0));
    int slot = encoded.r + encoded.g * 256 + encoded.b * 65536 - 1;
    if (slot < 0) return vec4(0.0);
    ivec3 cell = p - section * 16;
    int columns = int(SaphyraStaticColumns);
    return texelFetch(SaphyraStaticAtlas, ivec2((slot % columns) * 64 + (cell.z % 4) * 16 + cell.x,
            (slot / columns) * 64 + (cell.z / 4) * 16 + cell.y), 0);
}

vec4 saphyra_sample(ivec3 p, bool fixedLight) {
    if (fixedLight) return saphyra_static_voxel(p);
    return saphyra_voxel(p);
}

vec4 saphyra_field(vec3 p, bool fixedLight) {
    vec3 f = fract(p); ivec3 b = ivec3(floor(p));
    return mix(mix(mix(saphyra_sample(b,fixedLight), saphyra_sample(b+ivec3(1,0,0),fixedLight), f.x),
                   mix(saphyra_sample(b+ivec3(0,1,0),fixedLight), saphyra_sample(b+ivec3(1,1,0),fixedLight), f.x), f.y),
               mix(mix(saphyra_sample(b+ivec3(0,0,1),fixedLight), saphyra_sample(b+ivec3(1,0,1),fixedLight), f.x),
                   mix(saphyra_sample(b+ivec3(0,1,1),fixedLight), saphyra_sample(b+ivec3(1,1,1),fixedLight), f.x), f.y), f.z);
}

vec4 saphyra_lightmap(sampler2D lightmap, ivec2 uv, vec3 position, vec3 normal) {
    vec2 originalUV = clamp(vec2(uv)/256.0, vec2(0.03125), vec2(0.96875));
    vec4 original = texture(lightmap, originalUV);
    if (SaphyraLightEnabled < 0.5) return original;
    mat4 transform = SaphyraInverseView * ModelViewMat;
    vec3 relative = (transform * vec4(position,1.0)).xyz;
    vec3 n = mat3(transform) * normal;
    if (dot(n,n) > 0.001) relative += normalize(n) * 0.52;
    vec4 field = vec4(0.0);
    if (SaphyraStaticEnabled > 0.5) field = saphyra_field(relative + SaphyraStaticOffset - vec3(0.5), true);
    if (SaphyraDynamicEnabled > 0.5) {
        vec3 local = relative + SaphyraCameraOffset;
        float edge = min(min(local.x,local.y),local.z);
        edge = min(edge,min(min(64.0-local.x,64.0-local.y),64.0-local.z));
        if (edge > 15.0) field = max(field, saphyra_field(local - vec3(0.5), false) * clamp((edge-15.0)/3.0,0.0,1.0));
    }
    float blend = clamp(field.a * 15.0,0.0,1.0);
    if (blend <= 0.0) return original;
    vec3 illumination;
    illumination.r = texture(lightmap,vec2(clamp(field.r*0.9375+0.03125,0.03125,0.96875),originalUV.y)).r;
    illumination.g = texture(lightmap,vec2(clamp(field.g*0.9375+0.03125,0.03125,0.96875),originalUV.y)).g;
    illumination.b = texture(lightmap,vec2(clamp(field.b*0.9375+0.03125,0.03125,0.96875),originalUV.y)).b;
    return vec4(mix(original.rgb,illumination,blend),original.a);
}
