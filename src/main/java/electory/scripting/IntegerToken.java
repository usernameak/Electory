package electory.scripting;

public class IntegerToken extends ScriptToken {
	@Override
	public String toString() {
		return "IntegerToken [val=" + val + "]";
	}

	private int val;

	public IntegerToken(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}
}
