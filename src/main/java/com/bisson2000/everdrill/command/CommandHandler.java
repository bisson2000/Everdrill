package com.bisson2000.everdrill.command;

import com.bisson2000.everdrill.Everdrill;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;

public class CommandHandler {

    private static ArgumentBuilder<CommandSourceStack, ?> listOresCommand;
    public static CommandHandler commandHandler = new CommandHandler();

    private void registerCommands(CommandBuildContext ctx) {
        listOresCommand = ListOresCommand.register();
    }

    public void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        registerCommands(event.getBuildContext());
        event.getDispatcher().register(
                LiteralArgumentBuilder.<CommandSourceStack>literal(Everdrill.MOD_ID)
                        .then(listOresCommand)
        );
    }

}

