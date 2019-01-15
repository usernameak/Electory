package electory.world;

public class ChunkPosition {
	public int x;
	public int z;

	public ChunkPosition(int x, int z) {
		super();
		this.x = x;
		this.z = z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkPosition other = (ChunkPosition) obj;
		if (x != other.x)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChunkPosition [x=" + x + ", z=" + z + "]";
	}

	public static long createLong(int x, int z) {
		return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
	}
}
