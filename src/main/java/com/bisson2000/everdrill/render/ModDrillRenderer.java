package com.bisson2000.everdrill.render;

import com.bisson2000.everdrill.config.EverdrillConfig;
import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.drill.DrillBlock;
import com.simibubi.create.content.kinetics.drill.DrillRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class ModDrillRenderer extends KineticBlockEntityRenderer<EverdrillBlockEntity> {

    private static RandomSource RANDOM = RandomSource.create();
    private BlockEntityRendererProvider.Context context;

    public ModDrillRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected SuperByteBuffer getRotatedModel(EverdrillBlockEntity be, BlockState state) {
        return CachedBuffers.partialFacing(AllPartialModels.DRILL_HEAD, state);
    }

    @Override
    protected void renderSafe(EverdrillBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {

        if (be.getLevel() == null) return;
        if (be.getEnchantmentInstances().isEmpty()) return;

        poseStack.pushPose();

        if (EverdrillConfig.ENABLE_ENCHANTMENT_GLINT.get()) {
            PoseStack.Pose pose = poseStack.last();

            // body
            VertexConsumer consumer = new SheetedDecalTextureGenerator(buffer.getBuffer(CustomRenderType.GLINT), pose.pose(), pose.normal(), 0.007125f);
            this.context.getBlockRenderDispatcher().renderBatched(
                    be.getBlockState(),
                    be.getBlockPos(),
                    be.getLevel(),
                    poseStack,
                    consumer,
                    true,
                    ModDrillRenderer.RANDOM,
                    ModelData.EMPTY,
                    null
            );

            // Drill head
            if (!VisualizationManager.supportsVisualization(be.getLevel())) {
                renderRotatingBuffer(be, getRotatedModel(be, getRenderedBlockState(be)), poseStack, consumer, light);
            }

        }

        super.renderSafe(be, partialTicks, poseStack, buffer, light, overlay);

        poseStack.popPose();
    }

    public static void renderInContraption(MovementContext movementContext, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        DrillRenderer.renderInContraption(movementContext, renderWorld, matrices, buffer);

        BlockState state = movementContext.state;
        SuperByteBuffer superBuffer = CachedBuffers.partial(AllPartialModels.DRILL_HEAD, state);
        Direction facing = state.getValue(DrillBlock.FACING);

        float speed = movementContext.contraption.stalled || !VecHelper.isVecPointingTowards(movementContext.relativeMotion, facing.getOpposite()) ? movementContext.getAnimationSpeed() : 0.0f;
        float time = AnimationTickHolder.getRenderTime() / 20.0f;
        float angle = (time * speed) % 360;

        VertexConsumer consumer = new SheetedDecalTextureGenerator(
                buffer.getBuffer(CustomRenderType.GLINT),
                matrices.getViewProjection().last().pose(),
                matrices.getViewProjection().last().normal(),
                0.007125f
        );

        // Drill head
        superBuffer
                .transform(matrices.getModel())
                .center()
                .rotateYDegrees(AngleHelper.horizontalAngle(facing))
                .rotateXDegrees(AngleHelper.verticalAngle(facing))
                .rotateZDegrees(angle)
                .uncenter()
                .light(LevelRenderer.getLightColor(renderWorld, movementContext.localPos))
                .useLevelLight(movementContext.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), consumer);

        // Body
        matrices.getModelViewProjection().pushPose(); // Push

        TransformStack.of(matrices.getModelViewProjection()).translate(movementContext.localPos);
        Minecraft.getInstance().getBlockRenderer().renderBatched(
                movementContext.state,
                movementContext.localPos,
                movementContext.world,
                matrices.getModelViewProjection(),
                consumer,
                true,
                ModDrillRenderer.RANDOM,
                ModelData.EMPTY,
                null
        );

        matrices.getModelViewProjection().popPose(); // Pop
    }
}
