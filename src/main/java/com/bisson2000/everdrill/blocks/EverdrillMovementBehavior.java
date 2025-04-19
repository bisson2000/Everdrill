package com.bisson2000.everdrill.blocks;

import com.bisson2000.everdrill.render.EverdrillActorVisual;
import com.bisson2000.everdrill.render.ModDrillRenderer;
import com.bisson2000.everdrill.util.BlockBreakingUtil;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

// Credits to https://www.curseforge.com/minecraft/mc-mods/create-enchantable-machinery

public class EverdrillMovementBehavior extends DrillMovementBehaviour {

    @Override
    protected void destroyBlock(MovementContext context, BlockPos breakingPos) {
        Level level = context.world;

        ItemStack breakingItem = new ItemStack(Items.NETHERITE_PICKAXE);
        breakingItem.setTag(context.blockEntityData);

        BlockBreakingUtil.destroyBlockAs(level, breakingPos, (Player)null, breakingItem, 1.0F, (stack) -> {
            this.dropItem(context, stack);
        });
    }

    @Override
    protected float getBlockBreakingSpeed(MovementContext context) {
        ItemStack itemStack = ItemStack.EMPTY;
        itemStack.setTag(context.blockEntityData);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);

        if (!enchantments.containsKey(Enchantments.BLOCK_EFFICIENCY)) {
            return super.getBlockBreakingSpeed(context);
        }

        return super.getBlockBreakingSpeed(context) * (enchantments.get(Enchantments.BLOCK_EFFICIENCY) + 1);
    }

    @Override
    public @Nullable ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new EverdrillActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffer) {
        if (!VisualizationManager.supportsVisualization(context.world)) {
            ModDrillRenderer.renderInContraption(context, renderWorld, matrices, buffer);
        }
    }
}
