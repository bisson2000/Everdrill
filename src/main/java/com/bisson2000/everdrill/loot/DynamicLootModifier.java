package com.bisson2000.everdrill.loot;

import com.bisson2000.everdrill.Everdrill;
import com.bisson2000.everdrill.config.EverdrillConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class DynamicLootModifier extends LootModifier {

    public static final Codec<DynamicLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            LootModifier.codecStart(instance).apply(instance, DynamicLootModifier::new)
    );

    protected DynamicLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    public @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> objectArrayList, LootContext lootContext) {
        // TODO: keep track of block charge in the future. Setting 1 to prevent infinite loops and abuse
        BlockState blockState = lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState == null || !EverdrillConfig.isTargeted(blockState.getBlock())) {
            return objectArrayList;
        }

        for (ItemStack drop : objectArrayList) {
            if (EverdrillConfig.isTargeted(drop.getItem())) {
                drop.getOrCreateTagElement(Everdrill.MOD_ID).putInt(ModLootModifiers.CHARGE_TAG, 1); // Set to 1, meaning no charge
            }
        }
        return objectArrayList;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    // Override onBlockBroken from BlockBreakingKineticBlockEntity or KineticBlockEntity
}
