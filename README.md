# Important — Visual Client

A client-side Fabric mod project for **Minecraft Java 1.21.11**. The project keeps the mod ID and display name `important` / `Important` and uses Java 21.

## Included in this starter update

- Minimal dark ClickGUI with blue accent, categories, module toggles and scrolling.
- Right Shift opens the menu by default.
- Persistent configuration at `.minecraft/config/important.json`.
- Working visual HUD options: watermark, FPS, coordinates and facing direction.
- Category structure for HUD, Particles, Render and Elements, ready for incremental implementation.
- GitHub Actions workflow that builds the project and uploads the JAR as an artifact.

## Build on GitHub

1. Create a new GitHub repository and upload the **contents** of this folder.
2. Open **Actions** and allow workflows if GitHub asks.
3. Run **Build Important** (or push a commit to trigger it automatically).
4. Open the completed workflow run, download the `Important-JAR` artifact and extract the `.jar` inside.
5. Put the JAR in your Minecraft `mods` folder alongside Fabric API for Minecraft 1.21.11.

The workflow uses Java 21 and Gradle 9.2.1. No Gradle wrapper binary is included; GitHub Actions installs Gradle for the build.

## Current implementation status

The GUI, config saving, category filtering, accent cycling, watermark, FPS, coordinates and facing indicator are implemented in this starter project. The other module entries are settings placeholders and do not change gameplay; their render effects still need to be implemented in subsequent updates. This is intentionally a visual-only project and does not include combat automation or gameplay advantages.

## Keybind

Default: **Right Shift**. You can change it in Minecraft's Controls screen under the Important category.
