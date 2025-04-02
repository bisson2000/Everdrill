package com.bisson2000.everdrill.config;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;
import java.util.stream.Collectors;

public class EverdrillConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> NATURAL_ONLY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> USE_INTERNAL_BUFFER;
    public static final ForgeConfigSpec.ConfigValue<Boolean> TARGET_ALL_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENABLE_ENCHANTMENT_GLINT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOW_LISTED_TAGS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DENY_LISTED_TAGS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ALLOW_LISTED_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DENY_LISTED_BLOCKS;
    private static HashSet<Block> TARGETED_BLOCKS = new HashSet<>();

    static {
        BUILDER.push("Configs for Everdrill");

        NATURAL_ONLY = BUILDER.comment("True if only naturally generated blocks are valid")
                .define("Natural only", true);

        USE_INTERNAL_BUFFER = BUILDER.comment("True if the mined blocks should be stored within the everdrill's internal buffer. " +
                        "The items can be extracted with hoppers or other pipes. Good to use if you expect to have multiple drills used and you want to avoid lag.")
                .define("Use internal buffer", false);

        TARGET_ALL_BLOCKS = BUILDER.comment("True if you want to target all blocks")
                .define("Target all", false);

        ALLOW_LISTED_TAGS = BUILDER.comment("Which block tags are allowed to be mined more than once. Write the tag with modid:tag_name")
                .defineListAllowEmpty("Allowed tags", Arrays.asList("forge:ores"), entry -> entry instanceof String);

        DENY_LISTED_TAGS = BUILDER.comment("Which block tags are NOT allowed to be mined more than once. Write the tag with modid:tag_name")
                .defineListAllowEmpty("Denied tags", Arrays.asList(), entry -> entry instanceof String);

        ALLOW_LISTED_BLOCKS = BUILDER.comment("Which blocks are allowed to be mined more than once. Write the block with modid:block_name")
                .defineListAllowEmpty("Allowed blocks", Arrays.asList("minecraft:iron_ore", "minecraft:deepslate_iron_ore"), entry -> entry instanceof String);

        DENY_LISTED_BLOCKS = BUILDER.comment("Which blocks are NOT allowed to be mined more than once. Write the block with modid:block_name")
                .defineListAllowEmpty("Denied blocks", Arrays.asList("minecraft:oak_log", "minecraft:acacia_log"), entry -> entry instanceof String);

        ENABLE_ENCHANTMENT_GLINT = BUILDER.comment("True if you want the enchanted blocks to have a glint effect")
                .define("Enable glint", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void SetTargetedBlocks(HashSet<Block> set) {
        TARGETED_BLOCKS = set;
    }

    public static Set<Block> getTargetedBlocks() {
        return TARGETED_BLOCKS;
    }

    public static boolean isTargeted(Block block) {
        return TARGETED_BLOCKS.contains(block);
    }

}
