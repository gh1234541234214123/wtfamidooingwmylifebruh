package com.superpowers.mod.mixin;

import com.superpowers.mod.SuperpowerState;
import com.superpowers.mod.command.SuperpowersCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Re-applies ability flags after the player respawns, because Minecraft
 * resets abilities on death.
 */
@Mixin(net.minecraft.server.PlayerManager.class)
public abstract class LivingEntityMixin {

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void restoreSuperpowersOnRespawn(ServerPlayerEntity player, boolean alive, CallbackInfo ci) {
        if (SuperpowerState.hasFlight(player)) {
            SuperpowersCommand.applyFlight(player, true);
        }
        if (SuperpowerState.hasSpeed(player)) {
            SuperpowersCommand.applySpeed(player, true);
        }
        // Strength is tick-driven; it will self-restore within one tick.
    }
}
