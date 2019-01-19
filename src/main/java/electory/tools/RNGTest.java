package electory.tools;

public class RNGTest {
	private static long xorshift64star(long x) {
		x ^= x >> 12; // a
		x ^= x << 25; // b
		x ^= x >> 27; // c
		return (x * 0x2545F4914F6CDD1DL) ^ 0x132485746548574EL;
	}

	public static void main(String[] args) {
		double v = 0.0;
		
		long seed = xorshift64star(System.currentTimeMillis());
		
		for(int i = 0; i < 16384; i++) {
			long s = xorshift64star(seed ^ i);
			long t = s & 0x001FFFFFFFFFFFFFL;
			double u = t / 9007199254740992.0;
			v += u;
		}
		
		System.out.println(v / 16384.0);
	}
}
