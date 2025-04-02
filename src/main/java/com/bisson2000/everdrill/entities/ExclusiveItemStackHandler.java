package com.bisson2000.everdrill.entities;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExclusiveItemStackHandler extends ItemStackHandler {

    private final BlockEntity blockEntity;

    public ExclusiveItemStackHandler(int size, BlockEntity entity) {
        super(size);
        this.blockEntity = entity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        blockEntity.setChanged(); // Tell the entity it changed
    }

    public SimpleContainer getAsContainer() {
        SimpleContainer container = new SimpleContainer(this.getSlots());
        for(int i = 0; i < this.getSlots(); i++) {
            container.setItem(i, this.getStackInSlot(i));
        }
        return container;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        int validEmptySlot = -1;
        for (int i = 0; i < this.getSlots(); ++i) {
            if (this.getStackInSlot(i).isEmpty()) {
                if (validEmptySlot == -1) {
                    validEmptySlot = i;
                }
            }

            if (ItemHandlerHelper.canItemStacksStack(this.getStackInSlot(i), stack)) {
                return i == slot;
            }
        }

        return validEmptySlot == slot;
    }

    public ItemStack addItemStack(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) {
            return itemStack;
        }

        for (int i = 0; i < this.getSlots(); ++i) {
            if (this.insertItem(i, itemStack, true) != itemStack) {
                return this.insertItem(i, itemStack, false);
            }
        }

        return itemStack;
    }
}
