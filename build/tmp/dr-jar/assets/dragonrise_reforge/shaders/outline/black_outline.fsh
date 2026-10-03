#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D EntityMask;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform float Radius;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
    vec4 mask = texture2D(EntityMask, texCoord);
    
    if (mask.a > 0.1) {
        fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    } else {
        fragColor = color;
    }
}