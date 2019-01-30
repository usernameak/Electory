package electory.test;

import org.junit.Test;
import org.lwjgl.BufferUtils;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import electory.client.BufferPool;

public class BufferPoolBenchmark extends AbstractBenchmark {
	@BenchmarkOptions(benchmarkRounds = 200000, warmupRounds = 50)
	@Test
	public void benchmarkBufferPoolAllocation() {
		BufferPool.get().getFloatBuffer(16);
	}

	@BenchmarkOptions(benchmarkRounds = 200000, warmupRounds = 50)
	@Test
	public void benchmarkNormalAllocation() {
		BufferUtils.createFloatBuffer(16);
	}
}
