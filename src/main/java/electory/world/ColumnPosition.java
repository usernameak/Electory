package electory.world;

import lombok.Data;

@Data
public class ColumnPosition {
	public final int x, z;
/*
	public static long createLong(int x, int y, int z) {
		long xx = ((long) x) & 0x1FFFFF | ((long) x) >>> 63 << 21;
		long yy = (((long) y) & 0x07FFFF | ((long) y) >>> 63 << 19) << 22;
		long zz = (((long) z) & 0x1FFFFF | ((long) z) >>> 63 << 21) << 42;
		return xx | yy | zz;
	}

	public static int unpackLongX(long a) {
		return (int) (((a >>> 0) & 0x3FFFFF) << 42 >> 42);
	}

	public static int unpackLongY(long a) {
		return (int) (((a >>> 22) & 0x0FFFFF) << 44 >> 44);
	}

	public static int unpackLongZ(long a) {
		return (int) (((a >>> 42) & 0x3FFFFF) << 42 >> 42);
	}*/
}
