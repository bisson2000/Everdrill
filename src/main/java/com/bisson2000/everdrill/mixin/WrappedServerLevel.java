package com.bisson2000.everdrill.mixin;

import com.bisson2000.everdrill.capability.NaturalBlockTracker;
import com.bisson2000.everdrill.capability.NaturalBlockTrackerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;

/**
 * Mixin for ServerLevel
 * This is better than wrapping the serverLevel
 * Inspired from <a href="https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---Overwriting-Methods">This link</a>
 *
 * */
@Mixin(Level.class) //
@Implements(@Interface(iface = LevelWriter.class, prefix = "id$"))
public abstract class WrappedServerLevel implements LevelWriter {

    @Shadow
    public abstract boolean setBlock(BlockPos targetPos, BlockState newState, int unkown1, int unkown2);

    /**
     * This method will become our intrinsic proxy method, it
     * calls the original (shadowed) version of the accessor.
     * It uses the displace parameter to avoid re-entrance when
     * the method would otherwise be overwritten.
     */
    @Intrinsic(displace = true)
    public boolean id$setBlock(BlockPos targetPos, BlockState newState, int unkown1, int unkown2) {


        Level level = (Level)((Object)this);
        final BlockState oldState = level.getBlockState(targetPos);

        if (!(level instanceof ServerLevel serverLevel)) {
            return this.setBlock(targetPos, newState, unkown1, unkown2);
        }

        if (didBlockChange(oldState, newState)) {
            NaturalBlockTrackerCapability.getNaturalBlockTracker(serverLevel.getChunkAt(targetPos)).ifPresent(iNaturalBlockTracker -> {
                if (!(iNaturalBlockTracker instanceof NaturalBlockTracker naturalBlockTracker)) return;

                naturalBlockTracker.markArtifical(targetPos);
            });
        }

        return this.setBlock(targetPos, newState, unkown1, unkown2);
    }

    private static boolean didBlockChange(BlockState oldBlockState, BlockState newBlockState) {
        //return ReMineConfig.isTargeted(oldBlockState.getBlock()) && newBlockState.getBlock() != oldBlockState.getBlock();
        return newBlockState.getBlock() != oldBlockState.getBlock();
    }
}
