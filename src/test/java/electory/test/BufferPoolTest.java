package electory.test;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import electory.client.BufferPool;

public class BufferPoolTest {
	@Before
	public void beforeEach() {
		BufferPool.get().new TestAccessor().cleanup();
	}

	@Test
	public void bufferPoolIsEmptyByDefault() {
		BufferPool pool = BufferPool.get();
		BufferPool.TestAccessor accessor = pool.new TestAccessor();

		for (FloatBuffer fb : accessor.getFloatBuffers()) {
			Assert.assertNull(fb);
		}
	}

	@Test
	public void bufferPoolAllocatesFirstBufferOnEnd() {
		BufferPool pool = BufferPool.get();
		BufferPool.TestAccessor accessor = pool.new TestAccessor();

		pool.getFloatBuffer(16);

		int l = accessor.getFloatBuffers().length;
		FloatBuffer[] fbs = accessor.getFloatBuffers();

		for (int i = 0; i < l; i++) {
			if (i == l - 1) {
				Assert.assertNotNull(fbs[i]);
			} else {
				Assert.assertNull(fbs[i]);
			}
		}
	}

	@Test
	public void bufferPoolAllocationOrder() {
		BufferPool pool = BufferPool.get();
		BufferPool.TestAccessor accessor = pool.new TestAccessor();

		int l = accessor.getFloatBuffers().length;

		for (int i = l - 1; i >= 0; i--) {
			pool.getFloatBuffer(i);
		}

		FloatBuffer[] fbs = accessor.getFloatBuffers();

		for (int i = 0; i < l; i++) {
			Assert.assertNotNull(fbs[i]);
			Assert.assertEquals(i, fbs[i].capacity());
		}
	}

	@Test
	public void noBufferPoolDuplication() {
		BufferPool pool = BufferPool.get();
		BufferPool.TestAccessor accessor = pool.new TestAccessor();

		pool.getFloatBuffer(16);
		pool.getFloatBuffer(16);

		FloatBuffer[] fbs = accessor.getFloatBuffers();

		for (int i = 0; i < fbs.length - 1; i++) {
			if (i == fbs.length - 1) {
				Assert.assertNotNull(fbs[i]);
			} else {
				Assert.assertNull(fbs[i]);
			}
		}
	}

	@Test
	public void bufferPoolOverallocationCheck() {
		BufferPool pool = BufferPool.get();
		BufferPool.TestAccessor accessor = pool.new TestAccessor();

		int l = accessor.getFloatBuffers().length;

		for (int i = l - 1; i >= 0; i--) {
			pool.getFloatBuffer(i + 1);
		}

		pool.getFloatBuffer(0);
		
		FloatBuffer[] fbs = accessor.getFloatBuffers();

		for (int i = 0; i < l; i++) {
			Assert.assertNotNull(fbs[i]);
			Assert.assertEquals(i > 0 ? i + 1 : i, fbs[i].capacity());
		}
	}
}
