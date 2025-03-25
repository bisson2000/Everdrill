package com.bisson2000.everdrill.blocks;

import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.bisson2000.everdrill.entities.ModEntities;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EverdrillBlock extends DrillBlock {

    private static final int enchantedPlacementHelperId = PlacementHelpers.register(new PlacementHelper());

    public EverdrillBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockEntityType<? extends DrillBlockEntity> getBlockEntityType() {
        return ModEntities.EVERDRILL.get();
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof EverdrillBlockEntity everdrillBlockEntity) {
            everdrillBlockEntity.setEnchantment(stack.getEnchantmentTags());
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        BlockEntity blockEntity = builder.getParameter(LootContextParams.BLOCK_ENTITY);
        ItemStack stack = new ItemStack(ModBlocks.EVERDRILL_BLOCK);
        if (blockEntity instanceof EverdrillBlockEntity everdrillBlockEntity) {
            everdrillBlockEntity.getEnchantmentInstances().forEach(enchantmentInstance -> {
                stack.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
            });
        }

        return new ArrayList<>(List.of(stack));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ItemStack stack = new ItemStack(ModBlocks.EVERDRILL_BLOCK);
        if (blockEntity instanceof EverdrillBlockEntity everdrillBlockEntity) {
            everdrillBlockEntity.getEnchantmentInstances().forEach(enchantmentInstance -> {
                stack.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
            });
        }

        return stack;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        IPlacementHelper placementHelper = PlacementHelpers.get(enchantedPlacementHelperId);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(heldItem)) {
            placementHelper.getOffset(player, world, state, pos, ray).placeInWorld(world, (BlockItem)heldItem.getItem(), player, hand, ray);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public boolean canApplyEnchantment(Enchantment enchantment) {
        if (enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING || enchantment == Enchantments.SILK_TOUCH) {
            return false;
        }

        return enchantment.category == EnchantmentCategory.DIGGER;
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            BlockEntry<EverdrillBlock> blockEntry = ModBlocks.EVERDRILL_BLOCK;
            Objects.requireNonNull(blockEntry);
            return blockEntry::isIn;
        }

        public Predicate<BlockState> getStatePredicate() {
            BlockEntry<EverdrillBlock> blockEntry = ModBlocks.EVERDRILL_BLOCK;
            Objects.requireNonNull(blockEntry);
            return blockEntry::has;
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(
                    pos,
                    ray.getLocation(),
                    state.getValue(DirectionalKineticBlock.FACING).getAxis(),
                    (dir) -> world.getBlockState(pos.relative(dir)).canBeReplaced());

            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }

            return PlacementOffset.success(
                    pos.relative(directions.get(0)),
                    (s) -> s.setValue(DirectionalKineticBlock.FACING, state.getValue(DirectionalKineticBlock.FACING))
            );
        }
    }


}
