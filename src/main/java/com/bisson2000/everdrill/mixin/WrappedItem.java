package com.bisson2000.everdrill.mixin;

import com.bisson2000.everdrill.blocks.EverdrillBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Credits to https://www.curseforge.com/minecraft/mc-mods/create-enchantable-machinery

@Mixin(Item.class)
public abstract class WrappedItem {
    @Inject(method = "isEnchantable(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void everdrill$isEnchantable(ItemStack p_41456_, CallbackInfoReturnable<Boolean> cir) {
        if (p_41456_.getItem() instanceof BlockItem blockItem && p_41456_.getCount() == 1) {
            Block block = blockItem.getBlock();
            if (block instanceof EverdrillBlock) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getEnchantmentValue()I", at = @At("HEAD"), cancellable = true)
    public void everdrill$getEnchantmentValue(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item) (Object) this;
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof EverdrillBlock) {
                cir.setReturnValue(1);
            }
        }
    }
}
