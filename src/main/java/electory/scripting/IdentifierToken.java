package electory.scripting;

public class IdentifierToken extends ScriptToken {

	private String id;

	public IdentifierToken(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "IdentifierToken [id=" + id + "]";
	}

}
