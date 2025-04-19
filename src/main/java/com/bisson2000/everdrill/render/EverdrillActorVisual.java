package com.bisson2000.everdrill.render;

import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class EverdrillActorVisual extends ActorVisual {

    TransformedInstance drillHead;
    TransformedInstance enchantedDrillHead;
    private final Direction facing;

    private double rotation = 0.0;
    private double previousRotation = 0.0;

    public EverdrillActorVisual(VisualizationContext visualizationContext, BlockAndTintGetter world, MovementContext context) {
        super(visualizationContext, world, context);

        BlockState state = context.state;

        facing = state.getValue(DrillBlock.FACING);

        drillHead = instancerProvider.instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.DRILL_HEAD))
                .createInstance();
        enchantedDrillHead = instancerProvider.instancer(InstanceTypes.TRANSFORMED, BakedModelBuilder
                        .create(AllPartialModels.DRILL_HEAD.get()).materialFunc(
                                (renderType, aBoolean) -> Materials.GLINT)
                        .build())
                .createInstance();
    }

    @Override
    public void tick() {
        previousRotation = rotation;

        if (context.disabled
                || VecHelper.isVecPointingTowards(context.relativeMotion, facing.getOpposite()))
            return;

        float deg = context.getAnimationSpeed();

        rotation += deg / 20;

        rotation %= 360;
    }

    @Override
    public void beginFrame() {

        drillHead.setIdentityTransform()
                .translate(context.localPos)
                .center()
                .rotateToFace(facing.getOpposite())
                .rotateZDegrees(getRotation())
                .uncenter()
                .setChanged();

        if (!EverdrillBlockEntity.readEnchantments(context.blockEntityData).isEmpty()) {
            enchantedDrillHead.setIdentityTransform()
                    .translate(context.localPos)
                    .center()
                    .rotateToFace(facing.getOpposite())
                    .rotateZDegrees(getRotation())
                    .uncenter()
                    .setChanged();
        }
    }

    private float getRotation() {
        return AngleHelper.angleLerp(AnimationTickHolder.getPartialTicks(), previousRotation, rotation);
    }

    @Override
    protected void _delete() {
        drillHead.delete();
        enchantedDrillHead.delete();
    }
}
