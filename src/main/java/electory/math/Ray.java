package electory.math;

import static java.lang.Math.max;
import static java.lang.Math.min;

import org.joml.AxisAngle4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import electory.utils.EnumSide;

public class Ray {
	public final Vector3d origin;
	public final Vector3d direction;

	public Ray(Vector3d origin, Vector3d direction) {
		super();
		this.origin = origin;
		this.direction = direction;
	}

	public Ray rotate(AxisAngle4f rotation) {
		return new Ray(origin, direction.rotate(new Quaterniond(rotation)).normalize());
	}

	public AABBIntersectionResult intersectsAABB(AABB aabb) {
		Vector3d dirfrac = new Vector3d(1f / direction.x, 1f / direction.y, 1f / direction.z);

		double t1 = (aabb.x0 - origin.x) * dirfrac.x;
		double t2 = (aabb.x1 - origin.x) * dirfrac.x;
		double t3 = (aabb.y0 - origin.y) * dirfrac.y;
		double t4 = (aabb.y1 - origin.y) * dirfrac.y;
		double t5 = (aabb.z0 - origin.z) * dirfrac.z;
		double t6 = (aabb.z1 - origin.z) * dirfrac.z;

		double tmin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));
		double tmax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));
		
		if(tmax < 0 || tmin > tmax) {
			return new AABBIntersectionResult(false, tmax, EnumSide.UNKNOWN);
		}

		EnumSide side = EnumSide.UNKNOWN;
		if(tmin == t1) {
			side = EnumSide.WEST;
		} else if(tmin == t2) {
			side = EnumSide.EAST;
		} else if(tmin == t3) {
			side = EnumSide.DOWN;
		} else if(tmin == t4) {
			side = EnumSide.UP;
		} else if(tmin == t5) {
			side = EnumSide.NORTH;
		} else if(tmin == t6) {
			side = EnumSide.SOUTH;
		}
		return new AABBIntersectionResult(true, tmin, side);
	}
	
	public static class AABBIntersectionResult {
		public final boolean hasHit;
		public final double distance;
		public final EnumSide side;
		
		public AABBIntersectionResult(boolean hasHit, double distance, EnumSide side) {
			super();
			this.hasHit = hasHit;
			this.distance = distance;
			this.side = side;
		}
	}
}
