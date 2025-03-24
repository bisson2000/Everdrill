package com.bisson2000.everdrill.mixin;

import com.bisson2000.everdrill.blocks.EverdrillBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Credits to https://www.curseforge.com/minecraft/mc-mods/create-enchantable-machinery

@Mixin(BlockItem.class)
public abstract class WrappedBlockItem extends Item {

    public WrappedBlockItem(Properties settings) {
        super(settings);
    }

    @Shadow
    public abstract Block getBlock();

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (this.getBlock() instanceof EverdrillBlock everdrillBlock) {
            return everdrillBlock.canApplyEnchantment(enchantment);
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

}
