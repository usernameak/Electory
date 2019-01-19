#version 120

uniform float timer;

float random(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

vec2 random2(vec2 co) {
    return vec2(random(co), random(co + 647.645));
}

vec3 random3(vec3 p) {
  return fract(
      sin(vec3(dot(p, vec3(1.0, 57.0, 113.0)), dot(p, vec3(57.0, 113.0, 1.0)),
               dot(p, vec3(113.0, 1.0, 57.0)))) *
      43758.5453);
}

float voronoi(vec2 st) {
    // Tile the space
    vec2 i_st = floor(st);
    vec2 f_st = fract(st);

    float m_dist = 1.;  // minimun distance

    for (int y= -1; y <= 1; y++) {
        for (int x= -1; x <= 1; x++) {
            // Neighbor place in the grid
            vec2 neighbor = vec2(float(x),float(y));

            // Random position from current + neighbor place in the grid
            vec2 point = random2(i_st + neighbor);

			// Animate the point
            point = 0.5 + 0.5*sin(timer * 0.1 + 6.2831*point);

			// Vector between the pixel and the point
            vec2 diff = neighbor + point - f_st;

            // Distance to the point
            float dist = length(diff);

            // Keep the closer distance
            m_dist = min(m_dist, dist);
        }
    }
    
    return m_dist;
}

float voronoi(vec3 st) {
    // Tile the space
    vec3 i_st = floor(st);
    vec3 f_st = fract(st);

    float m_dist = 1.;  // minimun distance

    for (int y= -1; y <= 1; y++) {
        for (int x= -1; x <= 1; x++) {
        	for (int z= -1; z <= 1; z++) {
	            // Neighbor place in the grid
	            vec3 neighbor = vec3(float(x),float(y),float(z));
	
	            // Random position from current + neighbor place in the grid
	            vec3 point = random3(i_st + neighbor);
	
				// Animate the point
	            point.xyz = 0.5 + 0.5*sin(timer * 0.1 + 6.2831*point.xyz);
	
				// Vector between the pixel and the point
	            vec3 diff = neighbor + point - f_st;
	
	            // Distance to the point
	            float dist = length(diff);
	
	            // Keep the closer distance
	            m_dist = min(m_dist, dist);
            }
        }
    }
    
    return m_dist;
}