package com.bisson2000.everdrill.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;


public class NaturalBlockTracker implements INaturalBlockTracker, INBTSerializable<CompoundTag> {
    private static final int CHUNK_SIZE_X = 16;
    private static final int CHUNK_SIZE_Z = 16;
    private final int CHUNK_SIZE_Y; // y-coordinate range from 0 to 384

    private final Level level;
    private final ChunkPos chunkPos;
    private byte[] artificalBlocks;

    public NaturalBlockTracker(final Level level, final ChunkPos chunkPos) {
        this.level = level;
        this.chunkPos = chunkPos;
        this.CHUNK_SIZE_Y = level.getHeight();

        // False by default.
        // division by 8 is safe, because CHUNK_SIZE_Y * 16 * 16 is always divisible by 8
        this.artificalBlocks = new byte[(CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z) >> 3];
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public ChunkPos getChunkPos() {
        return this.chunkPos;
    }

    private int getIndex(BlockPos pos) {

        int x = pos.getX() & 0xF;  // x within the chunk (0-15), mod 16
        int y = pos.getY() - this.getLevel().getMinBuildHeight(); // y within the chunk (0-384)
        int z = pos.getZ() & 0xF;  // z within the chunk (0-15), mod 16

        // Compute a unique index (x, y, z -> index)
        // z takes 4 bits, move the next value by 4
        // x takes 4 bits, it is moved by 4, move the next value by 4 more
        // y is moved by a total of 8
        return (y << 8) + (x << 4) + z; // (pos.getY() * 256) + (pos.getX() & 15) * 16 + (pos.getZ() & 15);
    }

    public boolean isNatural(BlockPos pos) {
        int index = getIndex(pos);
        int byteIndex = index >> 3;     // Which byte in the Byte[] to modify, div 8
        int bitOffset = index & 0x7;    // Which bit within the byte to modify, mod 8

        return (this.artificalBlocks[byteIndex] & (1 << (7 - bitOffset)) ) == 0;
    }

    public void markNatural(BlockPos pos) {
        int index = getIndex(pos);
        int byteIndex = index >> 3;     // Which byte in the Byte[] to modify, div 8
        int bitOffset = index & 0x7;    // Which bit within the byte to modify, mod 8

        // Set the bit to 0 to indicate the block is natural
        this.artificalBlocks[byteIndex] = (byte) (artificalBlocks[byteIndex] & ~(1 << (7 - bitOffset)) );

        onDataChanged();
    }

    public void markArtifical(BlockPos pos) {
        int index = getIndex(pos);
        int byteIndex = index >> 3;     // Which byte in the Byte[] to modify, div 8
        int bitOffset = index & 0x7;    // Which bit within the byte to modify, mod 8

        // Set the bit to 1 to indicate the block is no longer natural
        this.artificalBlocks[byteIndex] = (byte) (artificalBlocks[byteIndex] | (1 << (7 - bitOffset)) );

        onDataChanged();
    }

    private void onDataChanged() {
        final Level level = getLevel();
        final ChunkPos chunkPos = getChunkPos();

        if (level.isClientSide) {
            return;
        }

        if (level.hasChunk(chunkPos.x, chunkPos.z)) {  // Don't load the chunk when reading from NBT
            final var chunk = level.getChunk(chunkPos.x, chunkPos.z);
            chunk.setUnsaved(true);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("artificalBlocks", this.artificalBlocks);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        // TODO: deserialization edge case when height changes
        this.artificalBlocks = compoundTag.getByteArray("artificalBlocks");
    }
}
