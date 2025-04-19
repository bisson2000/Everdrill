package com.bisson2000.everdrill.mixin;

import com.bisson2000.everdrill.blocks.EverdrillBlock;
import com.bisson2000.everdrill.blocks.ModBlocks;
import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedBlockAndTintGetter;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualEmbedding;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.SimpleModel;
import dev.engine_room.flywheel.lib.model.baked.BlockModelBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

// Credits to https://www.curseforge.com/minecraft/mc-mods/create-enchantable-machinery

@Mixin(value = ContraptionVisual.class, remap = false)
public class WrappedContraptionVisual {
    @Shadow
    protected VirtualRenderWorld virtualRenderWorld;

    @Shadow
    @Final
    protected VisualEmbedding embedding;

    @Unique
    protected TransformedInstance everdrill$enchantedStructure;

    @Inject(method = "setupModel", at = @At("TAIL"))
    private void everdrill$setupModel(Contraption contraption, CallbackInfo ci) {
        Instancer<TransformedInstance> enchantedInstancer = everdrill$setupModel(contraption, virtualRenderWorld, embedding);

        if (everdrill$enchantedStructure == null) {
            everdrill$enchantedStructure = enchantedInstancer.createInstance();
        } else {
            enchantedInstancer.stealInstance(everdrill$enchantedStructure);
        }

        everdrill$enchantedStructure.setChanged();
    }

    @Inject(method = "_delete", at = @At("TAIL"))
    private void everdrill$_delete(CallbackInfo ci) {
        if (everdrill$enchantedStructure != null) {
            everdrill$enchantedStructure.delete();
        }
    }

    @Unique
    private static Instancer<TransformedInstance> everdrill$setupModel(Contraption contraption, VirtualRenderWorld virtualRenderWorld, VisualEmbedding embedding) {
        Contraption.RenderedBlocks renderedBlocks = contraption.getRenderedBlocks();

        WrappedBlockAndTintGetter wrappedBlockAndTintGetter = new WrappedBlockAndTintGetter(virtualRenderWorld) {
            @Override
            public BlockState getBlockState(BlockPos pos) {
                return renderedBlocks.lookup().apply(pos);
            }
        };

        List<BlockPos> enchantedBlocks = new ArrayList<>();
        renderedBlocks.positions().forEach(blockPos -> {
            if (wrappedBlockAndTintGetter.getBlockState(blockPos).getBlock() instanceof EverdrillBlock) {
                if (wrappedBlockAndTintGetter.getBlockEntity(blockPos) instanceof EverdrillBlockEntity everdrillBlockEntity && !everdrillBlockEntity.getEnchantmentInstances().isEmpty()) {
                    enchantedBlocks.add(blockPos);
                }
            }
        });

        SimpleModel enchantedModel = BlockModelBuilder.create(wrappedBlockAndTintGetter, enchantedBlocks)
                .materialFunc((renderType, aBoolean) -> Materials.GLINT)
                .build();
        return embedding.instancerProvider().instancer(InstanceTypes.TRANSFORMED, enchantedModel);
    }
}
