package electory.client.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TriangleBuffer {
	private ByteBuffer bb;

	private ByteBuffer quadBuffer = ByteBuffer.allocate(4 * 6 * 4).order(ByteOrder.nativeOrder());
	// private int quadBufferOffset = 0;
	private static final int QUAD_VERTEX_SIZE = 4 * 6;

	private int color = 0xFFFFFFFF;

	private int vertexCount = 0;

	public void allocate(int count) {
		bb = ByteBuffer.allocateDirect(count * 4 * 6 * 3);
		bb.order(ByteOrder.nativeOrder());
	}

	public void allocateVertices(int count) {
		bb = ByteBuffer.allocateDirect(count * 4 * 6);
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

	private void putBytesWithAuxData(byte[] bytes, int offset, int length) {
		bb.put(bytes, offset, length);
		// bb.putInt(color);
		vertexCount++;
	}

	public void addQuadVertex(float x, float y, float z) {
		addQuadVertexWithUV(x, y, z, 0, 0);
	}

	public void addQuadVertexWithUV(float x, float y, float z, float u, float v) {
		quadBuffer.putFloat(x);
		quadBuffer.putFloat(y);
		quadBuffer.putFloat(z);
		quadBuffer.putFloat(u);
		quadBuffer.putFloat(v);
		quadBuffer.putInt(color);
		/*
		 * quadBuffer[quadBufferOffset++] = x; quadBuffer[quadBufferOffset++] = y;
		 * quadBuffer[quadBufferOffset++] = z; quadBuffer[quadBufferOffset++] = u;
		 * quadBuffer[quadBufferOffset++] = v; quadBuffer[quadBufferOffset++] = v;
		 */
		if (quadBuffer.position() == quadBuffer.capacity()) {
			byte[] qb = quadBuffer.array();
			putBytesWithAuxData(qb, 0 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putBytesWithAuxData(qb, 1 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putBytesWithAuxData(qb, 2 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putBytesWithAuxData(qb, 2 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putBytesWithAuxData(qb, 3 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			putBytesWithAuxData(qb, 0 * QUAD_VERTEX_SIZE, QUAD_VERTEX_SIZE);
			quadBuffer.clear();
		}
	}
}
