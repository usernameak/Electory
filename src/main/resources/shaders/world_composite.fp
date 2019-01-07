#version 120

varying vec2 vTexCoord;
varying vec4 vColor;

uniform sampler2D texture;
uniform sampler2D watermask_texture;
uniform sampler2D depth_texture;
uniform sampler2D opaque_depth_texture;
uniform float timer;
uniform bool isSubmergedUnderwater = false;

float voronoi(vec2 st);

//RADIUS of our vignette, where 0.5 results in a circle fitting the screen
const float RADIUS = 0.75;

//softness of our vignette, between 0.0 and 1.0
const float SOFTNESS = 0.45;

void main() {
	vec2 tTexCoord = vTexCoord;
	tTexCoord += texture2D(watermask_texture, vTexCoord).rb * .1;
	
	vec2 uwvoro = vec2(voronoi(vTexCoord * 30 + 76.45798) * 0.01, voronoi(vTexCoord * 30) * 0.01);
	
	if(isSubmergedUnderwater) {
		tTexCoord += uwvoro * 0.3;
	}
	if(texture2D(watermask_texture, vTexCoord).g > 0.5 && texture2D(watermask_texture, tTexCoord).g < 0.5) {
		tTexCoord = vTexCoord;
	}
	
	vec4 fc = texture2D(texture, tTexCoord) * vColor;
	
	float odd = texture2D(opaque_depth_texture, vTexCoord).r - texture2D(depth_texture, vTexCoord).r;
	float d = 1.0 - odd * 1000.0; //(odd - 0.9999) * (1.0 / 0.0001);
	
	d = clamp(d, 0.0, 1.0);
	/*fc.rgb += d * vec3(0.52, 0.8, 0.92);*/
	
	// TODO: make better coefficients
	
	if(isSubmergedUnderwater) {
		fc.rgb = mix(fc.rgb, vec3(0.14117, 0.14117, 1.0), clamp((texture2D(depth_texture, vTexCoord).r - 0.9999) * (1.0 / 0.0001), 0.0, 1.0));
	} else {
		fc.rgb = mix(fc.rgb, mix(fc.rgb, vec3(0.14117, 0.14117, 1.0), d), texture2D(watermask_texture, vTexCoord).g);
	}
	
	fc = mix(fc, vec4(1.0), texture2D(watermask_texture, vTexCoord).b);
	
	if(isSubmergedUnderwater) {
		fc.rgb = mix(fc.rgb, vec3(0.14117, 0.14117, 1.0), 0.5);
		fc.rgb = mix(fc.rgb, vec3(1.0), uwvoro.y * 10.0);
	}
	
	// fc.rgb = vec3(d);
	
	vec2 vignettePos = vTexCoord - vec2(0.5);
	float len = length(vignettePos); 
	float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);
	fc = mix(fc, fc * vignette, 0.5);
	
	if(fc.a < 0.1) {
		discard;
	}
	gl_FragColor = fc;
}