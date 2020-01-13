package com.masonschleider.breakbedrock;

import net.minecraft.client.multiplayer.PlayerController;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class PlayerControllerReflector {
    private final Field curBlockDamageMP;
    private final PlayerController playerController;
    
    public PlayerControllerReflector(PlayerController playerController) {
        this.curBlockDamageMP =
                ObfuscationReflectionHelper.findField(PlayerController.class, SrgMappings.curBlockDamageMP);
        this.playerController = playerController;
    }
    
    public float getCurrentBlockDamage() throws IllegalAccessException {
        return this.curBlockDamageMP.getFloat(this.playerController);
    }
    
    public void setCurrentBlockDamage(float value) throws IllegalAccessException {
        this.curBlockDamageMP.setFloat(this.playerController, value);
    }
    
    public PlayerController getPlayerController() { return this.playerController; }
}
