package electory.world.gen.condition;

public enum IntegerCondition {
	GREATER {
		@Override
		public boolean doComparsion(int a, int b) {
			return a > b;
		}
	},
	GREATER_OR_EQUAL {
		@Override
		public boolean doComparsion(int a, int b) {
			return a >= b;
		}
	},
	LESS {
		@Override
		public boolean doComparsion(int a, int b) {
			return a < b;
		}
	},
	LESS_OR_EQUAL {
		@Override
		public boolean doComparsion(int a, int b) {
			return a <= b;
		}
	},
	EQUAL {
		@Override
		public boolean doComparsion(int a, int b) {
			return a == b;
		}
	},
	NOT_EQUAL {
		@Override
		public boolean doComparsion(int a, int b) {
			return a != b;
		}
	};
	public abstract boolean doComparsion(int a, int b);
}
