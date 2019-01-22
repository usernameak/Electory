package electory.utils;

import electory.nbt.CompoundTag;

public interface IMetaSerializable {
	void writeToNBT(CompoundTag tag);

	void readFromNBT(CompoundTag tag);
}
