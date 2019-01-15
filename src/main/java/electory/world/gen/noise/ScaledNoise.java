package electory.world.gen.noise;

public class ScaledNoise implements IWorldNoiseGenerator {
	private double scaleX, scaleY;
	private IWorldNoiseGeneratord input;

	public ScaledNoise(IWorldNoiseGeneratord input, double scaleX, double scaleY) {
		super();
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.input = input;
	}

	@Override
	public double val(int x, int y) {
		return input.val(scaleX * x, scaleY * y);
	}
	
	
}
