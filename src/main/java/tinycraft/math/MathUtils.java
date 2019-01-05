package tinycraft.math;

public abstract class MathUtils {
	private MathUtils() {
		
	}
	
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public static double clamp(double a, double min, double max) {
		return a < min ? min : (a > max ? max : a);
	}
}
