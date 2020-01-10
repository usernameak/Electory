#version 120

varying vec2 vTexCoord;
varying vec4 vColor;

uniform sampler2D texture;
uniform sampler2D watermask_texture;
uniform sampler2D depth_texture;
uniform sampler2D opaque_depth_texture;
uniform sampler2D depth_shadow_texture;
uniform float timer;
uniform float zFar;
uniform float zNear;
uniform bool isSubmergedUnderwater = false;

float voronoi(vec2 st);

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
	float depth = texture2D(depth_texture, vTexCoord).r;
	float zDist = (zNear * zFar) / (zFar - depth * (zFar - zNear));
	float opaquedepth = texture2D(opaque_depth_texture, vTexCoord).r;
	float opaqueZDist = (zNear * zFar) / (zFar - opaquedepth * (zFar - zNear));
	// return;
	vec2 tTexCoord = vTexCoord;
	tTexCoord += texture2D(watermask_texture, vTexCoord).rb * .1;
	
	vec2 uwvoro = vec2(voronoi(vTexCoord * 30.0 + 76.45798) * 0.01, voronoi(vTexCoord * 30.0) * 0.01);
	
	if(isSubmergedUnderwater) {
		tTexCoord += uwvoro * 0.3;
	}
	if(texture2D(watermask_texture, vTexCoord).g > 0.5 && texture2D(watermask_texture, tTexCoord).g < 0.5) {
		tTexCoord = vTexCoord;
	}
	
	vec4 fc = texture2D(texture, tTexCoord) * vColor;
	
	float odd = opaqueZDist - zDist;
	
	vec3 underwaterColor = vec3(0.098, 0.298, 0.3412);
	
	if(isSubmergedUnderwater) {
		float waterFogRamp = clamp((50 - zDist) / (50 - 2), 0.0, 1.0);
		fc.rgb = mix(underwaterColor, fc.rgb, waterFogRamp);
	} else {
		float waterFogRamp = clamp((50 - odd) / (50 - 2), 0.0, 1.0);
		fc.rgb = mix(fc.rgb, mix(underwaterColor, fc.rgb, waterFogRamp), texture2D(watermask_texture, vTexCoord).g);
	}
	
	fc = mix(fc, vec4(1.0), texture2D(watermask_texture, vTexCoord).b * 0.4);
	
	if(isSubmergedUnderwater) {
		fc.rgb = mix(fc.rgb, underwaterColor, 0.5);
		fc.rgb = mix(fc.rgb, vec3(1.0), uwvoro.y * 2.0);
	}
	
	vec2 vignettePos = vTexCoord - vec2(0.5);
	float len = length(vignettePos); 
	float vignette = smoothstep2(RADIUS, RADIUS-SOFTNESS, len);
	fc = mix(fc, fc * vignette, 0.5);
	
	if(fc.a < 0.1) {
		discard;
	}
	gl_FragColor = fc;
}