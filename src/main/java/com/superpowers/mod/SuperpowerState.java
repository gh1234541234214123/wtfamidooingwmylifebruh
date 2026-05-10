package com.superpowers.mod;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks which players have superpowers enabled.
 * Each power can be toggled independently.
 */
public class SuperpowerState {

    private static final Set<UUID> flightEnabled    = new HashSet<>();
    private static final Set<UUID> speedEnabled     = new HashSet<>();
    private static final Set<UUID> strengthEnabled  = new HashSet<>();

    // ── Flight ──────────────────────────────────────────────────────────────

    public static boolean hasFlight(PlayerEntity player) {
        return flightEnabled.contains(player.getUuid());
    }

    public static boolean toggleFlight(PlayerEntity player) {
        UUID id = player.getUuid();
        if (flightEnabled.contains(id)) {
            flightEnabled.remove(id);
            return false;
        } else {
            flightEnabled.add(id);
            return true;
        }
    }

    public static void setFlight(PlayerEntity player, boolean enabled) {
        if (enabled) flightEnabled.add(player.getUuid());
        else         flightEnabled.remove(player.getUuid());
    }

    // ── Speed ───────────────────────────────────────────────────────────────

    public static boolean hasSpeed(PlayerEntity player) {
        return speedEnabled.contains(player.getUuid());
    }

    public static boolean toggleSpeed(PlayerEntity player) {
        UUID id = player.getUuid();
        if (speedEnabled.contains(id)) {
            speedEnabled.remove(id);
            return false;
        } else {
            speedEnabled.add(id);
            return true;
        }
    }

    public static void setSpeed(PlayerEntity player, boolean enabled) {
        if (enabled) speedEnabled.add(player.getUuid());
        else         speedEnabled.remove(player.getUuid());
    }

    // ── Strength ─────────────────────────────────────────────────────────────

    public static boolean hasStrength(PlayerEntity player) {
        return strengthEnabled.contains(player.getUuid());
    }

    public static boolean toggleStrength(PlayerEntity player) {
        UUID id = player.getUuid();
        if (strengthEnabled.contains(id)) {
            strengthEnabled.remove(id);
            return false;
        } else {
            strengthEnabled.add(id);
            return true;
        }
    }

    public static void setStrength(PlayerEntity player, boolean enabled) {
        if (enabled) strengthEnabled.add(player.getUuid());
        else         strengthEnabled.remove(player.getUuid());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    public static void removeAll(PlayerEntity player) {
        UUID id = player.getUuid();
        flightEnabled.remove(id);
        speedEnabled.remove(id);
        strengthEnabled.remove(id);
    }
}
