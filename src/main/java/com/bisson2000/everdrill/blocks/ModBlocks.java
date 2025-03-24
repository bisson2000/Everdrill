package com.bisson2000.everdrill.blocks;

import com.bisson2000.everdrill.Everdrill;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {

    public static final BlockEntry<EverdrillBlock> EVERDRILL_BLOCK = Everdrill.REGISTRATE
            .block("everdrill", EverdrillBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties((p) -> p.mapColor(MapColor.PODZOL))
            .transform(TagGen.axeOrPickaxe())
            .blockstate((c, p) -> {
                p.directionalBlock(c.get(), blockState -> {
                    return p.models().getExistingFile(new ResourceLocation(Everdrill.MOD_ID, "block/everdrill/source_block"));
                });
            })
            .transform(BlockStressDefaults.setImpact(4.0))
            .onRegister(AllMovementBehaviours.movementBehaviour(new DrillMovementBehaviour()))
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .model((c, p) -> {
                ResourceLocation itemLocation = new ResourceLocation(Everdrill.MOD_ID, "block/everdrill/source_item");
                ResourceLocation headLocation = new ResourceLocation(Everdrill.MOD_ID, "block/everdrill/source_head");
                p.withExistingParent(c.getName(), itemLocation);
                p.withExistingParent("block/" + c.getName() + "/head", headLocation);
            })
            .build()
            .register();

    public static void register() {
    }
}
