package electory.math;

public class AABB implements Cloneable {
	public static final double epsilon = 1.19e-07f;
	public double x0;
	public double y0;
	public double z0;
	public double x1;
	
	@Override
	public String toString() {
		return "AABB [x0=" + x0 + ", y0=" + y0 + ", z0=" + z0 + ", x1=" + x1 + ", y1=" + y1 + ", z1=" + z1 + "]";
	}
	
	public AABB copy() {
		return new AABB(x0, y0, z0, x1, y1, z1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x0);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y0);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z0);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (Double.doubleToLongBits(x0) != Double.doubleToLongBits(other.x0))
			return false;
		if (Double.doubleToLongBits(x1) != Double.doubleToLongBits(other.x1))
			return false;
		if (Double.doubleToLongBits(y0) != Double.doubleToLongBits(other.y0))
			return false;
		if (Double.doubleToLongBits(y1) != Double.doubleToLongBits(other.y1))
			return false;
		if (Double.doubleToLongBits(z0) != Double.doubleToLongBits(other.z0))
			return false;
		if (Double.doubleToLongBits(z1) != Double.doubleToLongBits(other.z1))
			return false;
		return true;
	}

	public double y1;
	public double z1;

	public AABB(double x0, double y0, double z0, double x1, double y1, double z1) {
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	}

	public AABB expand(double xa, double ya, double za) {
		double _x0 = this.x0;
		double _y0 = this.y0;
		double _z0 = this.z0;
		double _x1 = this.x1;
		double _y1 = this.y1;
		double _z1 = this.z1;
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

	public AABB grow(double xa, double ya, double za) {
		double _x0 = this.x0 - xa;
		double _y0 = this.y0 - ya;
		double _z0 = this.z0 - za;
		double _x1 = this.x1 + xa;
		double _y1 = this.y1 + ya;
		double _z1 = this.z1 + za;
		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	public double clipXCollide(AABB c, double xa) {
		double max;
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

	public double clipYCollide(AABB c, double ya) {
		double max;
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

	public double clipZCollide(AABB c, double za) {
		double max;
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

	public void move(double xa, double ya, double za) {
		this.x0 += xa;
		this.y0 += ya;
		this.z0 += za;
		this.x1 += xa;
		this.y1 += ya;
		this.z1 += za;
	}
}
