#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec4 vScreenPosition;
varying vec4 vPosition;

uniform sampler2D texture;
uniform sampler2D depth_shadow_texture;

varying vec4 lightPos;

void main() {
	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	if(fc.a < 0.1) {
		discard;
	}

	gl_FragData[0] = fc;
	gl_FragData[1] = vPosition;
}