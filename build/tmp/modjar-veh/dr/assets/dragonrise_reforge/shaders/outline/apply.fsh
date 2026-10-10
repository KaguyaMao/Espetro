#version 150

uniform sampler2D DiffuseSampler;
uniform vec4 OutlineColor;
uniform int UseSourceColor;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);
    
    if (UseSourceColor == 1) {
        fragColor = vec4(color.rgb, color.a) * OutlineColor;
    } else {
        fragColor = OutlineColor * color.a;
    }
}