package dev.important;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ImportantConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("important.json");
    private static final Map<String, Boolean> DEFAULTS = new LinkedHashMap<>();
    private static final Map<String, Boolean> VALUES = new LinkedHashMap<>();
    private static String accent = "#55B8FF";

    static {
        DEFAULTS.put("Watermark", true);
        DEFAULTS.put("FPS", true);
        DEFAULTS.put("Coordinates", true);
        DEFAULTS.put("Direction", false);
        DEFAULTS.put("Keystrokes", false);
        DEFAULTS.put("CPS Counter", false);
        DEFAULTS.put("Armor HUD", true);
        DEFAULTS.put("Potion HUD", true);
        DEFAULTS.put("Target HUD", false);
        DEFAULTS.put("Smooth Zoom", true);
        DEFAULTS.put("No Hurt Cam", false);
        DEFAULTS.put("Block Overlay", false);
        DEFAULTS.put("Custom Sky", false);
        DEFAULTS.put("Hit Particles", true);
        DEFAULTS.put("Particle Trail", false);
        DEFAULTS.put("Enchantment Glow", false);
        DEFAULTS.put("Clean Chat", false);
        DEFAULTS.put("Low Fire", false);
        DEFAULTS.put("Custom Crosshair", false);
        DEFAULTS.put("Minimal Scoreboard", false);
    }

    private ImportantConfig() {}

    public static synchronized void load() {
        VALUES.clear(); VALUES.putAll(DEFAULTS);
        if (!Files.exists(FILE)) { save(); return; }
        try {
            JsonObject json = GSON.fromJson(Files.readString(FILE), JsonObject.class);
            if (json == null) return;
            if (json.has("modules") && json.get("modules").isJsonObject()) {
                JsonObject modules = json.getAsJsonObject("modules");
                for (String key : DEFAULTS.keySet()) if (modules.has(key)) VALUES.put(key, modules.get(key).getAsBoolean());
            }
            if (json.has("accent")) accent = json.get("accent").getAsString();
        } catch (Exception e) {
            System.err.println("[Important] Could not read config: " + e.getMessage());
        }
    }

    public static synchronized void save() {
        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            JsonObject modules = new JsonObject();
            VALUES.forEach(modules::addProperty);
            root.add("modules", modules);
            root.addProperty("accent", accent);
            Files.writeString(FILE, GSON.toJson(root));
        } catch (IOException e) {
            System.err.println("[Important] Could not save config: " + e.getMessage());
        }
    }

    public static synchronized boolean enabled(String name) { return VALUES.getOrDefault(name, false); }
    public static synchronized void toggle(String name) { VALUES.put(name, !enabled(name)); save(); }
    public static synchronized void reset() { VALUES.clear(); VALUES.putAll(DEFAULTS); accent = "#55B8FF"; save(); }
    public static synchronized Map<String, Boolean> all() { return new LinkedHashMap<>(VALUES); }
    public static synchronized String accent() { return accent; }
    public static synchronized void cycleAccent() {
        accent = switch (accent) { case "#55B8FF" -> "#B18CFF"; case "#B18CFF" -> "#70E0A0"; default -> "#55B8FF"; };
        save();
    }
}
