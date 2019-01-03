package tinycraft.math;

import static java.lang.Math.max;
import static java.lang.Math.min;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Ray {
	public final Vector3f origin;
	public final Vector3f direction;

	public Ray(Vector3f origin, Vector3f direction) {
		super();
		this.origin = origin;
		this.direction = direction;
	}

	public Ray rotate(AxisAngle4f rotation) {
		return new Ray(origin, direction.rotate(new Quaternionf(rotation)).normalize());
	}

	public AABBIntersectionResult intersectsAABB(AABB aabb) {
		Vector3f dirfrac = new Vector3f(1f / direction.x, 1f / direction.y, 1f / direction.z);

		float t1 = (aabb.x0 - origin.x) * dirfrac.x;
		float t2 = (aabb.x1 - origin.x) * dirfrac.x;
		float t3 = (aabb.y0 - origin.y) * dirfrac.y;
		float t4 = (aabb.y1 - origin.y) * dirfrac.y;
		float t5 = (aabb.z0 - origin.z) * dirfrac.z;
		float t6 = (aabb.z1 - origin.z) * dirfrac.z;

		float tmin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));
		float tmax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));
		
		if(tmax < 0 || tmin > tmax) {
			return new AABBIntersectionResult(false, tmax);
		}

		return new AABBIntersectionResult(true, tmin);
	}
	
	public static class AABBIntersectionResult {
		public final boolean hasHit;
		public final float distance;
		
		public AABBIntersectionResult(boolean hasHit, float distance) {
			super();
			this.hasHit = hasHit;
			this.distance = distance;
		}
	}
}
