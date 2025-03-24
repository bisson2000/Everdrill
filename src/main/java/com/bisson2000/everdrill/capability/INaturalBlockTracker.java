package com.bisson2000.everdrill.capability;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public interface INaturalBlockTracker {
    Level getLevel();
    ChunkPos getChunkPos();
}
