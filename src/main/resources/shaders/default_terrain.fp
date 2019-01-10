#version 120

varying vec2 vTexCoord;
varying vec4 vColor;
varying vec4 vScreenPosition;
varying vec4 vPosition;
//varying float depth;

uniform sampler2D texture;
uniform sampler2D depth_shadow_texture;

varying vec4 lightPos;
/*
vec3 ftov(float f)
{
    return vec3(floor(f/255.0)/255.0,fract(f/255.0),fract(f));
}
float vtof(vec3 v)
{
    return v.x*65025.0+v.y*255.0+v.z;
}*/

void main() {
	vec4 fc = texture2D(texture, vTexCoord) * vColor;
	if(fc.a < 0.1) {
		discard;
	}
	// fc.rgb = vec3(lightPos.z);
	/*if(texture2D(depth_shadow_texture, lightPos.xy * 0.5 + 0.5).r < (lightPos.z * 0.5 + 0.5) - 0.005) {
		fc.rgb *= 0.5;
	}*/
	gl_FragData[0] = fc;
	//gl_FragData[1] = vec4(ftov(depth*65025.0), fc.a);
}