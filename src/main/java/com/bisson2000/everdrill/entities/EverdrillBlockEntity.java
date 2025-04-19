package com.bisson2000.everdrill.entities;

import com.bisson2000.everdrill.config.EverdrillConfig;
import com.bisson2000.everdrill.util.BlockBreakingUtil;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import joptsimple.internal.Strings;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EverdrillBlockEntity extends DrillBlockEntity {

    private final ExclusiveItemStackHandler inventory = new ExclusiveItemStackHandler(4, this);
    private final LazyOptional<IItemHandler> optionalInventory = LazyOptional.of(() -> this.inventory);

    private ListTag enchantmentTags = new ListTag();

    public EverdrillBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected float getBreakSpeed() {
        return super.getBreakSpeed() * (getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY) + 1);
    }

    @Override
    public void onBlockBroken(BlockState stateToBreak) {
        Vec3 vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(this.breakingPos), this.level.random, 0.125F);

        BlockBreakingUtil.destroyBlockAs(this.level, this.breakingPos, (Player)null, getEquivalentBreakingItem(), 1.0F, (stack) -> {
            if (stack.isEmpty()) {
                return;
            }

            if (EverdrillConfig.USE_INTERNAL_BUFFER.get()) {
                ItemStack remainingStack = this.inventory.addItemStack(stack);
            }
            else if (this.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                if (!this.level.restoringBlockSnapshots) {
                    ItemEntity itementity = new ItemEntity(this.level, vec.x, vec.y, vec.z, stack);
                    itementity.setDefaultPickUpDelay();
                    itementity.setDeltaMovement(Vec3.ZERO);
                    this.level.addFreshEntity(itementity);
                }
            }
        });
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        for (EnchantmentInstance enchantmentInstance : getEnchantmentInstances()) {
            int level = enchantmentInstance.level;
            CreateLang.text(Strings.repeat(' ', 0))
                    .add(enchantmentInstance.enchantment.getFullname(level).copy())
                    .forGoggles(tooltip);
        }

        return true;
    }

    // TODO: cache
    public ItemStack getEquivalentBreakingItem() {
        ItemStack res = new ItemStack(Items.NETHERITE_PICKAXE);
        for (EnchantmentInstance enchantmentInstance : getEnchantmentInstances()) {
            res.enchant(enchantmentInstance.enchantment, enchantmentInstance.level);
        }
        return res;
    }

    public int getEnchantmentLevel(Enchantment searched) {
        for (EnchantmentInstance enchantmentInstance : getEnchantmentInstances()) {
            if (enchantmentInstance.enchantment == searched) {
                return enchantmentInstance.level;
            }
        }

        return 0;
    }

    public List<EnchantmentInstance> getEnchantmentInstances() {
        List<EnchantmentInstance> res = new ArrayList<>();

        EnchantmentHelper.deserializeEnchantments(this.enchantmentTags).forEach((e, lvl) -> {
            res.add(new EnchantmentInstance(e, lvl));
        });
        return res;
    }

    public void setEnchantment(ListTag listTag) {
        this.enchantmentTags = listTag;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        this.enchantmentTags = readEnchantments(compound);
        this.inventory.deserializeNBT(compound.getCompound("Inventory"));
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put(ItemStack.TAG_ENCH, this.enchantmentTags);
        compound.put("Inventory", this.inventory.serializeNBT());
        super.write(compound, clientPacket);
    }

    // Inventory logic
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap != ForgeCapabilities.ITEM_HANDLER || !EverdrillConfig.USE_INTERNAL_BUFFER.get()) {
            return LazyOptional.empty();
        }

        return this.optionalInventory.cast();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return this.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.optionalInventory.invalidate();
    }

    public void drops() {
        Containers.dropContents(this.level, this.worldPosition, this.inventory.getAsContainer());
    }

    @Override
    public boolean canBreak(BlockState stateToBreak, float blockHardness) {
        // TODO: Stop the drill if nothing can fit
        //List<ItemStack> drops = Block.getDrops(stateToBreak, (ServerLevel) this.level, this.breakingPos, this, null, getEquivalentBreakingItem());
        return super.canBreak(stateToBreak, blockHardness);
    }

    public static ListTag readEnchantments(CompoundTag compound) {
        return compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND);
    }
}
