#version 120

varying vec4 vColor;
uniform vec4 uColor = vec4(1.0);

void main() {
	gl_FragColor = uColor * vColor;
}