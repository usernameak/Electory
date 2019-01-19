package electory.world.gen.biomemap;

import electory.world.BiomeGenBase;

public class FuzzyChunkBlurBiomeMapGenerator implements IBiomeMapGenerator {

	private IBiomeMapGenerator parent;

	public FuzzyChunkBlurBiomeMapGenerator(IBiomeMapGenerator parent) {
		this.parent = parent;
	}

	private int convolveBiomeMap(int[][] map) {
		int[] bcs = new int[BiomeGenBase.biomeList.length];

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				bcs[IBiomeMapGenerator.getPreferredBiome(map[x][y]).biomeID]++;
			}
		}

		int bmax = 0;
		int bval = 0;

		for (int i = 0; i < bcs.length; i++) {
			if (bcs[i] > bval) {
				bmax = i;
				bval = bcs[i];
			}
		}

		return bmax;
	}

	@Override
	public int[][] generateBiomeMap(int cx, int cy) {
		int cv00 = convolveBiomeMap(parent.generateBiomeMap(cx, cy));
		int cv01 = convolveBiomeMap(parent.generateBiomeMap(cx, cy + 1));
		int cv10 = convolveBiomeMap(parent.generateBiomeMap(cx + 1, cy));
		int cv11 = convolveBiomeMap(parent.generateBiomeMap(cx + 1, cy + 1));

		if(cv11 != cv01) {
			if(cv10 == cv01) {
				cv11 = cv01;
			} else if(cv11 == cv10) {
				cv01 = cv11;
			} else {
				cv01 = cv10 = cv11;
			}
		} else if(cv11 != cv10) {
			cv10 = cv11;
		}

		int[][] biomeMap = new int[16][16];

		final int a = 255 / 16;

		for (int x = 0; x < 16; x++) {
			int xl = a * x;
			for (int y = 0; y < 16; y++) {
				int yl = a * y;
				biomeMap[x][y] = cv00 | (cv11 << 8) | (((xl + yl) / 2) << 16);
			}
		}

		return biomeMap;
	}

}
