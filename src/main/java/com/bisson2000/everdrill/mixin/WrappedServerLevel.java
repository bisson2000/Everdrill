package com.bisson2000.everdrill.mixin;

import com.bisson2000.everdrill.capability.NaturalBlockTracker;
import com.bisson2000.everdrill.capability.NaturalBlockTrackerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for ServerLevel
 * This is better than wrapping the serverLevel
 * Inspired from <a href="https://github.com/SpongePowered/Mixin/wiki/Introduction-to-Mixins---Overwriting-Methods">This link</a>
 *
 * */
@Mixin(Level.class)
public class WrappedServerLevel {

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"))
    private void everdrill$setBlock(BlockPos pos, BlockState newState, int unkown1, int unkown2, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof ServerLevel serverLevel)) return;

        BlockState oldState = serverLevel.getBlockState(pos);

        if (everdrill$didBlockChange(oldState, newState)) {
            NaturalBlockTrackerCapability.getNaturalBlockTracker(serverLevel.getChunkAt(pos)).ifPresent(tracker -> {
                if (tracker instanceof NaturalBlockTracker naturalTracker) {
                    naturalTracker.markArtifical(pos);
                }
            });
        }
    }

    @Unique
    private static boolean everdrill$didBlockChange(BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }
}
