package dev.important;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class ImportantClient implements ClientModInitializer {
    public static final String MOD_ID = "important";
    public static final int ACCENT = 0xFF55B8FF;
    public static final int PANEL = 0xF0181B20;
    public static final int PANEL_LIGHT = 0xFF222831;
    public static final int TEXT = 0xFFF1F5F9;
    public static final int MUTED = 0xFF9AA7B5;
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        ImportantConfig.load();
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.important.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                new KeyBinding.Category(Identifier.of(MOD_ID, "main"))));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new ImportantScreen());
            }
        });
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, id("visual_hud"),
                (drawContext, tickCounter) -> renderHud(MinecraftClient.getInstance(), drawContext));
    }

    private static void renderHud(MinecraftClient client, net.minecraft.client.gui.DrawContext context) {
        if (client.player == null || client.options.hudHidden || client.currentScreen instanceof ImportantScreen) return;
        int x = 8, y = 8;
        if (ImportantConfig.enabled("FPS")) {
            context.drawTextWithShadow(client.textRenderer, "FPS  " + client.getCurrentFps(), x, y, ACCENT);
            y += 12;
        }
        if (ImportantConfig.enabled("Coordinates")) {
            String coords = String.format(
                    java.util.Locale.ROOT,
                    "XYZ  %.1f  %.1f  %.1f",
                    client.player.getX(),
                    client.player.getY(),
                    client.player.getZ()
);
            context.drawTextWithShadow(client.textRenderer, coords, x, y, TEXT);
            y += 12;
        }
        if (ImportantConfig.enabled("Direction")) {
            context.drawTextWithShadow(client.textRenderer, "FACING  " + client.player.getHorizontalFacing().asString().toUpperCase(java.util.Locale.ROOT), x, y, MUTED);
        }
        if (ImportantConfig.enabled("Watermark")) {
            String label = "IMPORTANT";
            int w = client.textRenderer.getWidth(label);
            int right = client.getWindow().getScaledWidth() - w - 12;
            context.fill(right - 7, 8, right + w + 7, 25, PANEL);
            context.fill(right - 7, 8, right - 4, 25, ACCENT);
            context.drawTextWithShadow(client.textRenderer, label, right, 13, TEXT);
        }
    }

    public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }
}
