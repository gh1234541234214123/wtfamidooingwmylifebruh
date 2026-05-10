# ✨ Superpowers Mod — Fabric 1.21.4

A lightweight Fabric mod that grants the player three classic superpowers,
each togglable at any time with a simple chat command.

---

## Superpowers

| Power | What it does |
|---|---|
| **Flight** | Grants creative-style free flight (no Creative mode needed) |
| **Super Speed** | Applies Speed X (≈ 10× walk speed) as a permanent status effect |
| **Super Strength** | Kills any mob/player in one hit; instamines all blocks (Haste XI) |

---

## Commands

All commands are available to every player (no OP required).

```
/superpowers              – show help
/superpowers flight       – toggle flight on/off
/superpowers speed        – toggle super speed on/off
/superpowers strength     – toggle super strength on/off
/superpowers all          – toggle ALL powers on (or off if any are active)
/superpowers status       – show which powers are currently active
```

---

## Building

### Prerequisites
- Java 21
- Gradle 8+  (or use the included wrapper)

### Steps

```bash
# 1. Clone / unzip the project
cd superpowers-mod

# 2. Generate Minecraft sources (optional but useful for IDE)
./gradlew genSources

# 3. Build the mod JAR
./gradlew build

# The output JAR will be at:
#   build/libs/superpowers-1.0.0.jar
```

### Installing
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.4.
2. Install [Fabric API](https://modrinth.com/mod/fabric-api).
3. Drop `superpowers-1.0.0.jar` into your `.minecraft/mods/` folder.
4. Launch the game!

---

## Project Structure

```
src/
├── main/
│   ├── java/com/superpowers/mod/
│   │   ├── SuperpowersMod.java       ← mod init, command registration
│   │   ├── SuperpowerState.java      ← per-player power state (in-memory)
│   │   ├── command/
│   │   │   └── SuperpowersCommand.java ← /superpowers command tree
│   │   └── mixin/
│   │       ├── PlayerEntityMixin.java  ← one-hit kill + haste injection
│   │       └── LivingEntityMixin.java  ← restore powers on respawn
│   └── resources/
│       ├── fabric.mod.json
│       ├── superpowers.mixins.json
│       └── superpowers.client.mixins.json
└── client/
    └── java/com/superpowers/mod/client/
        └── SuperpowersClient.java    ← client init (keybinds future home)
```

---

## Extending the Mod

### Adding a Keybind (client-side)
In `SuperpowersClient.java`:
```java
KeyBinding flightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
    "key.superpowers.flight", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "Superpowers"
));

ClientTickEvents.END_CLIENT_TICK.register(client -> {
    if (flightKey.wasPressed() && client.player != null) {
        // Send command to server via chat packet
        client.player.networkHandler.sendChatCommand("superpowers flight");
    }
});
```

### Adding More Powers
1. Add a new `Set<UUID>` + getter/toggle/setter to `SuperpowerState.java`.
2. Add a new `CommandManager.literal("yourpower")` branch in `SuperpowersCommand.java`.
3. Implement the effect via a Mixin or a tick event.

---

## License
MIT — free to use, modify, and distribute.
