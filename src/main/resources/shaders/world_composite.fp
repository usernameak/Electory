#version 120

varying vec2 vTexCoord;
varying vec4 vColor;

uniform sampler2D texture;
uniform sampler2D depth_texture;
uniform sampler2D position_texture;
uniform float timer;
uniform float zFar;
uniform float zNear;
uniform vec3 uCameraPos;

float voronoi(vec2 st);

#define M_E 2.71828182846

//RADIUS of our vignette, where 0.5 results in a circle fitting the screen
const float RADIUS = 0.75;

//softness of our vignette, between 0.0 and 1.0
const float SOFTNESS = 0.45;

float smoothstep2(float edge0, float edge1, float x) {
  // Scale, bias and saturate x to 0..1 range
  x = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0); 
  // Evaluate polynomial
  return x * x * (3.0 - 2.0 * x);
}

void main() {
	
	// water effects

	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	
    // depth init

	float zDist = distance(uCameraPos, texture2D(position_texture, vTexCoord).rgb);
	
	// world fog
	
	float worldZDist = zDist;

    float worldFogRamp = clamp((96 - worldZDist) / (96 - 80), 0.0, 1.0);
    fc.rgb = mix(vec3(0.52, 0.8, 0.92), fc.rgb, worldFogRamp);
	
	// vignette
	
	vec2 vignettePos = vTexCoord - vec2(0.5);
	float len = length(vignettePos); 
	float vignette = smoothstep2(RADIUS, RADIUS-SOFTNESS, len);
	
	fc = mix(fc, fc * vignette, 0.5);
	
	// output color
	
	gl_FragColor = fc;
}