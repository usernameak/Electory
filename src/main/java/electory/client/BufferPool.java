package electory.client;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Comparator;

import org.lwjgl.BufferUtils;

public class BufferPool {
	private static ThreadLocal<BufferPool> local = new ThreadLocal<BufferPool>() {
		@Override
		protected BufferPool initialValue() {
			return new BufferPool();
		}
	};

	public static BufferPool get() {
		return local.get();
	}

	private FloatBuffer[] floatBuffers = new FloatBuffer[16];
	
	private int floatBufferSize = 0;
	
	private class FBComparator implements Comparator<FloatBuffer> {

		@Override
		public int compare(FloatBuffer buf, FloatBuffer unused) {
			return buf == null ? -floatBufferSize :  buf.capacity() - floatBufferSize;
		}
		
	}
	
	private FBComparator fbComparator = new FBComparator();

	public FloatBuffer getFloatBuffer(final int size) {
		floatBufferSize = size;
		int ofs = Arrays.binarySearch(floatBuffers, null, fbComparator);
		
		if(ofs >= 0) {
			floatBuffers[ofs].clear();
			return floatBuffers[ofs];
		} else {
			int insertOfs = -(ofs + 1);
			FloatBuffer fb = BufferUtils.createFloatBuffer(size);
			floatBuffers[insertOfs >= floatBuffers.length ? floatBuffers.length - 1 : insertOfs] = fb;
			return fb;
		}
	}
}
