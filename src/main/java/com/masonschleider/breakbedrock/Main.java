package com.masonschleider.breakbedrock;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MOD_ID)
@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class Main {
    public static final String MOD_ID = "breakbedrock";
    
    static final long BREAK_DELAY = 1250;
    static final Logger LOGGER = LogManager.getLogger(Main.MOD_ID);
    
    private static final float BREAK_DAMAGE = 1 / (BREAK_DELAY / 50.0F);
    
    private static BlockPos currentBlockPos;
    private static PlayerControllerReflector playerControllerReflector;
    
    @SubscribeEvent
    static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!event.getPlayer().world.isRemote)
            return;
        
        currentBlockPos = event.getPos();
    }
    
    @SubscribeEvent
    static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
    
        if (playerControllerReflector == null) {
            if (!event.player.world.isRemote)
                return;
    
            PlayerController playerController = Minecraft.getInstance().playerController;
            playerControllerReflector = new PlayerControllerReflector(playerController);
        }
    
        if (currentBlockPos == null)
            return;
    
        World world = event.player.world;
        Block block = world.getBlockState(currentBlockPos).getBlock();
        PlayerController playerController = playerControllerReflector.getPlayerController();
    
        if (!playerController.getIsHittingBlock() || block != Blocks.BEDROCK)
            return;
        
        try {
            float currentBlockDamage = playerControllerReflector.getCurrentBlockDamage();
            
            if (world.isRemote) {
                LOGGER.info("currentBlockDamage: " + currentBlockDamage);
                playerControllerReflector.setCurrentBlockDamage(currentBlockDamage + BREAK_DAMAGE);
            } else if (currentBlockDamage >= 1.0F){
                IFluidState fluidState = world.getFluidState(currentBlockPos);
                world.setBlockState(currentBlockPos, fluidState.getBlockState(), 1);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
