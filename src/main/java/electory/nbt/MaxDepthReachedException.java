package electory.nbt;

public class MaxDepthReachedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MaxDepthReachedException(String msg) {
		super(msg);
	}
}
