package com.bisson2000.everdrill.loot;

import com.bisson2000.everdrill.Everdrill;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Everdrill.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> POSITION_LOOT_MODIFIER =
            LOOT_MODIFIERS.register("charges", () -> DynamicLootModifier.CODEC);

    public static final String CHARGE_TAG = new ResourceLocation(Everdrill.MOD_ID, "charges").toString();

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIERS.register(eventBus);
    }
}
