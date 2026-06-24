package com.mahghuuuls.leftclickvacation.common.config;

import com.mahghuuuls.leftclickvacation.Tags;
import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

@Config(modid = Tags.MOD_ID, name = Tags.MOD_ID)
public final class ModConfig {

    @Config.Comment("HUD feedback settings.")
    public static Hud hud = new Hud();

    private ModConfig() {
    }

    public static void load() {
        ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
    }

    public static ConfigValues values() {
        return ConfigSanitizer.sanitize(
                hud.showEnabledMessage,
                hud.showDisabledMessage,
                hud.showPausedMessage,
                hud.fixedMessageDurationSeconds);
    }

    public static final class Hud {

        @Config.Comment("Show a HUD message when automation is enabled.")
        public boolean showEnabledMessage = true;

        @Config.Comment("Show a HUD message when automation is disabled.")
        public boolean showDisabledMessage = true;

        @Config.Comment("Show a HUD message while automation is paused.")
        public boolean showPausedMessage = true;

        @Config.Comment("Duration in seconds for fixed enabled and disabled HUD messages.")
        @Config.RangeInt(
                min = ConfigSanitizer.MIN_FIXED_HUD_DURATION_SECONDS,
                max = ConfigSanitizer.MAX_FIXED_HUD_DURATION_SECONDS)
        public int fixedMessageDurationSeconds = ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS;
    }
}
