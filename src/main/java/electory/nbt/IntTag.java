package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends NumberTag<Integer> {

	public IntTag() {}

	public IntTag(int value) {
		super(value);
	}

	@Override
	protected Integer getEmptyValue() {
		return 0;
	}

	public void setValue(int value) {
		super.setValue(value);
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeInt(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		setValue(dis.readInt());
	}

	@Override
	public String valueToTagString(int depth) {
		return getValue() + "";
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asInt() == ((IntTag) other).asInt();
	}

	@Override
	public IntTag clone() {
		return new IntTag(getValue());
	}
}
