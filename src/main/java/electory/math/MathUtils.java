package electory.math;

public abstract class MathUtils {
	private MathUtils() {

	}

	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	public static double clamp(double a, double min, double max) {
		return a < min ? min : (a > max ? max : a);
	}

	public static float rangeRemap(float in, float low1, float high1, float low2, float high2) {
		return (low2 + (in - low1) * (high2 - low2) / (high1 - low1));
	}
	
	public static int floor_double(double in) {
		return (int) Math.floor(in);
	}
}
