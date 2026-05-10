package com.superpowers.mod.mixin;

import com.superpowers.mod.SuperpowerState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    /**
     * Super Strength – one-hit kill.
     * We boost the damage dealt to living entities to a very large value
     * when strength is active.
     */
    @ModifyVariable(
        method = "attack",
        at = @At("HEAD"),
        argsOnly = true
    )
    // The first arg to attack() is the Entity target; we don't modify it,
    // but we hook the attack method itself via @Inject below.
    private net.minecraft.entity.Entity onAttackEntity(net.minecraft.entity.Entity target) {
        return target;
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void superStrengthAttack(net.minecraft.entity.Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (SuperpowerState.hasStrength(self) && target instanceof LivingEntity living) {
            // 1000 damage = always fatal
            living.damage(self.getWorld().getDamageSources().playerAttack(self), 1000f);
        }
    }

    /**
     * Insta-mine – when super strength is active the player mines at maximum speed.
     * We achieve this by injecting into the block-breaking progress tick and
     * bumping the mining speed multiplier to a huge value via the status effect
     * approach (Haste 10 is applied alongside strength).
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void applyHasteWithStrength(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        if (self.getWorld().isClient) return; // server side only

        boolean wantsHaste = SuperpowerState.hasStrength(self);
        boolean hasHaste = self.hasStatusEffect(
            net.minecraft.entity.effect.StatusEffects.HASTE
        );

        if (wantsHaste && !hasHaste) {
            self.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.HASTE,
                Integer.MAX_VALUE,
                10,    // Haste XI – insta-mines almost everything
                false,
                false,
                true
            ));
        } else if (!wantsHaste && hasHaste) {
            self.removeStatusEffect(
                net.minecraft.registry.entry.RegistryEntry.of(
                    net.minecraft.entity.effect.StatusEffects.HASTE.value()
                )
            );
        }
    }
}
