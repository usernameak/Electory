package electory.world.gen.heightmap;

import electory.world.gen.biome.IBiomeAccessor;
import electory.world.gen.heightmap.postprocessor.IHeightMapPostProcessor;

public class PostprocessingHeightMapGenerator implements IHeightMapGenerator {
	
	private IHeightMapGenerator parent;
	private IHeightMapPostProcessor postproc;

	public PostprocessingHeightMapGenerator(IHeightMapGenerator parent, IHeightMapPostProcessor postproc) {
		this.parent = parent;
		this.postproc = postproc;
	}

	@Override
	public int[][] generateHeightmap(IBiomeAccessor biomeTrigger, int cx, int cy) {
		return postproc.postprocess(parent.generateHeightmap(biomeTrigger, cx, cy));
	}

}
