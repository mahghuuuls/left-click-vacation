# Changelog

## 0.1.0

### Added

- Initial Forge 1.12.2 release.
- Configurable `Toggle Auto Mine` keybind, unbound by default.
- Controlled automatic block breaking for valid block targets.
- Activation requires a non-empty main-hand item.
- Survival and Adventure mode support.
- Practical activation-item tracking that tolerates normal durability loss.
- Pause and resume behavior when switching away from the activation item.
- Configurable grace period, defaulting to 20 seconds.
- Rebinding behavior when pressing the toggle key while holding a different valid item.
- Entity-target safety so automation does not generate entity attacks.
- Configurable HUD messages for enabled, paused, and disabled states.
- Config sanitization for HUD duration and grace-period values.
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
