package electory.utils;

public class CrashException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CrashException() {
		super();
	}

	public CrashException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CrashException(String message, Throwable cause) {
		super(message, cause);
	}

	public CrashException(String message) {
		super(message);
	}

	public CrashException(Throwable cause) {
		super(cause);
	}

}
