package com.bisson2000.everdrill.blocks;

import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.bisson2000.everdrill.entities.ModEntities;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EverdrillBlock extends DrillBlock {

    public EverdrillBlock(Properties properties) {
        super(properties);
    }

    //@Override
    //public @NotNull MutableComponent getName() {
    //    return ModBlocks.INFINIDRILL_BLOCK.get().getName();
    //}
//
    //@Override
    //public @NotNull Item asItem() {
    //    return ModBlocks.INFINIDRILL_BLOCK.asItem();
    //}

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

    public boolean canApplyEnchantment(Enchantment enchantment) {
        if (enchantment == Enchantments.UNBREAKING || enchantment == Enchantments.MENDING || enchantment == Enchantments.SILK_TOUCH) {
            return false;
        }

        return enchantment.category == EnchantmentCategory.DIGGER;
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        super.entityInside(state, worldIn, pos, entityIn);
    }


}
