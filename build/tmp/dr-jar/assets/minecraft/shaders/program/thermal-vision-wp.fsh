#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform float Resolution;
uniform float MosaicSize;

uniform vec3 Gray;
uniform float Saturation;

uniform vec3 Phosphor;

void main() {
    vec2 mosaicInSize = InSize / MosaicSize;
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;
    vec2 pixelatedCoord = texCoord - fractPix;
    vec4 BaseTexel = texture2D(DiffuseSampler, pixelatedCoord);

    BaseTexel.rgb = BaseTexel.rgb - fract(BaseTexel.rgb * Resolution) / Resolution;

    float Luma = dot(BaseTexel.rgb, Gray);
    vec3 Chroma = BaseTexel.rgb - Luma;
    BaseTexel.rgb = (Chroma * Saturation) + Luma;

    vec4 PrevTexel = texture2D(PrevSampler, texCoord);

    // 添加衰减因子，减少残影效果
    float decayFactor = 0; // 衰减系数，值越小残影消失越快
    BaseTexel.rgb = max(PrevTexel.rgb * Phosphor * decayFactor, BaseTexel.rgb);

    BaseTexel.a = 1.0;
    gl_FragColor = BaseTexel;
}