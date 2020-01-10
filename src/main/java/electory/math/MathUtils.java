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
	
	public static int[][] doPartialSums(int[][] arr) {
		int[][] new_arr = new int[arr.length][arr[0].length];
		
		for(int x = 0; x < arr.length; x++) {
			for(int y = 0; y < arr[0].length; y++) {
				int val = arr[x][y];
				int lval = (x == 0) ? 0 : new_arr[x - 1][y];
				int tval = (y == 0) ? 0 : new_arr[x][y - 1];
				int tlval = (x == 0 || y == 0) ? 0 : new_arr[x - 1][y - 1];
				new_arr[x][y] = val + lval + tval - tlval;
			}
		}
		return new_arr;
	}
	
	public static int getRectangleSum(int[][] partialSums, int x, int y, int width, int height) {
		int val = partialSums[x + width - 1][y + height - 1];
		int lval = (x == 0) ? 0 : partialSums[x - 1][y + height - 1];
		int tval = (y == 0) ? 0 : partialSums[x + width - 1][y - 1];
		int tlval = (x == 0 || y == 0) ? 0 : partialSums[x - 1][y - 1];
		return val - lval - tval + tlval;
	}
}
