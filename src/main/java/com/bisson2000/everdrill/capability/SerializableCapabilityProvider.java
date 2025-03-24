package com.bisson2000.everdrill.capability;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SerializableCapabilityProvider<HANDLER> implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    protected final Capability<HANDLER> capability;
    protected final Direction facing;
    protected final HANDLER instance;
    protected final LazyOptional<HANDLER> lazyOptional;
    private final INBTSerializable<CompoundTag> serializableInstance;

    @SuppressWarnings("unchecked")
    public SerializableCapabilityProvider(final Capability<HANDLER> capability, @Nullable final Direction facing, final HANDLER instance) {
        this.capability = Preconditions.checkNotNull(capability, "capability");
        this.facing = facing;
        this.instance = Preconditions.checkNotNull(instance, "instance");

        Preconditions.checkArgument(instance instanceof INBTSerializable, "instance must implement INBTSerializable");
        this.serializableInstance = (INBTSerializable<CompoundTag>) instance;

        lazyOptional = LazyOptional.of(() -> this.instance);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return getCapability().orEmpty(capability, lazyOptional);
    }

    public final Capability<HANDLER> getCapability() {
        return capability;
    }

    @Nullable
    public Direction getFacing() {
        return facing;
    }
    
    public final HANDLER getInstance() {
        return instance;
    }

    @Override
    public CompoundTag serializeNBT() {
        return serializableInstance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        serializableInstance.deserializeNBT(tag);
    }

}
