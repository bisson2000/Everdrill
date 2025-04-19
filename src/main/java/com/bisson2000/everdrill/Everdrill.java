package com.bisson2000.everdrill;

import com.bisson2000.everdrill.blocks.ModBlocks;
import com.bisson2000.everdrill.command.CommandHandler;
import com.bisson2000.everdrill.config.CustomStress;
import com.bisson2000.everdrill.config.EverdrillConfig;
import com.bisson2000.everdrill.entities.ModEntities;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.infrastructure.config.CServer;
import com.simibubi.create.infrastructure.config.CStress;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
// NaturaDrill
// Primal Drill
@Mod(Everdrill.MOD_ID)
public class Everdrill
{

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "everdrill";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Everdrill.MOD_ID);
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public Everdrill() {
        this(FMLJavaModLoadingContext.get());
    }

    public Everdrill(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Config
        context.registerConfig(ModConfig.Type.COMMON, EverdrillConfig.SPEC);

        // Registration
        Everdrill.REGISTRATE.registerEventListeners(modEventBus);

        // CustomStress
        BlockStressValues.IMPACTS.registerProvider(CustomStress::getImpact);

        ModBlocks.register(); // Force static variables to be initialized
        ModEntities.register(); // Force static variables to be initialized

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event)
    {
        // Commands
        CommandHandler.commandHandler.onRegisterCommandsEvent(event);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = Everdrill.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
        }
    }
}
