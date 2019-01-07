#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec4 vScreenPosition;

uniform sampler2D texture;

void main() {
	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	if(fc.a < 0.1) {
		discard;
	}
	gl_FragData[0] = fc;
	// gl_FragData[1] = vec4((vScreenPosition.z + 1.0) / 2, 1.0, 1.0, 1.0);
}