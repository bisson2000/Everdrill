package com.bisson2000.everdrill.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;
import java.util.stream.Collectors;

public class EverdrillConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> BLOCK_CHARGES_IS_INFINITE;
    public static final ForgeConfigSpec.ConfigValue<Integer> BLOCK_CHARGES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> AUTO_ORE_SEARCH;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOW_LISTED_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DENY_LISTED_BLOCKS;
    private static HashSet<Block> TARGETED_BLOCKS = new HashSet<>();
    private static HashSet<Item> TARGETED_BLOCKS_NAMES = new HashSet<>();

    static {
        BUILDER.push("Configs for Re-mine");

        BLOCK_CHARGES_IS_INFINITE = BUILDER.comment("Can blocks be harvested infinitely")
                .define("Block charges is infinite", false);

        BLOCK_CHARGES = BUILDER.comment("How many times a block can be harvested before having it break")
                .defineInRange("Charges", 2, 0, Integer.MAX_VALUE);

        AUTO_ORE_SEARCH = BUILDER.comment("Search automatically for ores")
                .define("Auto ore search", true);

        ALLOW_LISTED_BLOCKS = BUILDER.comment("Which blocks are allowed to be mined more than once. Write the block with modid:block_name")
                .defineListAllowEmpty("Allowed blocks", Arrays.asList("minecraft:iron_ore", "minecraft:deepslate_iron_ore"), entry -> entry instanceof String);

        DENY_LISTED_BLOCKS = BUILDER.comment("Which blocks are NOT allowed to be mined more than once. Write the block with modid:block_name")
                .defineListAllowEmpty("Denied blocks", Arrays.asList("minecraft:oak_log", "minecraft:acacia_log"), entry -> entry instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void SetTargetedBlocks(HashSet<Block> set) {
        TARGETED_BLOCKS = set;
        TARGETED_BLOCKS_NAMES = set.stream().map(Block::asItem).collect(Collectors.toCollection(HashSet::new));
    }

    public static Set<Block> getTargetedBlocks() {
        return TARGETED_BLOCKS;
    }

    public static boolean isTargeted(Block block) {
        return TARGETED_BLOCKS.contains(block);
    }

    public static boolean isTargeted(Item item) {
        return TARGETED_BLOCKS_NAMES.contains(item);
    }

}
