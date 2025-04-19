package com.bisson2000.everdrill.config;

import com.simibubi.create.Create;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleSupplier;

public class CustomStress {

    public static final Map<ResourceLocation, Double> DEFAULT_IMPACTS = new ConcurrentHashMap();

    @Nullable
    public static DoubleSupplier getImpact(Block block) {
        ResourceLocation id = CatnipServices.REGISTRIES.getKeyOrThrow(block);
        DoubleSupplier supplier = () -> DEFAULT_IMPACTS.get(id);
        return DEFAULT_IMPACTS.containsKey(id) ? supplier : null;
    }

    public static void setDefaultImpact(ResourceLocation blockId, double impact) {
        DEFAULT_IMPACTS.put(blockId, impact);
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double impact) {
        return (b) -> {
            setDefaultImpact(new ResourceLocation(b.getOwner().getModid(), b.getName()), impact);
            return b;
        };
    }
}
