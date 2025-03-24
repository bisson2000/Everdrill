package com.bisson2000.everdrill.entities;

import com.bisson2000.everdrill.Everdrill;
import com.bisson2000.everdrill.blocks.ModBlocks;
import com.bisson2000.everdrill.render.ModDrillRenderer;
import com.simibubi.create.content.kinetics.drill.DrillInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class ModEntities {

    public static final BlockEntityEntry<EverdrillBlockEntity> EVERDRILL = Everdrill.REGISTRATE
            .blockEntity("everdrill", EverdrillBlockEntity::new)
            .instance(() -> DrillInstance::new, true)
            .validBlocks(ModBlocks.EVERDRILL_BLOCK)
            .renderer(() -> ModDrillRenderer::new)
            .register();

    public static void register() {
    }
}
