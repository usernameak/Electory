package electory.world.gen;

public class VoronoiGenerator {
	private long seed;
	private int cellSize;

	public VoronoiGenerator(long seed, int cellSize) {
		this.seed = seed;
		this.cellSize = cellSize;
	}

	private long xorshift64star(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return (x * 0x2545F4914F6CDD1DL) ^ 0x7ffffe24dbaed607L;// ^ 0x132485746548574EL;
	}

	private long xorshift64star_(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return x;
	}

	double normalize(double value, double min, double max) {
		return 1 - ((value - min) / (max - min));
	}

	public double generate(int x_, int y_, int z_) {
		int i = x_;
		int j = y_;
		int k = z_;
		int ix = i / cellSize;
		int iy = j / cellSize;
		int iz = k / cellSize;
		float fx = (i / (float) cellSize - ix);
		float fy = (j / (float) cellSize - iy);
		float fz = (k / (float) cellSize - iz);
		float m_dist = 1f;
		int m_nx = 0;
		int m_ny = 0;
		int m_nz = 0;
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int q = 0; q < 1; q++) {
						int nx = ix + x;
						int ny = iy + y;
						int nz = iz + z;
						long s = xorshift64star(seed);
						long t = xorshift64star(s ^ ny);
						long u = xorshift64star(t ^ nx);
						long u1 = xorshift64star(u ^ nz);
						long v = xorshift64star_(u1 ^ q);
						long w = xorshift64star(u1 ^ q);
						long a = xorshift64star(v);
						long a1 = xorshift64star(xorshift64star_(xorshift64star_(u1 ^ q)));
						long b = a & 0x001FFFFFFFFFFFFFL;
						double c = b / 9007199254740992.0;
						long d = w & 0x001FFFFFFFFFFFFFL;
						double e = d / 9007199254740992.0;
						long f = a1 & 0x001FFFFFFFFFFFFFL;
						double g = f / 9007199254740992.0;
						float rx = (float) c;
						float ry = (float) e;
						float rz = (float) g;
						float dx = x + rx - fx;
						float dy = y + ry - fy;
						float dz = z + rz - fz;
						float dist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
						if (dist < m_dist) {
							m_dist = dist;
							m_nx = nx;
							m_ny = ny;
							m_nz = nz;
						}
					}
				}
			}
		}

		{
			long s = xorshift64star(seed);
			long t = xorshift64star(s ^ m_nx);
			long u = xorshift64star(t ^ m_ny);
			long v = xorshift64star(u ^ m_nz);
			long a = v & 0x001FFFFFFFFFFFFFL;
			double b = a / 9007199254740992.0;
			return b;
		}

	}

}
