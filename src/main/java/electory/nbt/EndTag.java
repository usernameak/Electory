package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;

public final class EndTag extends Tag<Void> {

	static final EndTag INSTANCE = new EndTag();

	private EndTag() {}

	@Override
	protected Void getEmptyValue() {
		return null;
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) {
		//nothing to do
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) {
		//nothing to do
	}

	@Override
	public String valueToString(int depth) {
		return "\"end\"";
	}

	@Override
	public String valueToTagString(int depth) {
		throw new UnsupportedOperationException("EndTag cannot be turned into a String");
	}

	@Override
	public int compareTo(Tag<Void> o) {
		return 0;
	}

	@Override
	public EndTag clone() {
		return INSTANCE;
	}
}
