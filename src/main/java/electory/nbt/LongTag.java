package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumberTag<Long> {

	public LongTag() {}

	public LongTag(long value) {
		super(value);
	}

	@Override
	protected Long getEmptyValue() {
		return 0L;
	}

	public void setValue(long value) {
		super.setValue(value);
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeLong(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		setValue(dis.readLong());
	}

	@Override
	public String valueToTagString(int depth) {
		return getValue() + "l";
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && asLong() == ((LongTag) other).asLong();
	}

	@Override
	public LongTag clone() {
		return new LongTag(getValue());
	}
}
