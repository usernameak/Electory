package electory.world.gen.heightmap.postprocessor;

public class Lerp4HeightMapPostProcessor implements IHeightMapPostProcessor {
	protected float lerp(float s, float e, float t) {
		return s + (e - s) * t;
	}

	protected float blerp(float c00, float c10, float c01, float c11, float tx, float ty) {
		return lerp(lerp(c00, c10, tx), lerp(c01, c11, tx), ty);
	}
	
	@Override
	public int[][] postprocess(int[][] heightData) {
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				float tx = ((i) - (i - i % 4)) / 4.f;
				float ty = ((j) - (j - j % 4)) / 4.f;
				heightData[i][j] = Math.round(blerp(heightData[i - i % 4][j - j % 4], heightData[i - i % 4 + 4][j - j % 4],
						heightData[i - i % 4][j - j % 4 + 4], heightData[i - i % 4 + 4][j - j % 4 + 4], tx, ty));
			}
		}
		
		return heightData;
	}

}
