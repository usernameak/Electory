package electory.world.gen.noise;

import java.util.Random;

public class FBM implements IWorldNoiseGeneratord {
	// private double delta = 16; // scales down all layers, probably would be
	// better in the constructor
	public int octaves; // number of raw Perlin noise samples
	/*
	 * private double[] offset; // picks the z-value for each layer private double[]
	 * cos; // cosine table for rotating the noise layers private double[] sin;
	 * private double[] amplitude; // how prominent each layer is private double[]
	 * frequency; // scale of each layer
	 */

	private double[] amplitudes;

	private double[] doubleSeeds = new double[3];
	
	private long seed;

	private Random random = new Random();

	public FBM(int octaves, long seed) {
		this.octaves = octaves;

		this.amplitudes = new double[octaves];
		
		this.seed = seed;

		Random r = new Random(seed);

		for (int i = 0; i < doubleSeeds.length; i++) {
			doubleSeeds[i] = r.nextDouble() * 2000d;
		}

		double amplitude = .5;

		for (int i = 0; i < octaves; i++) {
			this.amplitudes[i] = amplitude;
			amplitude *= .5;
		}
	}
	
	private long xorshift64star(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return (x * 0x2545F4914F6CDD1DL) ^ 0x132485746548574EL;
	}
	
	private double random(double x, double y) {
		/*random.setSeed(seed);
		random.setSeed(random.nextLong() ^ Double.doubleToLongBits(x));
		random.setSeed(random.nextLong() ^ Double.doubleToLongBits(y));
		return random.nextDouble();*/
		// TODO: make it less predictable
		long a = xorshift64star(xorshift64star(seed) ^ Double.doubleToLongBits(x)) ^ Double.doubleToLongBits(y);
		long s = xorshift64star(a);
		long t = s & 0x001FFFFFFFFFFFFFL;
		double u = t / 9007199254740992.0;
		return u;
	}

	private double lerp(double a, double b, double f) {
		return a + f * (b - a);
	}

	private double noise(double x, double y) {
		double xi = Math.floor(x);
		double yi = Math.floor(y);
		double xf = x - xi;
		double yf = y - yi;

		double a = random(xi, yi);
		double b = random(xi + 1.0, yi);
		double c = random(xi, yi + 1.0);
		double d = random(xi + 1.0, yi + 1.0);

		double xu = xf * xf * (3.0 - 2.0 * xf);
		double yu = yf * yf * (3.0 - 2.0 * yf);

		return ((lerp(a, b, xu) + (c - a) * yu * (1.0 - xu) + (d - b) * yu * xu) - 0.5f) * 2.0f;
	}

	public double val(double x, double y) {
		// Initial values
		double value = 0.0;
		
		// Loop of octaves
		for (int i = 0; i < octaves; i++) {
			value += amplitudes[i] * noise(x, y);
			x *= 2.;
			y *= 2.;
		}
		return value * 0.5f + 0.5f;
	}

}