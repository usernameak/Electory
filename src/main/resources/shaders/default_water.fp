#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec4 vPosition;

uniform sampler2D texture;
uniform float timer;
uniform vec3 waterPositionOffset;

float voronoi(vec2 st);
float voronoi(vec3 stu);

void main() {
	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	gl_FragData[0] = fc;
	vec3 waterPos = waterPositionOffset.xzy + vPosition.xzy;
	gl_FragData[1] = vec4(voronoi(waterPos * 3.0 + 76.45798) * 0.1, 1.0, voronoi(waterPos * 3.0) * 0.1, 1.0);
}