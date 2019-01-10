package electory.world.gen.heightmap.postprocessor;

public class RangeRemapPostProcessor implements IHeightMapPostProcessor {
	private float low1;
	private float high1;
	private float low2;
	private float high2;

	public RangeRemapPostProcessor(float low1, float high1, float low2, float high2) {
		super();
		this.low1 = low1;
		this.high1 = high1;
		this.low2 = low2;
		this.high2 = high2;
	}

	@Override
	public int[][] postprocess(int[][] in) {
		for(int i = 0; i < in.length; i++) {
			for(int j = 0; j < in[i].length; j++) {
				in[i][j] = (int) (low2 + (in[i][j] - low1) * (high2 - low2) / (high1 - low1));
			}
		}
		return in;
	}
}
