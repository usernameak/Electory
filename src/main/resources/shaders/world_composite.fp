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
	
    // depth init

	float depth = texture2D(depth_texture, tTexCoord).r;
	float zDist = (zNear * zFar) / (zFar - depth * (zFar - zNear));
	float opaquedepth = texture2D(opaque_depth_texture, tTexCoord).r;
	float opaqueZDist = (zNear * zFar) / (zFar - opaquedepth * (zFar - zNear));
	
	// world fog
	
	float worldZDist = zDist;
	if(isSubmergedUnderwater) {
		if(texture2D(watermask_texture, tTexCoord).g > 0.5) {
			worldZDist = opaqueZDist - zDist;
		} else {
			worldZDist = 0;
		}
		float worldFogRamp = clamp((96 - worldZDist) / (96 - 80), 0.0, 1.0);
		fc.rgb = mix(vec3(0.52, 0.8, 0.92), fc.rgb, worldFogRamp);
	}
	
	// water fog
	
	float odd = opaqueZDist - zDist;
	
	vec3 underwaterColor = vec3(0.098, 0.298, 0.3412);
	
	if(isSubmergedUnderwater) {
		float waterFogRamp = 1 / pow(M_E, zDist * 0.1);
		fc.rgb = mix(underwaterColor, fc.rgb, waterFogRamp);
	} else {
		float waterFogRamp = 1 / pow(M_E, odd * 0.1);
		fc.rgb = mix(fc.rgb, mix(underwaterColor, fc.rgb, waterFogRamp), texture2D(watermask_texture, vTexCoord).g);
	}
	
	fc = mix(fc, vec4(1.0), texture2D(watermask_texture, vTexCoord).b * 0.4);
	
	if(isSubmergedUnderwater) {
		fc.rgb = mix(fc.rgb, underwaterColor, 0.5);
		fc.rgb = mix(fc.rgb, vec3(1.0), uwvoro.y * 2.0);
	}
	
	// world fog cont.
	
	if(!isSubmergedUnderwater) {
		float worldFogRamp = clamp((96 - worldZDist) / (96 - 80), 0.0, 1.0);
		fc.rgb = mix(vec3(0.52, 0.8, 0.92), fc.rgb, worldFogRamp);
	}
	
	// vignette
	
	vec2 vignettePos = vTexCoord - vec2(0.5);
	float len = length(vignettePos); 
	float vignette = smoothstep2(RADIUS, RADIUS-SOFTNESS, len);
	
	fc = mix(fc, fc * vignette, 0.5);
	
	// alpha test
	/*
	if(fc.a < 0.1) {
		discard;
	}*/
	
	// output color
	
	gl_FragColor = fc;
}