package electory.world.gen.biomemap;

import electory.world.BiomeGenBase;

public class VoronoiBiomeMapGenerator implements IBiomeMapGenerator {

	private BiomeGenBase[] allowedBiomes;
	private long seed;

	public VoronoiBiomeMapGenerator(long seed, BiomeGenBase... allowedBiomes) {
		this.allowedBiomes = allowedBiomes;
		this.seed = seed;
	}

	private long xorshift64star(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return (x * 0x2545F4914F6CDD1DL) ^ 0x132485746548574EL;
	}

	private long xorshift64star_(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return x;
	}

	@Override
	public int[][] generateBiomeMap(int cx, int cy) {
		int[][] biomeData = new int[16][16];

		for (int i_ = 0; i_ < 16; i_++) {
			for (int j_ = 0; j_ < 16; j_++) {
				int i = i_ | (cx << 4);
				int j = j_ | (cy << 4);
				int ix = i / 256;
				int iy = j / 256;
				float fx = (i / 256.f - ix);
				float fy = (j / 256.f - iy);
				float m_dist = 1f;
				int m_nx = 0;
				int m_ny = 0;
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						for (int k = 0; k < 16; k++) {
							int nx = ix + x;
							int ny = iy + y;
							long s = xorshift64star(seed);
							long t = xorshift64star(s ^ ny);
							long u = xorshift64star(t ^ nx);
							long v = xorshift64star_(u ^ k);
							long w = xorshift64star(u ^ k);
							long a = xorshift64star(v);
							long b = a & 0x001FFFFFFFFFFFFFL;
							double c = b / 9007199254740992.0;
							long d = w & 0x001FFFFFFFFFFFFFL;
							double e = d / 9007199254740992.0;
							float rx = (float) c;
							float ry = (float) e;
							float dx = x + rx - fx;
							float dy = y + ry - fy;
							float dist = (float) Math.sqrt(dx * dx + dy * dy);
							if (dist < m_dist) {
								m_dist = dist;
								m_nx = nx;
								m_ny = ny;
							}
						}
					}
				}
				long c = xorshift64star(xorshift64star(m_ny) ^ m_nx);
				int v = allowedBiomes[(int) ((int) c) & 0xFFFF % allowedBiomes.length].biomeID;
				biomeData[i_][j_] = v | (v << 8) | (0x7F << 16);
			}
		}

		return biomeData;
	}

}
