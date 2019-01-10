package electory.client.render.shader;

public class ShaderCompileException extends Exception {
	private static final long serialVersionUID = 1L;

	public ShaderCompileException() {
		super();
	}

	public ShaderCompileException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ShaderCompileException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ShaderCompileException(String arg0) {
		super(arg0);
	}

	public ShaderCompileException(Throwable arg0) {
		super(arg0);
	}

}
