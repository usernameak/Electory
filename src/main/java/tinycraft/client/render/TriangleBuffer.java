package tinycraft.client.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TriangleBuffer {
	private ByteBuffer bb;
	
	private float[] quadBuffer = new float[4 * 5];
	private int quadBufferOffset = 0;
	private static final int QUAD_VERTEX_SIZE = 5;
	
	private int color = 0xFFFFFFFF;

	private int vertexCount = 0;
	
	public void allocate(int count) {
		bb = ByteBuffer.allocateDirect(count * 4 * 6 * 3);
		bb.order(ByteOrder.nativeOrder());
	}
	
	public ByteBuffer getBuffer() {
		return bb;
	}
	
	public void reset() {
		bb.clear();
		vertexCount = 0;
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public void addVertex(float x, float y, float z) {
		addVertexWithUV(x, y, z, 0, 0);
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public void addVertexWithUV(float x, float y, float z, float u, float v) {
		bb.putFloat(x);
		bb.putFloat(y);
		bb.putFloat(z);
		bb.putFloat(u);
		bb.putFloat(v);
		bb.putInt(color);
		vertexCount++;
	}
	
	private void putFloatsWithAuxData(float[] floats, int offset, int length) {
		for(int i = offset; i < offset + length; i++) {
			bb.putFloat(floats[i]);
		}
		bb.putInt(color);
		vertexCount++;
	}
	
	public void addQuadVertex(float x, float y, float z) {
		addQuadVertexWithUV(x, y, z, 0, 0);
	}
	
	public void addQuadVertexWithUV(float x, float y, float z, float u, float v) {
		quadBuffer[quadBufferOffset++] = x;
		quadBuffer[quadBufferOffset++] = y;
		quadBuffer[quadBufferOffset++] = z;
		quadBuffer[quadBufferOffset++] = u;
		quadBuffer[quadBufferOffset++] = v;
		if(quadBufferOffset >= quadBuffer.length) {
			putFloatsWithAuxData(quadBuffer, 0 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putFloatsWithAuxData(quadBuffer, 1 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putFloatsWithAuxData(quadBuffer, 2 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putFloatsWithAuxData(quadBuffer, 2 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putFloatsWithAuxData(quadBuffer, 3 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putFloatsWithAuxData(quadBuffer, 0 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			quadBufferOffset = 0;
		}
	}
}
