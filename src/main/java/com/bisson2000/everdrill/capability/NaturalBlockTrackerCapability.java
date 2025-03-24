package com.bisson2000.everdrill.capability;

import com.bisson2000.everdrill.Everdrill;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class NaturalBlockTrackerCapability {

    public static final Capability<INaturalBlockTracker> NATURAL_BLOCK_TRACKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Direction DEFAULT_FACING = null;
    public static final ResourceLocation ID = new ResourceLocation(Everdrill.MOD_ID, "chunk_blocks");

    public static void register(final RegisterCapabilitiesEvent event) {
        event.register(INaturalBlockTracker.class);
    }

    public static LazyOptional<INaturalBlockTracker> getNaturalBlockTracker(final Level level, final ChunkPos chunkPos) {
        return getNaturalBlockTracker(level.getChunk(chunkPos.x, chunkPos.z));
    }

    public static LazyOptional<INaturalBlockTracker> getNaturalBlockTracker(final LevelChunk chunk) {
        return chunk.getCapability(NATURAL_BLOCK_TRACKER_CAPABILITY, DEFAULT_FACING);
    }

    @Mod.EventBusSubscriber(modid = Everdrill.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void attachChunkCapabilities(final AttachCapabilitiesEvent<LevelChunk> event) {
            final LevelChunk chunk = event.getObject();
            final INaturalBlockTracker chunkBlocksCharge = new NaturalBlockTracker(chunk.getLevel(), chunk.getPos());
            event.addCapability(ID, new SerializableCapabilityProvider<>(NATURAL_BLOCK_TRACKER_CAPABILITY, DEFAULT_FACING, chunkBlocksCharge));
        }
    }

    @Mod.EventBusSubscriber(modid = Everdrill.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ModCapabilities {
        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            NaturalBlockTrackerCapability.register(event);
        }
    }
}
