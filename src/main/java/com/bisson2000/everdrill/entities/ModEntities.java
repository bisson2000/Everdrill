package com.bisson2000.everdrill.entities;

import com.bisson2000.everdrill.Everdrill;
import com.bisson2000.everdrill.blocks.ModBlocks;
import com.bisson2000.everdrill.render.EnchantedOrientedRotatingVisual;
import com.bisson2000.everdrill.render.ModDrillRenderer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ModEntities {

    public static final BlockEntityEntry<EverdrillBlockEntity> EVERDRILL = Everdrill.REGISTRATE
            .blockEntity("everdrill", EverdrillBlockEntity::new)
            .visual(() -> EnchantedOrientedRotatingVisual.of(AllPartialModels.DRILL_HEAD), true)
            .validBlocks(ModBlocks.EVERDRILL_BLOCK)
            .renderer(() -> ModDrillRenderer::new)
            .register();

    public static void register() {
    }
}
