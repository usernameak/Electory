package electory.world.gen.noise;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.Map;

public class DSNoise implements IWorldNoiseGenerator {
	private Map<SimpleImmutableEntry<Integer, Integer>, Double> cache = createLRUMap(32768);
	private final int size;
	private final float roughness;
	private final long seed;

	public DSNoise(int size, float roughness, long seed) {
		this.size = size;
		this.roughness = roughness;
		this.seed = seed;
	}

	public static <K, V> Map<K, V> createLRUMap(final int maxEntries) {
		return new LinkedHashMap<K, V>(maxEntries * 10 / 7, 0.7f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > maxEntries;
			}
		};
	}

	public double val(int x, int y) {
		Double ov = cache.get(new SimpleImmutableEntry<>(x, y));
		if (ov != null) {
			return ov;
		} else {
			if(x <= 0 || x >= size || y <= 0 || y >= size) {
				return 0.0;
			}
			
			int base = 1;
			while ((x & base) == 0 && (y & base) == 0) {
				base <<= 1;
			}
			
			if((x & base) != 0 && (y & base) != 0) {
				squareStep(x, y, base);
			} else {
				diamondStep(x, y, base);
			}
			return cache.get(new SimpleImmutableEntry<>(x, y));
		}
	}

	public double displace(double v, int blockSize, int x, int y) {
		return (v + (this.randFromPair(x, y, this.seed) - 0.5) * blockSize * 2 / this.size * this.roughness);
	}

	private double randFromPair(long x, long y, long seed2) {
		long xm7 = 0, xm13 = 0, xm1301081 = 0, ym8461 = 0, ym105467 = 0, ym105943 = 0;
		for (int i = 0; i < 80; i++) {
			xm7 = x % 7;
			xm13 = x % 13;
			xm1301081 = x % 1301081;
			ym8461 = y % 8461;
			ym105467 = y % 105467;
			ym105943 = y % 105943;
			// y = (i < 40 ? seed : x);
			y = x + this.seed;
			x += (xm7 + xm13 + xm1301081 + ym8461 + ym105467 + ym105943);
		}

		return (xm7 + xm13 + xm1301081 + ym8461 + ym105467 + ym105943) / 1520972.0;
	}

	public double val(int x, int y, double v) {
		cache.put(new SimpleImmutableEntry<>(x, y), Math.max(0.0, Math.min(v, 1.0)));
		return Math.max(0.0, Math.min(v, 1.0));
	}

	public void squareStep(int x, int y, int blockSize) {
		if (cache.get(new SimpleImmutableEntry<>(x, y)) == null) {
			this
					.val(x, y, this
							.displace((this.val(x - blockSize, y - blockSize) + this.val(x + blockSize, y - blockSize)
									+ this.val(x - blockSize, y + blockSize) + this.val(x + blockSize, y + blockSize))
									/ 4, blockSize, x, y));
		}
	}

	public void diamondStep(int x, int y, int blockSize) {
		if (cache.get(new SimpleImmutableEntry<>(x, y)) == null) {
			this
					.val(x, y,
							this
									.displace(
											(this.val(x - blockSize, y) + this.val(x + blockSize, y)
													+ this.val(x, y - blockSize) + this.val(x, y + blockSize)) / 4,
											blockSize, x, y));
		}
	}
}
