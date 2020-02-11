package electory.nbt;

public class MaxDepthReachedException extends RuntimeException {
	private static final long serialVersionUID = -4047326743940513170L;

	public MaxDepthReachedException(String msg) {
		super(msg);
	}
}
