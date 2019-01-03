package tinycraft.math;

public class AABB {
	public static final float epsilon = 1.19e-07f;
	public float x0;
	public float y0;
	public float z0;
	public float x1;
	
	@Override
	public String toString() {
		return "AABB [x0=" + x0 + ", y0=" + y0 + ", z0=" + z0 + ", x1=" + x1 + ", y1=" + y1 + ", z1=" + z1 + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x0);
		result = prime * result + Float.floatToIntBits(x1);
		result = prime * result + Float.floatToIntBits(y0);
		result = prime * result + Float.floatToIntBits(y1);
		result = prime * result + Float.floatToIntBits(z0);
		result = prime * result + Float.floatToIntBits(z1);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AABB other = (AABB) obj;
		if (Float.floatToIntBits(x0) != Float.floatToIntBits(other.x0))
			return false;
		if (Float.floatToIntBits(x1) != Float.floatToIntBits(other.x1))
			return false;
		if (Float.floatToIntBits(y0) != Float.floatToIntBits(other.y0))
			return false;
		if (Float.floatToIntBits(y1) != Float.floatToIntBits(other.y1))
			return false;
		if (Float.floatToIntBits(z0) != Float.floatToIntBits(other.z0))
			return false;
		if (Float.floatToIntBits(z1) != Float.floatToIntBits(other.z1))
			return false;
		return true;
	}

	public float y1;
	public float z1;

	public AABB(float x0, float y0, float z0, float x1, float y1, float z1) {
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}

	public AABB expand(float xa, float ya, float za) {
		float _x0 = this.x0;
		float _y0 = this.y0;
		float _z0 = this.z0;
		float _x1 = this.x1;
		float _y1 = this.y1;
		float _z1 = this.z1;
		if (xa < 0.0f) {
			_x0 += xa;
		}
		if (xa > 0.0f) {
			_x1 += xa;
		}
		if (ya < 0.0f) {
			_y0 += ya;
		}
		if (ya > 0.0f) {
			_y1 += ya;
		}
		if (za < 0.0f) {
			_z0 += za;
		}
		if (za > 0.0f) {
			_z1 += za;
		}
		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	public AABB grow(float xa, float ya, float za) {
		float _x0 = this.x0 - xa;
		float _y0 = this.y0 - ya;
		float _z0 = this.z0 - za;
		float _x1 = this.x1 + xa;
		float _y1 = this.y1 + ya;
		float _z1 = this.z1 + za;
		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	public float clipXCollide(AABB c, float xa) {
		float max;
		if (c.y1 <= this.y0)
			return xa;
		if (c.y0 >= this.y1) {
			return xa;
		}
		if (c.z1 <= this.z0)
			return xa;
		if (c.z0 >= this.z1) {
			return xa;
		}
		if (xa > 0.0f && c.x1 <= this.x0 && (max = this.x0 - c.x1 - epsilon) < xa) {
			xa = max;
		}
		if (xa >= 0.0f)
			return xa;
		if (c.x0 < this.x1)
			return xa;
		max = this.x1 - c.x0 + epsilon;
		if (max <= xa)
			return xa;
		return max;
	}

	public float clipYCollide(AABB c, float ya) {
		float max;
		if (c.x1 <= this.x0)
			return ya;
		if (c.x0 >= this.x1) {
			return ya;
		}
		if (c.z1 <= this.z0)
			return ya;
		if (c.z0 >= this.z1) {
			return ya;
		}
		if (ya > 0.0f && c.y1 <= this.y0 && (max = this.y0 - c.y1 - epsilon) < ya) {
			ya = max;
		}
		if (ya >= 0.0f)
			return ya;
		if (c.y0 < this.y1)
			return ya;
		max = this.y1 - c.y0 + epsilon;
		if (max <= ya)
			return ya;
		return max;
	}

	public float clipZCollide(AABB c, float za) {
		float max;
		if (c.x1 <= this.x0)
			return za;
		if (c.x0 >= this.x1) {
			return za;
		}
		if (c.y1 <= this.y0)
			return za;
		if (c.y0 >= this.y1) {
			return za;
		}
		if (za > 0.0f && c.z1 <= this.z0 && (max = this.z0 - c.z1 - epsilon) < za) {
			za = max;
		}
		if (za >= 0.0f)
			return za;
		if (c.z0 < this.z1)
			return za;
		max = this.z1 - c.z0 + epsilon;
		if (max <= za)
			return za;
		return max;
	}

	public boolean intersects(AABB c) {
		if (c.x1 <= this.x0)
			return false;
		if (c.x0 >= this.x1) {
			return false;
		}
		if (c.y1 <= this.y0)
			return false;
		if (c.y0 >= this.y1) {
			return false;
		}
		if (c.z1 <= this.z0)
			return false;
		if (c.z0 < this.z1)
			return true;
		return false;
	}

	public void move(float xa, float ya, float za) {
		this.x0 += xa;
		this.y0 += ya;
		this.z0 += za;
		this.x1 += xa;
		this.y1 += ya;
		this.z1 += za;
	}
}
