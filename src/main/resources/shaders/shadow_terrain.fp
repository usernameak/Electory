#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
uniform sampler2D texture;

void main() {
	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	if(fc.a < 0.1) {
		discard;
	}
	gl_FragColor = vec4(1.0);
}