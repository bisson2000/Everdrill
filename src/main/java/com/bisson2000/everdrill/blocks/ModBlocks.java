package com.bisson2000.everdrill.blocks;

import com.bisson2000.everdrill.Everdrill;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.drill.DrillMovementBehaviour;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.ForgeRegistries;

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
            .onRegister(AllMovementBehaviours.movementBehaviour(new EverdrillMovementBehavior()))
            .recipe((c, p) -> {
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get())
                        .define('L', ItemTags.LAPIS_ORES)
                        .define('D', ItemTags.DIAMOND_ORES)
                        .define('M', AllBlocks.MECHANICAL_DRILL.asItem())
                        .pattern("LDL")
                        .pattern("DMD")
                        .pattern("LDL")
                        .unlockedBy("has_" + c.getName(), RegistrateRecipeProvider.has(c.get()))
                        .save(p, new ResourceLocation(Everdrill.MOD_ID, c.getName()));
            })
            .item()
            .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
            .model((c, p) -> {
                ResourceLocation itemLocation = new ResourceLocation(Everdrill.MOD_ID, "item/everdrill/source_item");
                ResourceLocation headLocation = new ResourceLocation(Everdrill.MOD_ID, "block/everdrill/source_head");
                p.withExistingParent(c.getName(), itemLocation);
                p.withExistingParent("block/" + c.getName() + "/head", headLocation);
            })
            .tab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey()) // Necessary otherwise modernfix will hide the block
            .build()
            .register();

    public static void register() {
    }
}
