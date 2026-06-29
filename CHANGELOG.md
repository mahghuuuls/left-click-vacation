# Changelog

## 0.1.0

### Added

- Initial Forge 1.12.2 release.
- Configurable `Toggle Auto Click Mode` keybind, unbound by default.
- Controlled automatic block breaking for valid block targets.
- Activation requires a non-empty main-hand item.
- Survival and Adventure mode support.
- Practical activation-item tracking that tolerates normal durability loss.
- Keybind arming for the held item, with normal left-click toggling auto click on or off.
- Auto click turns off when switching away from the activation item, while the mod remains armed for that item when possible.
- Manual restart after returning to the armed item.
- Entity-target safety so automation does not generate entity attacks.
- Primary HUD component showing the armed item icon plus green `ON` or red `OFF` status.
- Optional debug HUD messages, disabled by default.
- Configurable HUD component position and scale.
- Config sanitization for HUD position, scale, debug message duration, and grace-period compatibility values.
- Client-first multiplayer behavior that does not require server-side mod support.

### Compatibility

- Targets Minecraft 1.12.2 with Forge 1.12.2.
- Builds as Java 8-compatible bytecode.
- Does not use Mixins, access transformers, custom mining packets, or generic click simulation.

### Known Limitations

- Creative-mode automation is not supported.
- Dedicated-server runtime smoke testing was not completed in this workspace because the Minecraft EULA was not accepted here.
- Cleanroom runtime validation has not been completed in a standalone Cleanroom environment.
- Identical-looking replacement item stacks may be indistinguishable in client-first mode.
