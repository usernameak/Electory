package electory.scripting;

public class SpecialToken extends ScriptToken {
	@Override
	public String toString() {
		return "SpecialToken [c=" + c + "]";
	}

	public SpecialToken(char c) {
		super();
		this.c = c;
	}

	private char c;

	public char getChar() {
		return c;
	}
}
