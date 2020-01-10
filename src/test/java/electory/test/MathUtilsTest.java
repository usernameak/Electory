package electory.test;

import org.junit.Assert;
import org.junit.Test;

import electory.math.MathUtils;

public class MathUtilsTest {
	@Test
	public void partialSumsAreCorrect() {
		int[][] arr = new int[][] {
			{5, 7, 6, 7, 5},
			{6, 4, 3, 1, 4},
			{9, 8, 11, 13, 7},
			{8, 9, 6, 5, 4},
		};
		
		Assert.assertArrayEquals(new int[][] {
			{5, 12, 18, 25, 30},
			{11, 22, 31, 39, 48},
			{20, 39, 59, 80, 96},
			{28, 56, 82, 108, 128}
		}, MathUtils.doPartialSums(arr));
	}
	
	@Test
	public void rectangleSumsAreCorrect() {
		int[][] arr = new int[][] {
			{5, 7, 6, 7, 5},
			{6, 4, 3, 1, 4},
			{9, 8, 11, 13, 7},
			{8, 9, 6, 5, 4},
		};
		
		int[][] psums = MathUtils.doPartialSums(arr);
		
		Assert.assertEquals(35, MathUtils.getRectangleSum(psums, 2, 2, 2, 2));
		Assert.assertEquals(46, MathUtils.getRectangleSum(psums, 2, 2, 2, 3));
	}
}
