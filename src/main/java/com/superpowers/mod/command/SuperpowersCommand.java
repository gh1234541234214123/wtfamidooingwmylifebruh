package com.superpowers.mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.superpowers.mod.SuperpowerState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Registers /superpowers commands.
 *
 * Usage:
 *   /superpowers flight      – toggle creative-style flight
 *   /superpowers speed       – toggle super speed
 *   /superpowers strength    – toggle one-hit kill / insta-mine
 *   /superpowers all         – toggle all three on/off
 *   /superpowers status      – show current state
 */
public class SuperpowersCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(
            CommandManager.literal("superpowers")
                .requires(source -> source.hasPermissionLevel(0)) // any player can use

                .then(CommandManager.literal("flight")
                    .executes(ctx -> toggleFlight(ctx.getSource())))

                .then(CommandManager.literal("speed")
                    .executes(ctx -> toggleSpeed(ctx.getSource())))

                .then(CommandManager.literal("strength")
                    .executes(ctx -> toggleStrength(ctx.getSource())))

                .then(CommandManager.literal("all")
                    .executes(ctx -> toggleAll(ctx.getSource())))

                .then(CommandManager.literal("status")
                    .executes(ctx -> showStatus(ctx.getSource())))

                // Default: show help
                .executes(ctx -> showHelp(ctx.getSource()))
        );
    }

    // ── Handlers ─────────────────────────────────────────────────────────────

    private static int toggleFlight(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        boolean on = SuperpowerState.toggleFlight(player);
        applyFlight(player, on);
        sendMessage(player, "✈  Flight " + (on ? "ENABLED" : "DISABLED"), on);
        return 1;
    }

    private static int toggleSpeed(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        boolean on = SuperpowerState.toggleSpeed(player);
        applySpeed(player, on);
        sendMessage(player, "⚡ Super Speed " + (on ? "ENABLED" : "DISABLED"), on);
        return 1;
    }

    private static int toggleStrength(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        boolean on = SuperpowerState.toggleStrength(player);
        sendMessage(player, "💪 Super Strength " + (on ? "ENABLED" : "DISABLED"), on);
        return 1;
    }

    private static int toggleAll(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();

        // If any power is currently on, turn everything off; otherwise turn all on.
        boolean anyOn = SuperpowerState.hasFlight(player)
                     || SuperpowerState.hasSpeed(player)
                     || SuperpowerState.hasStrength(player);
        boolean on = !anyOn;

        SuperpowerState.setFlight(player, on);
        SuperpowerState.setSpeed(player, on);
        SuperpowerState.setStrength(player, on);

        applyFlight(player, on);
        applySpeed(player, on);

        String msg = on ? "✨ All superpowers ENABLED!" : "❌ All superpowers DISABLED.";
        sendMessage(player, msg, on);
        return 1;
    }

    private static int showStatus(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        player.sendMessage(Text.literal("--- Superpowers Status ---").formatted(Formatting.GOLD));
        player.sendMessage(statusLine("✈  Flight",   SuperpowerState.hasFlight(player)));
        player.sendMessage(statusLine("⚡ Speed",    SuperpowerState.hasSpeed(player)));
        player.sendMessage(statusLine("💪 Strength", SuperpowerState.hasStrength(player)));
        return 1;
    }

    private static int showHelp(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        player.sendMessage(Text.literal("--- Superpowers ---").formatted(Formatting.GOLD));
        player.sendMessage(Text.literal("/superpowers flight    – toggle flight").formatted(Formatting.YELLOW));
        player.sendMessage(Text.literal("/superpowers speed     – toggle super speed").formatted(Formatting.YELLOW));
        player.sendMessage(Text.literal("/superpowers strength  – toggle super strength").formatted(Formatting.YELLOW));
        player.sendMessage(Text.literal("/superpowers all       – toggle all powers").formatted(Formatting.YELLOW));
        player.sendMessage(Text.literal("/superpowers status    – show current state").formatted(Formatting.YELLOW));
        return 1;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Grant or revoke creative-style flight for a server player.
     * We touch only the allowFlying / flying flags so survival health etc. is unaffected.
     */
    public static void applyFlight(ServerPlayerEntity player, boolean on) {
        player.getAbilities().allowFlying = on;
        if (!on) {
            player.getAbilities().flying = false;
        }
        player.sendAbilitiesUpdate();
    }

    /**
     * Apply or remove the speed effect.
     * Speed level 9 = ~10× normal walk speed; feels super-heroic.
     */
    public static void applySpeed(ServerPlayerEntity player, boolean on) {
        if (on) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SPEED,
                Integer.MAX_VALUE, // duration: effectively permanent
                9,                 // amplifier: Speed X
                false,             // ambient
                false,             // show particles
                true               // show icon
            ));
        } else {
            player.removeStatusEffect(net.minecraft.registry.entry.RegistryEntry.of(
                net.minecraft.entity.effect.StatusEffects.SPEED.value()
            ));
        }
    }

    private static void sendMessage(ServerPlayerEntity player, String msg, boolean on) {
        player.sendMessage(Text.literal(msg).formatted(on ? Formatting.GREEN : Formatting.RED));
    }

    private static Text statusLine(String label, boolean on) {
        return Text.literal(label + ": ").formatted(Formatting.WHITE)
            .append(Text.literal(on ? "ON" : "OFF").formatted(on ? Formatting.GREEN : Formatting.RED));
    }
}
