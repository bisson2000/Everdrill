package com.bisson2000.everdrill.render;

import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class ModDrillRenderer extends KineticBlockEntityRenderer<EverdrillBlockEntity> {

    private RandomSource RANDOM = RandomSource.create();
    private BlockEntityRendererProvider.Context context;

    public ModDrillRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(EverdrillBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.DRILL_HEAD, state);
    }

    @Override
    protected void renderSafe(EverdrillBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {

        if (be.getLevel() == null) return;
        if (be.getEnchantmentInstances().isEmpty()) return;

        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = new SheetedDecalTextureGenerator(buffer.getBuffer(CustomRenderType.GLINT), pose.pose(), pose.normal(), 0.007125f);

        this.context.getBlockRenderDispatcher().renderBatched(
                be.getBlockState(),
                be.getBlockPos(),
                be.getLevel(),
                poseStack,
                consumer,
                true,
                this.RANDOM,
                ModelData.EMPTY,
                null
        );
        renderRotatingBuffer(be, getRotatedModel(be, getRenderedBlockState(be)), poseStack, consumer, light);
        super.renderSafe(be, partialTicks, poseStack, buffer, light, overlay);

        poseStack.popPose();
    }

    public void renderInContraption(MovementContext movementContext, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        BlockState state = movementContext.state;
        SuperByteBuffer superBuffer = CachedBufferer.partial(AllPartialModels.DRILL_HEAD, state);
        Direction facing = (Direction)state.getValue(DrillBlock.FACING);
        float speed = !movementContext.contraption.stalled && VecHelper.isVecPointingTowards(movementContext.relativeMotion, facing.getOpposite()) ? 0.0F : movementContext.getAnimationSpeed();
        float time = AnimationTickHolder.getRenderTime() / 20.0F;
        float angle = time * speed % 360.0F;

        VertexConsumer consumer = new SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                matrices.getViewProjection().last().pose(),
                matrices.getViewProjection().last().normal(),
                0.0078125f
        );

        double xRotation = AngleHelper.verticalAngle(facing);
        double yRotation = AngleHelper.horizontalAngle(facing);
        double zRotation = 0;

        if (movementContext.contraption.stalled || !VecHelper.isVecPointingTowards(movementContext.relativeMotion, facing.getOpposite())) {
            zRotation = movementContext.getAnimationSpeed() % 360.0;
        }

        superBuffer
                .transform(matrices.getModel())
                .centre()
                .rotateY(yRotation)
                .rotateX(xRotation)
                .rotateZ(zRotation)
                .unCentre()
                .light(matrices.getWorld(), ContraptionRenderDispatcher.getContraptionWorldLight(movementContext, renderWorld))
                .renderInto(matrices.getViewProjection(), consumer);

        matrices.getModelViewProjection().pushPose();
        TransformStack.cast(matrices.getModelViewProjection()).translate(movementContext.localPos);
        Minecraft.getInstance().getBlockRenderer().renderBatched(
                movementContext.state,
                movementContext.localPos,
                movementContext.world,
                matrices.getModelViewProjection(),
                consumer,
                true,
                RANDOM,
                ModelData.EMPTY,
                null
        );
        matrices.getModelViewProjection().popPose();
    }
}
