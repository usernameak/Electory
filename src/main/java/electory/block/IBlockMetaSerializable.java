package electory.block;

import electory.nbt.CompoundTag;

public interface IBlockMetaSerializable {
	void writeToNBT(CompoundTag tag);

	void readFromNBT(CompoundTag tag);
}
