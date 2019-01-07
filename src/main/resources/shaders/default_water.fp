#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec3 vPosition;

uniform sampler2D texture;
uniform float timer;

float voronoi(vec2 st);

void main() {
	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	gl_FragData[0] = fc;
	gl_FragData[1] = vec4(voronoi(vPosition.xz * 3.0 + 76.45798) * 0.1, 1.0, voronoi(vPosition.xz * 3.0) * 0.1, 1.0);
}