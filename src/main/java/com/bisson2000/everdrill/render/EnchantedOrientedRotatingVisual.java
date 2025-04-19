package com.bisson2000.everdrill.render;

import com.bisson2000.everdrill.entities.EverdrillBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.OrientedRotatingVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.BakedModelBuilder;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Consumer;

public class EnchantedOrientedRotatingVisual<T extends KineticBlockEntity> extends OrientedRotatingVisual<T> {
    protected final RotatingInstance enchantedRotatingModel;

    /**
     * @param context
     * @param blockEntity
     * @param partialTick
     * @param from        The source model orientation to rotate away from.
     * @param to          The orientation to rotate to.
     * @param model       The model to spin.
     */
    public EnchantedOrientedRotatingVisual(VisualizationContext context, T blockEntity, float partialTick, Direction from, Direction to, Model model, PartialModel partialModel) {
        super(context, blockEntity, partialTick, from, to, model);

        enchantedRotatingModel = instancerProvider().instancer(
                AllInstanceTypes.ROTATING,
                BakedModelBuilder.create(partialModel.get()).materialFunc((a, b) -> Materials.GLINT).build()
        )
                .createInstance()
                .rotateToFace(from, to)
                .setup(blockEntity)
                .setPosition(getVisualPosition());

        if (blockEntity instanceof EverdrillBlockEntity everdrillBlockEntity) {
            this.enchantedRotatingModel.setVisible(!everdrillBlockEntity.getEnchantmentInstances().isEmpty());
        }
        enchantedRotatingModel.setChanged();
    }

    @Override
    public void update(float pt) {
        if (blockEntity instanceof EverdrillBlockEntity everdrillBlockEntity) {
            this.enchantedRotatingModel.setVisible(!everdrillBlockEntity.getEnchantmentInstances().isEmpty());
        }

        this.enchantedRotatingModel.setup(blockEntity).setChanged();
        super.update(pt);
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(enchantedRotatingModel);
    }

    @Override
    protected void _delete() {
        super._delete();
        enchantedRotatingModel.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(enchantedRotatingModel);
    }

    public static <T extends KineticBlockEntity> SimpleBlockEntityVisualizer.Factory<T> of(PartialModel partial) {
        return (context, blockEntity, partialTick) -> {
            Direction facing = blockEntity.getBlockState()
                    .getValue(BlockStateProperties.FACING);
            return new EnchantedOrientedRotatingVisual<>(context, blockEntity, partialTick, Direction.SOUTH, facing, Models.partial(partial), partial);
        };
    }
}
