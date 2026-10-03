#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform vec2 BlurDir;
uniform float Radius;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = vec4(0.0);
    float total = 0.0;
    
    for (float i = -Radius; i <= Radius; i += 1.0) {
        vec2 offset = BlurDir * i / InSize;
        float weight = 1.0 - abs(i / Radius);
        color += texture2D(DiffuseSampler, texCoord + offset) * weight;
        total += weight;
    }
    
    fragColor = color / total;
}