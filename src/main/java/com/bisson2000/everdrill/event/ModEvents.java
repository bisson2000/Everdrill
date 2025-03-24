package com.bisson2000.everdrill.event;

import com.bisson2000.everdrill.Everdrill;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Everdrill.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onGenericBlockBreakEvent(BlockEvent.BreakEvent event) {
//        Player player  = event.getPlayer();
//        if (!(player.level() instanceof ServerLevel serverLevel)) {
//            return;
//        }
//
//        final BlockPos pos = event.getPos();
//        final BlockState blockState = event.getState();
//
//        NaturalBlockTrackerCapability.getNaturalBlockTracker(serverLevel.getChunkAt(pos)).ifPresent(iNaturalBlockTracker -> {
//            if (!(iNaturalBlockTracker instanceof NaturalBlockTracker naturalBlockTracker)) return;
//
//            // harvested, will be destroyed
//            boolean isNatural = naturalBlockTracker.isNatural(pos);
//        });
//
//        if (!ReMineConfig.isTargeted(blockState.getBlock()) || !EnchantmentHelper.hasSilkTouch(player.getMainHandItem())) {
//            return;
//        }

        // TODO?
    }
}
