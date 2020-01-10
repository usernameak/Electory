package electory.world.gen;

public interface INoiseGenerator {
	default double generate(double s) {
		return generate(s, 0);
	}
	default double generate(double s, double t) {
		return generate(s, t, 0);
	}
	default double generate(double s, double t, double u) {
		throw new UnsupportedOperationException();
	}
}
