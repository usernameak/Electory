package electory.world.gen.noise;

public class PoweredNoise implements IWorldNoiseGenerator {

	private final IWorldNoiseGenerator parent;
	private final float power;
	
	public PoweredNoise(IWorldNoiseGenerator parent, float power) {
		super();
		this.parent = parent;
		this.power = power;
	}

	@Override
	public double val(int x, int y) {
		return Math.pow(parent.val(x, y), power);
	}

}
