package com.bisson2000.everdrill.mixin;

import com.bisson2000.everdrill.blocks.EverdrillBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Credits to https://www.curseforge.com/minecraft/mc-mods/create-enchantable-machinery

@Mixin(Enchantment.class)
public class WrappedEnchantment {
    @Inject(
            method = "canEnchant",
            at = @At("HEAD"),
            cancellable = true
    )
    private void everdrill$canEnchant(ItemStack p_44689_, CallbackInfoReturnable<Boolean> cir) {
        if (p_44689_.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof EverdrillBlock everdrillBlock) {
                if (everdrillBlock.canApplyEnchantment((Enchantment) (Object) this)) {
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }
}
