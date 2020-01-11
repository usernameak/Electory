package electory.scripting;

public class StringToken extends ScriptToken {

	@Override
	public String toString() {
		return "StringToken [str=" + str + "]";
	}

	private String str;

	public StringToken(String s) {
		this.str = s;
	}

	public String getStr() {
		return str;
	}

}
