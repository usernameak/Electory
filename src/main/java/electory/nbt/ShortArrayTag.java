package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ShortArrayTag extends ArrayTag<short[]> {

	public static void register() {
		TagFactory.registerCustomTag(13, ShortArrayTag::new, ShortArrayTag.class);
	}

	public ShortArrayTag() {
		super(new short[0]);
	}

	public ShortArrayTag(short[] value) {
		super(value);
	}

	@Override
	protected short[] getEmptyValue() {
		return new short[0];
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeInt(length());
		for (int i : getValue()) {
			dos.writeShort(i);
		}
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		int length = dis.readInt();
		setValue(new short[length]);
		for (int i = 0; i < length; i++) {
			getValue()[i] = dis.readShort();
		}
	}

	@Override
	public String valueToTagString(int depth) {
		return arrayToString("S", "s");
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other)
				&& (getValue() == ((ShortArrayTag) other).getValue()
				|| getValue().length == (((ShortArrayTag) other).length())
				&& Arrays.equals(getValue(), ((ShortArrayTag) other).getValue()));
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(getValue());
	}

	@Override
	public ShortArrayTag clone() {
		return new ShortArrayTag(Arrays.copyOf(getValue(), length()));
	}
}
