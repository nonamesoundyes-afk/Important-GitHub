package dev.important;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ImportantScreen extends Screen {
    private static final String[] CATEGORIES = {"All", "HUD", "Particles", "Render", "Elements"};
    private String category = "All";
    private int scroll = 0;
    private final List<ButtonWidget> moduleButtons = new ArrayList<>();
    private int panelX, panelY, panelW, panelH;

    public ImportantScreen() { super(Text.literal("Important")); }

    @Override
    protected void init() { rebuildButtons(); }

    private void rebuildButtons() {
        clearChildren(); moduleButtons.clear();
        panelW = Math.min(560, width - 28); panelH = Math.min(360, height - 28);
        panelX = (width - panelW) / 2; panelY = (height - panelH) / 2;
        int catY = panelY + 55;
        int catW = Math.max(55, (panelW - 34) / CATEGORIES.length);
        for (int i = 0; i < CATEGORIES.length; i++) {
            String cat = CATEGORIES[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(cat), b -> { category = cat; scroll = 0; rebuildButtons(); })
                    .dimensions(panelX + 16 + i * catW, catY, catW - 4, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Accent: " + ImportantConfig.accent()), b -> rebuildAccent())
                .dimensions(panelX + panelW - 126, panelY + panelH - 31, 110, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> { ImportantConfig.reset(); rebuildButtons(); })
                .dimensions(panelX + 16, panelY + panelH - 31, 55, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(panelX + 77, panelY + panelH - 31, 55, 20).build());

        Map<String, Boolean> all = ImportantConfig.all();
        List<String> visible = all.keySet().stream().filter(this::matches).toList();
        int startY = panelY + 88;
        int colW = (panelW - 44) / 2;
        int visibleRows = Math.max(1, (panelH - 132) / 27);
        int maxScroll = Math.max(0, (int)Math.ceil(visible.size() / 2.0) - visibleRows);
        scroll = Math.min(scroll, maxScroll);
        for (int i = scroll * 2; i < visible.size() && i < (scroll + visibleRows) * 2; i++) {
            String name = visible.get(i);
            int row = (i / 2) - scroll, col = i % 2;
            int x = panelX + 16 + col * (colW + 12), y = startY + row * 27;
            ButtonWidget button = ButtonWidget.builder(moduleText(name), b -> { ImportantConfig.toggle(name); b.setMessage(moduleText(name)); })
                    .dimensions(x, y, colW, 22).build();
            moduleButtons.add(button); addDrawableChild(button);
        }
    }

    private void rebuildAccent() { ImportantConfig.cycleAccent(); rebuildButtons(); }

    private Text moduleText(String name) {
        return Text.literal((ImportantConfig.enabled(name) ? "●  " : "○  ") + name);
    }

    private boolean matches(String name) {
        if (category.equals("All")) return true;
        return switch (category) {
            case "HUD" -> List.of("Watermark", "FPS", "Coordinates", "Direction", "Keystrokes", "CPS Counter", "Armor HUD", "Potion HUD", "Target HUD").contains(name);
            case "Particles" -> List.of("Hit Particles", "Particle Trail", "Enchantment Glow").contains(name);
            case "Render" -> List.of("Smooth Zoom", "No Hurt Cam", "Block Overlay", "Custom Sky", "Low Fire").contains(name);
            case "Elements" -> List.of("Clean Chat", "Custom Crosshair", "Minimal Scoreboard").contains(name);
            default -> false;
        };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x99080B10);
        context.fill(panelX, panelY, panelX + panelW, panelY + panelH, ImportantClient.PANEL);
        context.fill(panelX, panelY, panelX + panelW, panelY + 2, colorAccent());
        context.fill(panelX, panelY, panelX + 1, panelY + panelH, 0xFF303843);
        context.fill(panelX + panelW - 1, panelY, panelX + panelW, panelY + panelH, 0xFF303843);
        context.fill(panelX, panelY + panelH - 1, panelX + panelW, panelY + panelH, 0xFF303843);
        context.drawTextWithShadow(textRenderer, "IMPORTANT", panelX + 16, panelY + 13, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, "VISUAL CLIENT  /  1.21.11", panelX + 16, panelY + 29, ImportantClient.MUTED);
        context.drawTextWithShadow(textRenderer, "RIGHT SHIFT  •  SETTINGS", panelX + panelW - 155, panelY + 17, ImportantClient.MUTED);
        context.drawTextWithShadow(textRenderer, "Modules", panelX + 16, panelY + 79, ImportantClient.TEXT);
        context.drawTextWithShadow(textRenderer, "20 VISUAL OPTIONS  ·  CONFIG SAVED AUTOMATICALLY", panelX + 16, panelY + panelH - 47, ImportantClient.MUTED);
        super.render(context, mouseX, mouseY, delta);
    }

    private int colorAccent() {
        return switch (ImportantConfig.accent()) { case "#B18CFF" -> 0xFFB18CFF; case "#70E0A0" -> 0xFF70E0A0; default -> ImportantClient.ACCENT; };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, scroll - (int)Math.signum(verticalAmount));
        rebuildButtons();
        return true;
    }

    @Override
    public boolean shouldPause() { return false; }
}
