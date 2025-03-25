package com.bisson2000.everdrill.tag;

import com.bisson2000.everdrill.Everdrill;
import com.bisson2000.everdrill.config.EverdrillConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Everdrill.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TagEventManager {

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() != TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            return;
        }

        final HashSet<String> ALLOW_LISTED_BLOCKS_SET = new HashSet<>(EverdrillConfig.ALLOW_LISTED_BLOCKS.get());
        final HashSet<String> DENY_LISTED_BLOCKS_SET = new HashSet<>(EverdrillConfig.DENY_LISTED_BLOCKS.get());
        final HashSet<String> ALLOW_LISTED_TAGS_SET = new HashSet<>(EverdrillConfig.ALLOW_LISTED_TAGS.get());
        final HashSet<String> DENY_LISTED_TAGS_SET = new HashSet<>(EverdrillConfig.DENY_LISTED_TAGS.get());

        HashSet<Block> targetList = new HashSet<>();

        // Search through all blocks
        targetList.addAll(
                ForgeRegistries.BLOCKS.getEntries().stream().filter(entry -> {
                    ResourceKey<Block> resourceKey = entry.getKey();
                    String name = resourceKey.location().toString();
                    Set<String> blockTags = entry.getValue().defaultBlockState().getTags().map(t -> t.location().toString()).collect(Collectors.toSet());

                    // Allow
                    boolean match = ALLOW_LISTED_BLOCKS_SET.contains(name);
                    match = match || !Collections.disjoint(ALLOW_LISTED_TAGS_SET, blockTags); // Contains any tag

                    // Deny
                    match = match && !DENY_LISTED_BLOCKS_SET.contains(name);
                    match = match && Collections.disjoint(DENY_LISTED_TAGS_SET, blockTags); // Contains no tag

                    return match;
                }).map(Map.Entry::getValue).collect(Collectors.toCollection(HashSet::new))
        );

        // Complete operation
        EverdrillConfig.SetTargetedBlocks(targetList);
    }

}
