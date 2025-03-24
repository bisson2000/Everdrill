package com.bisson2000.everdrill.command;

import com.bisson2000.everdrill.config.EverdrillConfig;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;

public class ListOresCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("listtargets").executes(ListOresCommand::run);
    }

    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        context.getSource().sendSuccess(() -> Component.literal("List of targeted blocks:"), true);
        HashSet<String> uniqueBlocks = new HashSet<>();
        for (Block block: EverdrillConfig.getTargetedBlocks()) {
            uniqueBlocks.add(block.getDescriptionId());
        }

        List<String> sortedBlocks = uniqueBlocks.stream().sorted((o1, o2) -> {
            String str1 = o1;
            String str2 = o2;
            return str1.compareTo(str2);
        }).toList();


        for (String blockName : sortedBlocks) {
            context.getSource().sendSuccess(() -> Component.literal(blockName), true);
        }

        return 0;
    }
}
