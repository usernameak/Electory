package electory.utils;

import electory.obf.Keep;

@Keep
public class CrashException extends RuntimeException {
	private static final long serialVersionUID = -5741648382068154712L;

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
