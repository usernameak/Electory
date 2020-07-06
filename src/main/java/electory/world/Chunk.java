package electory.world;

import electory.block.Block;
import electory.client.render.world.ChunkRenderer;
import electory.profiling.ElectoryProfiler;
import electory.utils.EnumSide;
import electory.utils.MetaSerializer;
import electory.utils.io.ArrayDataInput;
import electory.utils.io.ArrayDataOutput;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Chunk {
    private short blockArray[] = new short[16 * 256 * 16];
    private short lightArray[] = new short[16 * 256 * 16];
    private Object metaArray[] = new Object[16 * 256 * 16];
    private byte biomeArray[] = new byte[16 * 16];
    private short heightMap[] = new short[16 * 16];

    // private SortedMap<Integer, ChunkPosition> scheduledBlockUpdates = new
    // TreeMap<>(); // TODO:

    public final ChunkRenderer chunkRenderer = new ChunkRenderer(this);

    public final World world;

    private int chunkX, chunkZ;

    public boolean isPopulated = false;

    public final ReadWriteLock renderLock = new ReentrantReadWriteLock();

    public Chunk(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public void tryPopulateWithNeighbours(IChunkProvider provider) {
        if (!isPopulated
                && provider.isChunkLoaded(chunkX + 1, chunkZ + 1)
                && provider.isChunkLoaded(chunkX, chunkZ + 1)
                && provider.isChunkLoaded(chunkX + 1, chunkZ)) {
            provider.populate(null, chunkX, chunkZ);
        }

        if (provider.isChunkLoaded(chunkX - 1, chunkZ)
                && !provider.provideChunk(chunkX - 1, chunkZ).isPopulated
                && provider.isChunkLoaded(chunkX, chunkZ + 1)
                && provider.isChunkLoaded(chunkX - 1, chunkZ + 1)) {
            provider.populate(null, chunkX - 1, chunkZ);
        }

        if (provider.isChunkLoaded(chunkX, chunkZ - 1)
                && !provider.provideChunk(chunkX, chunkZ - 1).isPopulated
                && provider.isChunkLoaded(chunkX + 1, chunkZ)
                && provider.isChunkLoaded(chunkX + 1, chunkZ - 1)) {
            provider.populate(null, chunkX, chunkZ - 1);
        }

        if (provider.isChunkLoaded(chunkX - 1, chunkZ - 1)
                && !provider.provideChunk(chunkX - 1, chunkZ - 1).isPopulated
                && provider.isChunkLoaded(chunkX - 1, chunkZ)
                && provider.isChunkLoaded(chunkX, chunkZ - 1)) {
            provider.populate(null, chunkX - 1, chunkZ - 1);
        }
    }

    public void buildHeightMap() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y;
                for (y = 255; y >= 0; y--) {
                    Block block = getBlockAt(x, y, z);
                    if (block != null && block.isSolid()) {
                        break;
                    }
                }

                // System.out.println(y);
                heightMap[x * 16 + z] = (short) y;
            }
        }
    }

    public short getHeightAt(int x, int z) {
        if (x < 0 || z < 0 || x >= 16 || z >= 16) {
            return 0;
        }
        return heightMap[x * 16 + z];
    }

    public EnumWorldBiome getBiomeAt(int x, int z) {
        if (x < 0 || z < 0 || x >= 16 || z >= 16) {
            return null;
        }
        return EnumWorldBiome.biomeList[biomeArray[x * 16 + z]];
    }

    public void setBiomeAt(int x, int z, EnumWorldBiome biome) {
        if (x < 0 || z < 0 || x >= 16 || z >= 16) {
            return;
        }

        Lock lock = renderLock.writeLock();
        lock.lock();
        try {
            biomeArray[x * 16 + z] = (byte) biome.biomeID;
        } finally {
            lock.unlock();
        }
    }

    public Block getBlockAt(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
            return null;
        }
        return world.blockIdRegistry.getBlockById(blockArray[x + y * 16 + z * 16 * 256]);
    }

    public Block getWorldBlockFast(int x, int y, int z) {
        int cx = x - getChunkBlockCoordX();
        int cz = z - getChunkBlockCoordZ();
        if (cx < 0 || y < 0 || cz < 0 || cx >= 16 || y >= 256 || cz >= 16) {
            return world.getBlockAt(x, y, z);
        }
        return world.blockIdRegistry.getBlockById(blockArray[cx + y * 16 + cz * 16 * 256]);
    }

    public int getLightLevelAt(int x, int y, int z, int lightLevelType) {
        if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
            return lightLevelType == World.LIGHT_LEVEL_TYPE_SKY ? 0xF : 0x0;
        }

        int shift = lightLevelType << 2;
        int mask = 0xF << shift;

        return (byte) ((lightArray[x + y * 16 + z * 16 * 256] & mask) >> shift);
    }

    public int getWorldLightLevelFast(int x, int y, int z, int lightLevelType) {
        int cx = x - getChunkBlockCoordX();
        int cz = z - getChunkBlockCoordZ();
        if (y < 0) {
            return 0;
        }
        if (y >= 256) {
            return lightLevelType == World.LIGHT_LEVEL_TYPE_SKY ? 0xF : 0x0;
        }
        if (cx < 0 || cz < 0 || cx >= 16 || cz >= 16) {
            return world.getLightLevelAt(x, y, z, lightLevelType);
        }
        return getLightLevelAt(cx, y, cz, lightLevelType);
    }

    public void setLightLevelAt(int x, int y, int z, int lightLevelType, int val, int flags) {
        if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
            return;
        }
        Lock lock = renderLock.writeLock();
        lock.lock();
        try {
            int shift = lightLevelType << 2;
            int mask = 0xFFFF & ~(0xF << shift);

            lightArray[x + y * 16 + z * 16 * 256] = (short) ((lightArray[x + y * 16 + z * 16 * 256] & mask) | (val << shift));

            if ((flags & World.FLAG_SKIP_RENDER_UPDATE) == 0) {
                scheduleRenderUpdateForBlockAndAdjacents(x, z);
            }
        } finally {
            lock.unlock();
        }
    }

    public void setWorldLightLevelFast(int x, int y, int z, int lightLevelType, int val, int flags) {
        int cx = x - getChunkBlockCoordX();
        int cz = z - getChunkBlockCoordZ();
        if (cx < 0 || y < 0 || cz < 0 || cx >= 16 || y >= 256 || cz >= 16) {
            world.setLightLevelAt(x, y, z, lightLevelType, val, flags);
            return;
        }
        setLightLevelAt(cx, y, cz, lightLevelType, val, flags);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBlockMetadataAt(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
            return null;
        }
        return (T) metaArray[x + y * 16 + z * 16 * 256];
    }

    public void setBlockMetadataAt(int x, int y, int z, Object meta) {
        if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
            return;
        }
        Lock lock = renderLock.writeLock();
        lock.lock();
        try {
            metaArray[x + y * 16 + z * 16 * 256] = meta;
        } finally {
            lock.unlock();
        }
    }

    public void setBlockWithMetadataAt(int x, int y, int z, Block block, Object meta, int flags) {
        if (x < 0 || y < 0 || z < 0 || x >= 16 || y >= 256 || z >= 16) {
            return;
        }
        Lock lock = renderLock.writeLock();
        lock.lock();
        try {
            blockArray[x + y * 16 + z * 16 * 256] = (short) world.blockIdRegistry.getBlockId(block);
            metaArray[x + y * 16 + z * 16 * 256] = meta;
            if (block != null && block.isSolid()) {
                if (heightMap[x * 16 + z] < y) {
                    heightMap[x * 16 + z] = (short) y;
                }
            } else {
                if (heightMap[x * 16 + z] == y) {
                    int h = y - 1;
                    for (; h >= 0; h--) {
                        Block b = getBlockAt(x, h, z);
                        if (b != null) {
                            break;
                        }
                    }
                    if (h < 0) {
                        h = 0;
                    }
                    heightMap[x * 16 + z] = (short) h;
                }
            }
        } finally {
            lock.unlock();
        }
        if ((flags & World.FLAG_SKIP_LIGHT_UPDATE) == 0) {
            recalculateLightForBlock(x, y, z);
        }
        if ((flags & World.FLAG_SKIP_RENDER_UPDATE) == 0) {
            scheduleRenderUpdateForBlockAndAdjacents(x, z);
        }

    }

    private void scheduleRenderUpdateForBlockAndAdjacents(int x, int z) {
        scheduleChunkUpdate();
        for (int xx = chunkX - 1; xx <= chunkX + 1; xx++) {
            for (int zz = chunkZ - 1; zz <= chunkZ + 1; zz++) {
                if (xx == chunkX && zz == chunkZ) continue;
                boolean shouldUpdate = true;
                if (xx == chunkX - 1) shouldUpdate &= x == 0;
                if (xx == chunkX + 1) shouldUpdate &= x == 15;
                if (zz == chunkZ - 1) shouldUpdate &= z == 0;
                if (zz == chunkZ + 1) shouldUpdate &= z == 15;
                Chunk nearChunk = world.getChunkFromChunkCoord(xx, zz);
                if (nearChunk != null) {
                    nearChunk.scheduleChunkUpdate();
                }
            }
        }
    }

    public void setBlockAt(int x, int y, int z, Block block, int flags) {
        setBlockWithMetadataAt(x, y, z, block, null, flags);
    }

    public void setBlockAt(int x, int y, int z, Block block) {
        setBlockAt(x, y, z, block, 0);
    }

    public void scheduleChunkUpdate() {
        getRenderer().markDirty();
    }

    public void unload() {
        getRenderer().destroy();
    }

    public ChunkRenderer getRenderer() {
        return chunkRenderer;
    }

    public void notifyNeighbourChunks() {
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkZ + 1; z++) {
                if (x == chunkX && z == chunkZ) continue;
                Chunk nearChunk = world.getChunkFromChunkCoord(x, z);
                if (nearChunk != null) {
                    nearChunk.scheduleChunkUpdate();
                }
            }
        }
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int getChunkBlockCoordX() {
        return chunkX << 4;
    }

    public int getChunkBlockCoordZ() {
        return chunkZ << 4;
    }

    private void recalculateLight(int lightLevelType, Queue<WorldPosition> bfsSkyQueue) {
        ElectoryProfiler.INSTANCE.begin("skyLight");
        while (!bfsSkyQueue.isEmpty()) {
            WorldPosition pos = bfsSkyQueue.poll();

            recalculateLightStep(lightLevelType, pos.x, pos.y, pos.z, bfsSkyQueue);
        }
        ElectoryProfiler.INSTANCE.end("skyLight");
    }

    private void recalculateLightStep(int lightLevelType, int x, int y, int z, Queue<WorldPosition> bfsSkyQueue) {
        if (y >= 256 || y < 0) return;

        if (x >> 4 != chunkX || z >> 4 != chunkZ)
            if (!world.chunkProvider.isChunkLoaded(x >> 4, z >> 4)) return;

        int oldLightLevel = getWorldLightLevelFast(x, y, z, lightLevelType);

        Block block = getWorldBlockFast(x, y, z);

        int lightLevel = lightLevelType == World.LIGHT_LEVEL_TYPE_SKY ? 0 : (block == null ? 0 : block.getLightValue());

        // get block opacity
        int opacity = block == null ? 0 : block.getLightOpacity(lightLevelType);

        if (opacity != 15) {
            // find most affecting light source
            for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
                int realOpacity = opacity;

                int lightIntensityAdjacent = getWorldLightLevelFast(x + side.offsetX, y + side.offsetY, z + side.offsetZ, lightLevelType);
                if (lightLevelType == World.LIGHT_LEVEL_TYPE_SKY && side == EnumSide.UP) {
                    // when level 15 skylight propagates from above, light level below will be 15 too
                    // this should not affect local (block) light
                    int lightIntensityAbove = y == 255 ? 15 : getWorldLightLevelFast(x, y + 1, z, lightLevelType);
                    if (lightIntensityAbove != 15 && realOpacity == 0) {
                        // fade light if above light level is not 15
                        realOpacity = 1;
                    }
                } else if (realOpacity == 0) {
                    // fade light if it's not from above
                    realOpacity = 1;
                }

                if (lightLevelType == World.LIGHT_LEVEL_TYPE_BLOCK) {
                    System.out.println("side " + side.name() + "; real opacity " + realOpacity + "; intensity " + lightIntensityAdjacent);
                }

                if (lightIntensityAdjacent >= oldLightLevel) {
                    int newLightLevel = lightIntensityAdjacent - realOpacity;
                    //noinspection StatementWithEmptyBody
                    if (lightLevelType == World.LIGHT_LEVEL_TYPE_SKY
                            && oldLightLevel == 15
                            && lightIntensityAdjacent == 15
                            && side == EnumSide.DOWN) {
                        // direct sky light removal, this block should be empty
                    } else if (newLightLevel > lightLevel) { // note that this always avoids negative light level
                        lightLevel = newLightLevel;
                    }
                }
            }
        }

        if (lightLevelType == World.LIGHT_LEVEL_TYPE_BLOCK) {
            System.out.println(lightLevel);
        }

        setWorldLightLevelFast(x, y, z, lightLevelType, lightLevel, 0); // update light level

        if (lightLevel < oldLightLevel) { // light level decreased
            for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
                // find all possible adjacents to decrease their light level
                int lightIntensityAdjacent = getWorldLightLevelFast(x + side.offsetX, y + side.offsetY, z + side.offsetZ, lightLevelType);
                if (lightIntensityAdjacent < oldLightLevel
                        || (lightLevelType == World.LIGHT_LEVEL_TYPE_SKY && side == EnumSide.DOWN && lightIntensityAdjacent == 15 && oldLightLevel == 15)) {
                    // add them to BFS queue
                    bfsSkyQueue.add(new WorldPosition(x + side.offsetX, y + side.offsetY, z + side.offsetZ));
                }
            }
        } else if (lightLevel > oldLightLevel) { // light level increased
            for (EnumSide side : EnumSide.VALID_DIRECTIONS) {
                // find all possible adjacents to increase their light level
                Block adjBlock = getWorldBlockFast(x + side.offsetX, y + side.offsetY, z + side.offsetZ);
                int adjOpacity = adjBlock == null ? 0 : adjBlock.getLightOpacity(lightLevelType);
                if (lightLevelType == World.LIGHT_LEVEL_TYPE_SKY && side == EnumSide.DOWN) {
                    if (lightLevel != 15 && adjOpacity == 0) {
                        // fade light if above light level is not 15
                        adjOpacity = 1;
                    }
                } else if (adjOpacity == 0) {
                    // fade light if it's not from above
                    adjOpacity = 1;
                }
                int lightIntensityAdjacent = getWorldLightLevelFast(x + side.offsetX, y + side.offsetY, z + side.offsetZ, lightLevelType);
                if (lightIntensityAdjacent < lightLevel - adjOpacity) {
                    // add only if light level of adjacents is
                    // below the possible level assuming this side
                    // is picked as the most affecting light source
                    // add them to BFS queue
                    bfsSkyQueue.add(new WorldPosition(x + side.offsetX, y + side.offsetY, z + side.offsetZ)); // kinda needs light update
                }
            }
        }
    }

    public void recalculateLight(int lightLevelType) {
        Queue<WorldPosition> bfsSkyQueue = new LinkedList<>();
        for (int x = getChunkBlockCoordX(); x < getChunkBlockCoordX() + 16; x++) {
            for (int z = getChunkBlockCoordZ(); z < getChunkBlockCoordZ() + 16; z++) {
                bfsSkyQueue.add(new WorldPosition(x, 255, z));
            }
        }
        recalculateLight(lightLevelType, bfsSkyQueue);
    }

    public void recalculateLightForBlock(int lightLevelType, int x, int y, int z) {
        Queue<WorldPosition> bfsSkyQueue = new LinkedList<>();
        int wx = x + getChunkBlockCoordX();
        int wz = z + getChunkBlockCoordZ();
        bfsSkyQueue.add(new WorldPosition(wx, y, wz));
        recalculateLight(lightLevelType, bfsSkyQueue);
    }

    public void recalculateLightForBlock(int x, int y, int z) {
        recalculateLightForBlock(World.LIGHT_LEVEL_TYPE_SKY, x, y, z);
        recalculateLightForBlock(World.LIGHT_LEVEL_TYPE_BLOCK, x, y, z);
    }

    private void writeMetaArray(ArrayDataOutput dos) throws IOException {
        for (Object o : metaArray) {
            if (o != null) {
                dos.writeByte(1);
                MetaSerializer.serializeObject(dos, o);
            } else {
                dos.writeByte(0);
            }
        }
    }

    private void readMetaArray(ArrayDataInput dis) throws IOException {
        Arrays.fill(metaArray, null);
        for (int i = 0; i < metaArray.length; i++) {
            if (dis.readByte() == 1) {
                MetaSerializer.deserializeObject(dis, world.blockIdRegistry.getBlockById(blockArray[i]).getMetadataClass());
            }
        }
    }

    public void writeChunkData(ArrayDataOutput dos) throws IOException {
        dos.write(blockArray);
        dos.write(lightArray);
        dos.write(biomeArray);
        dos.write(heightMap);
        writeMetaArray(dos);
        dos.writeBoolean(isPopulated);
    }

    public void readChunkData(ArrayDataInput dis) throws IOException {
        dis.read(blockArray);
        dis.read(lightArray);
        dis.read(biomeArray);
        dis.read(heightMap);
        readMetaArray(dis);
        isPopulated = dis.readBoolean();
        scheduleChunkUpdate();
    }
}
