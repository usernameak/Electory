package electory.utils;

public enum EnumSide {
	/** -Y */
	DOWN(0, -1, 0, EnumAxis.Y),

	/** +Y */
	UP(0, 1, 0, EnumAxis.Y),

	/** -Z */
	NORTH(0, 0, -1, EnumAxis.Z),

	/** +Z */
	SOUTH(0, 0, 1, EnumAxis.Z),

	/** -X */
	WEST(-1, 0, 0, EnumAxis.X),

	/** +X */
	EAST(1, 0, 0, EnumAxis.X),

	/**
	 * Used only by getOrientation, for invalid inputs
	 */
	UNKNOWN(0, 0, 0, EnumAxis.X);

	public final int offsetX;
	public final int offsetY;
	public final int offsetZ;
	public final EnumAxis axis;
	public final int flag;
	public static final EnumSide[] VALID_DIRECTIONS = { DOWN, UP, NORTH, SOUTH, WEST, EAST };
	public static final EnumSide[] VALID_DIRECTIONS_UPFIRST = { UP, NORTH, SOUTH, WEST, EAST, DOWN };
	public static final EnumSide[] VALID_DIRECTIONS_DOWNFIRST = { DOWN, EAST, WEST, SOUTH, NORTH, UP };
	public static final int[] OPPOSITES = { 1, 0, 3, 2, 5, 4, 6 };
	// Left hand rule rotation matrix for all possible axes of rotation
	public static final int[][] ROTATION_MATRIX = { { 0, 1, 4, 5, 3, 2, 6 }, { 0, 1, 5, 4, 2, 3, 6 },
			{ 5, 4, 2, 3, 0, 1, 6 }, { 4, 5, 2, 3, 1, 0, 6 }, { 2, 3, 1, 0, 4, 5, 6 }, { 3, 2, 0, 1, 4, 5, 6 },
			{ 0, 1, 2, 3, 4, 5, 6 }, };

	private EnumSide(int x, int y, int z, EnumAxis axis) {
		offsetX = x;
		offsetY = y;
		offsetZ = z;
		this.axis = axis; 
		flag = 1 << ordinal();
	}

	public static EnumSide getOrientation(int id) {
		if (id >= 0 && id < VALID_DIRECTIONS.length) {
			return VALID_DIRECTIONS[id];
		}
		return UNKNOWN;
	}

	public EnumSide getOpposite() {
		return getOrientation(OPPOSITES[ordinal()]);
	}

	public EnumSide getRotation(EnumSide axis) {
		return getOrientation(ROTATION_MATRIX[axis.ordinal()][ordinal()]);
	}
}
