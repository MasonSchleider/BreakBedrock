package com.masonschleider.breakbedrock;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MODID)
@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class Main {
    static final String MODID = "breakbedrock";
    
    private static final long BREAK_PERIOD = 1250;
    private static final long BREAK_TIMEOUT = 125;
    private static final Logger LOGGER = LogManager.getLogger(Main.MODID);
    
    private static int breakDelay = 0;
    private static long clickDuration = 0;
    private static long lastClickTime = 0;
    
    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        World world = event.getPlayer().world;
    
        if (breakDelay > 0) {
            if (world.isRemote) {
                breakDelay--;
                lastClickTime = System.currentTimeMillis();
            }
            event.setCanceled(true);
            return;
        }
    
        BlockPos blockPos = event.getPos();
        BlockState blockState = world.getBlockState(blockPos);
    
        if (blockState.getBlock() != Blocks.BEDROCK || blockPos.getY() == 0)
            return;
        
        if (world.isRemote) {
            long currentTimeMillis = System.currentTimeMillis();
            long deltaTime = currentTimeMillis - lastClickTime;
    
            LOGGER.info("breakTime: " + clickDuration + ", delta: " + deltaTime);
    
            if (deltaTime > BREAK_TIMEOUT) {
                clickDuration = 0;
                event.setCanceled(true);
            } else if (clickDuration < BREAK_PERIOD) {
                clickDuration += deltaTime;
                event.setCanceled(true);
            }
    
            lastClickTime = currentTimeMillis;
        } else if (clickDuration >= BREAK_PERIOD) {
            breakDelay = 5;
            clickDuration = 0;
            world.destroyBlock(blockPos, false);
        } else {
            event.setCanceled(true);
        }
    }
}
