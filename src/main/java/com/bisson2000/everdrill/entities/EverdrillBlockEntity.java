package com.bisson2000.everdrill.entities;

import com.bisson2000.everdrill.util.BlockBreakingUtil;
import com.simibubi.create.content.kinetics.drill.DrillBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import joptsimple.internal.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EverdrillBlockEntity extends DrillBlockEntity {

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

        ItemStack breakingItem = getEquivalentBreakingItem();

        BlockBreakingUtil.destroyBlockAs(this.level, this.breakingPos, (Player)null, breakingItem, 1.0F, (stack) -> {
            if (!stack.isEmpty()) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                    if (!this.level.restoringBlockSnapshots) {
                        ItemEntity itementity = new ItemEntity(this.level, vec.x, vec.y, vec.z, stack);
                        itementity.setDefaultPickUpDelay();
                        itementity.setDeltaMovement(Vec3.ZERO);
                        this.level.addFreshEntity(itementity);
                    }
                }
            }
        });
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        for (EnchantmentInstance enchantmentInstance : getEnchantmentInstances()) {
            int level = enchantmentInstance.level;
            Lang.text(Strings.repeat(' ', 0))
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
        this.enchantmentTags = compound.getList(ItemStack.TAG_ENCH, Tag.TAG_COMPOUND);
        super.read(compound, clientPacket);
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.put(ItemStack.TAG_ENCH, this.enchantmentTags);
        super.write(compound, clientPacket);
    }
}
