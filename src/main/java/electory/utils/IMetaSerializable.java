package electory.utils;

import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

import java.io.IOException;

public interface IMetaSerializable {
	void writeToNBT(ArrayDataOutput tag) throws IOException;

	void readFromNBT(ArrayDataInput tag) throws IOException;
}
