package com.masonschleider.breakbedrock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
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
    public static final String MODID = "breakbedrock";
    
    private static final Logger LOGGER = LogManager.getLogger(Main.MODID);
    
    @SubscribeEvent
    public static void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        BlockPos blockPos = event.getPos();
        PlayerEntity playerEntity = event.getPlayer();
        World world = playerEntity.world;
        
        if (world.isRemote || blockPos.getY() == 0)
            return;
        
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        
        if (block != Blocks.BEDROCK)
            return;
        
        world.destroyBlock(blockPos, false);
    }
}
