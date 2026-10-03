#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PreviousGainSampler;
uniform float AutoGainEnabled;
uniform float AutoGainSpeed;
uniform float TargetBrightness;
uniform float MinGain;
uniform float MaxGain;

varying vec2 texCoord;
varying vec2 oneTexel;

float luminance(vec3 rgb) {
    return max(max(rgb.r, rgb.g), rgb.b);
}

void main() {
    if (texCoord.x < 0.5 && texCoord.y < 0.5) {
        vec2 scaled = texCoord * 2.0;
        vec3 color = texture2D(DiffuseSampler, scaled).rgb
        + texture2D(DiffuseSampler, scaled + vec2(0.0, oneTexel.y)).rgb
        + texture2D(DiffuseSampler, scaled + vec2(oneTexel.x, 0.0)).rgb
        + texture2D(DiffuseSampler, scaled + oneTexel).rgb;

        float currentLuminance = luminance(color);

        float previousGain = texture2D(PreviousGainSampler, texCoord).r;

        if (AutoGainEnabled > 0.5) {
            float targetGain = (currentLuminance > 0.001) ? (TargetBrightness / currentLuminance) : MaxGain;

            targetGain = clamp(targetGain, MinGain, MaxGain);

            float newGain = mix(previousGain, targetGain, AutoGainSpeed);
            gl_FragColor = vec4(vec3(newGain), 1.0);
        } else {
            gl_FragColor = vec4(vec3(mix(previousGain, 1.0, 0.1)), 1.0);
        }
    } else {
        gl_FragColor = texture2D(PreviousGainSampler, texCoord);
    }
}