package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends NumberTag<Short> {

	public ShortTag() {}

	public ShortTag(short value) {
		super(value);
	}

	@Override
	protected Short getEmptyValue() {
		return 0;
	}

	public void setValue(short value) {
		super.setValue(value);
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeShort(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		setValue(dis.readShort());
	}

	@Override
	public String valueToTagString(int depth) {
		return getValue() + "s";
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asShort() == ((ShortTag) other).asShort();
	}

	@Override
	public ShortTag clone() {
		return new ShortTag(getValue());
	}
}
