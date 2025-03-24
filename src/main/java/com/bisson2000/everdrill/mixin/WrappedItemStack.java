package com.bisson2000.everdrill.mixin;


import com.bisson2000.everdrill.blocks.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Credits to https://www.curseforge.com/minecraft/mc-mods/create-enchantable-machinery

@Mixin(ItemStack.class)
public abstract class WrappedItemStack {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "setRepairCost(I)V", at = @At("HEAD"), cancellable = true)
    public void everdrill$setRepairCost(int p_41743_, CallbackInfo ci) {
        if (ModBlocks.EVERDRILL_BLOCK.get().asItem() == getItem()) {
            ci.cancel();
        }
    }
}