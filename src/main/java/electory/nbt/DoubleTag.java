package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends NumberTag<Double> {

	public DoubleTag() {}

	public DoubleTag(double value) {
		super(value);
	}

	@Override
	protected Double getEmptyValue() {
		return 0.0d;
	}

	public void setValue(double value) {
		super.setValue(value);
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeDouble(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		setValue(dis.readDouble());
	}

	@Override
	public String valueToTagString(int depth) {
		return getValue() + "d";
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((DoubleTag) other).getValue());
	}

	@Override
	public DoubleTag clone() {
		return new DoubleTag(getValue());
	}
}
