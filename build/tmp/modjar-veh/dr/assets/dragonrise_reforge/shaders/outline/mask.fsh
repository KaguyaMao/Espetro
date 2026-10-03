#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform vec2 OutSize;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
    
    if (color.a > 0.1) {
        fragColor = vec4(1.0, 1.0, 1.0, 1.0);
    } else {
        fragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}