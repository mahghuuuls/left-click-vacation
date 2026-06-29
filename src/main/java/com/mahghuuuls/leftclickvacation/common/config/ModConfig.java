package com.mahghuuuls.leftclickvacation.common.config;

import com.mahghuuuls.leftclickvacation.Tags;
import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

@Config(modid = Tags.MOD_ID, name = Tags.MOD_ID)
public final class ModConfig {

    @Config.Comment("HUD feedback settings.")
    public static Hud hud = new Hud();

    @Config.Comment("Automation behavior settings.")
    public static Automation automation = new Automation();

    private static volatile ConfigValues cachedValues;

    private ModConfig() {
    }

    public static void load() {
        ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
        refreshValues();
    }

    public static ConfigValues values() {
        ConfigValues values = cachedValues;
        if (values == null) {
            return refreshValues();
        }
        return values;
    }

    private static ConfigValues refreshValues() {
        ConfigValues values = ConfigSanitizer.sanitize(
                hud.showEnabledMessage,
                hud.showDisabledMessage,
                hud.showPausedMessage,
                hud.fixedMessageDurationSeconds,
                hud.showDebugMessages,
                hud.componentX,
                hud.componentY,
                hud.componentScalePercent,
                automation.gracePeriodSeconds);
        cachedValues = values;
        return values;
    }

    public static final class Hud {

        @Config.Comment("Show debug HUD text messages. The primary HUD component remains visible while the mod is enabled.")
        public boolean showDebugMessages = false;

        @Config.Comment("Show a debug HUD message when the mod or auto click is enabled. Requires debug HUD messages to be enabled.")
        public boolean showEnabledMessage = true;

        @Config.Comment("Show a debug HUD message when the mod is disabled or auto click is turned off. Requires debug HUD messages to be enabled.")
        public boolean showDisabledMessage = true;

        @Config.Comment("Show a debug HUD message while auto click is paused. Requires debug HUD messages to be enabled.")
        public boolean showPausedMessage = true;

        @Config.Comment("Duration in seconds for fixed enabled and disabled debug HUD messages.")
        @Config.RangeInt(
                min = ConfigSanitizer.MIN_FIXED_HUD_DURATION_SECONDS,
                max = ConfigSanitizer.MAX_FIXED_HUD_DURATION_SECONDS)
        public int fixedMessageDurationSeconds = ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS;

        @Config.Comment("Primary HUD component X position in scaled screen pixels.")
        @Config.RangeInt(
                min = ConfigSanitizer.MIN_HUD_POSITION,
                max = ConfigSanitizer.MAX_HUD_POSITION)
        public int componentX = ConfigSanitizer.DEFAULT_HUD_X;

        @Config.Comment("Primary HUD component Y position in scaled screen pixels.")
        @Config.RangeInt(
                min = ConfigSanitizer.MIN_HUD_POSITION,
                max = ConfigSanitizer.MAX_HUD_POSITION)
        public int componentY = ConfigSanitizer.DEFAULT_HUD_Y;

        @Config.Comment("Primary HUD component scale percentage.")
        @Config.RangeInt(
                min = ConfigSanitizer.MIN_HUD_SCALE_PERCENT,
                max = ConfigSanitizer.MAX_HUD_SCALE_PERCENT)
        public int componentScalePercent = ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT;
    }

    public static final class Automation {

        @Config.Comment("Seconds auto click may remain paused after switching away from the activation item.")
        @Config.RangeInt(
                min = ConfigSanitizer.MIN_GRACE_PERIOD_SECONDS,
                max = ConfigSanitizer.MAX_GRACE_PERIOD_SECONDS)
        public int gracePeriodSeconds = ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS;
    }
}
