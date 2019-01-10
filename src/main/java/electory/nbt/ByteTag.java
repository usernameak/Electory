package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends NumberTag<Byte> {

	public ByteTag() {}

	public ByteTag(byte value) {
		super(value);
	}

	public ByteTag(boolean value) {
		super((byte) (value ? 1 : 0));
	}

	@Override
	protected Byte getEmptyValue() {
		return 0;
	}

	public boolean asBoolean() {
		return getValue() > 0;
	}

	public void setValue(byte value) {
		super.setValue(value);
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeByte(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		setValue(dis.readByte());
	}

	@Override
	public String valueToTagString(int depth) {
		return getValue() + "b";
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asByte() == ((ByteTag) other).asByte();
	}

	@Override
	public ByteTag clone() {
		return new ByteTag(getValue());
	}
}
