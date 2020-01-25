package electory.world.gen;

public class PerlinGenerator {
	private long seed;
	private int octaves;
	private double lacunarity;
	private double gain;

	public PerlinGenerator(long seed, int octaves, double lacunarity, double gain) {
		this.seed = seed;
		this.octaves = octaves;
		this.lacunarity = lacunarity;
		this.gain = gain;
	}

	private long xorshift64star(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return (x * 0x2545F4914F6CDD1DL) ^ 0x7ffffe24dbaed607L;//0x132485746548574EL;
	}

	private long xorshift64star_(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return x;
	}

	private double long2dbl(long x) {
		long a = x & 0x001FFFFFFFFFFFFFL;
		double b = a / 9007199254740992.0;
		return b;
	}

	private double random(double s, double t) {
		long a = xorshift64star(seed);
		long b = xorshift64star(a ^ Double.doubleToLongBits(s));
		long c = xorshift64star(b ^ Double.doubleToLongBits(t));
		return long2dbl(c);
	}

	private double mix(double a, double b, double val) {
		return a + val * (b - a);
	}

	public double noise(double s, double t) {
		double is = Math.floor(s);
		double it = Math.floor(t);
		double fs = s - is;
		double ft = t - it;

		// Four corners in 2D of a tile
		double a = random(is, it);
		double b = random(is + 1, it);
		double c = random(is, it + 1);
		double d = random(is + 1, it + 1);

		double us = fs * fs * (3.0 - 2.0 * fs);
		double ut = ft * ft * (3.0 - 2.0 * ft);

		return (mix(a, b, us) + (c - a) * ut * (1.0 - us) + (d - b) * us * ut);
	}

	public double generate(double s, double t) {
		double value = 0;
		double amplitude = .5;
		for (int i = 0; i < octaves; i++) {
			value += amplitude * noise(s, t);
			s *= lacunarity;
			t *= lacunarity;
			amplitude *= gain;
		}
		return value;
	}
}
