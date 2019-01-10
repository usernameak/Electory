package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends ArrayTag<byte[]> {

	public ByteArrayTag() {
		super(new byte[0]);
	}

	public ByteArrayTag(byte[] value) {
		super(value);
	}

	@Override
	protected byte[] getEmptyValue() {
		return new byte[0];
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeInt(length());
		dos.write(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		int length = dis.readInt();
		setValue(new byte[length]);
		dis.readFully(getValue());
	}

	@Override
	public String valueToTagString(int depth) {
		return arrayToString("B", "b");
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && Arrays.equals(getValue(), ((ByteArrayTag) other).getValue());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public ByteArrayTag clone() {
		return new ByteArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
