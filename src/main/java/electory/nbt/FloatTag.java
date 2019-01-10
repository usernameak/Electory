package electory.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatTag extends NumberTag<Float> {

	public FloatTag() {}

	public FloatTag(float value) {
		super(value);
	}

	@Override
	protected Float getEmptyValue() {
		return 0.0f;
	}

	public void setValue(float value) {
		super.setValue(value);
	}

	@Override
	public void serializeValue(DataOutput dos, int depth) throws IOException {
		dos.writeFloat(getValue());
	}

	@Override
	public void deserializeValue(DataInput dis, int depth) throws IOException {
		setValue(dis.readFloat());
	}

	@Override
	public String valueToTagString(int depth) {
		return getValue() + "f";
	}

	@Override
	public boolean equals(Object other) {
		return super.equals(other) && getValue().equals(((FloatTag) other).getValue());
	}

	@Override
	public FloatTag clone() {
		return new FloatTag(getValue());
	}
}
