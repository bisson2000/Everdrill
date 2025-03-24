package com.bisson2000.everdrill.util;

import com.bisson2000.everdrill.capability.NaturalBlockTracker;
import com.bisson2000.everdrill.capability.NaturalBlockTrackerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockBreakingUtil {

    public static void destroyBlockAs(Level world, BlockPos pos, @Nullable Player player, ItemStack usedTool, float effectChance, Consumer<ItemStack> droppedItemCallback) {
        FluidState fluidState = world.getFluidState(pos);
        BlockState state = world.getBlockState(pos);
        if (world.random.nextFloat() < effectChance) {
            world.levelEvent(2001, pos, Block.getId(state));
        }

        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        if (player != null) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, state, player);
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }

            if (event.getExpToDrop() > 0 && world instanceof ServerLevel) {
                state.getBlock().popExperience((ServerLevel)world, pos, event.getExpToDrop());
            }

            usedTool.mineBlock(world, state, pos, player);
            player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
        }

        if (world instanceof ServerLevel && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !world.restoringBlockSnapshots && (player == null || !player.isCreative())) {
            for(ItemStack itemStack : Block.getDrops(state, (ServerLevel)world, pos, blockEntity, player, usedTool)) {
                droppedItemCallback.accept(itemStack);
            }

            if (state.getBlock() instanceof IceBlock && usedTool.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0) {
                if (world.dimensionType().ultraWarm()) {
                    return;
                }

                BlockState blockstate = world.getBlockState(pos.below());
                if (blockstate.blocksMotion() || blockstate.liquid()) {
                    customSetBlockCondition(world, pos, Blocks.WATER.defaultBlockState()); // Modified
                }

                return;
            }

            state.spawnAfterBreak((ServerLevel)world, pos, ItemStack.EMPTY, true);
        }

        customSetBlockCondition(world, pos, fluidState.createLegacyBlock()); // Modified
    }

    private static void customSetBlockCondition(Level world, BlockPos pos, BlockState newBlockState) {
        if (world instanceof ServerLevel serverLevel && NaturalBlockTrackerCapability.getNaturalBlockTracker(serverLevel.getChunkAt(pos)).isPresent()) {
            NaturalBlockTrackerCapability.getNaturalBlockTracker(serverLevel.getChunkAt(pos)).ifPresent(iNaturalBlockTracker -> {
                if ((iNaturalBlockTracker instanceof NaturalBlockTracker naturalBlockTracker) && naturalBlockTracker.isNatural(pos)) {
                    // Do not destroy if we have a natural block
                    return;
                }

                world.setBlockAndUpdate(pos, newBlockState);
            });
        }
        else {
            world.setBlockAndUpdate(pos, newBlockState);
        }
    }

}
