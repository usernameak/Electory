package tinycraft.math;

public abstract class MathUtils {
	private MathUtils() {
		
	}
	
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}
}
