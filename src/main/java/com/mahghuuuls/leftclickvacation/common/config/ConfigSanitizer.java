package com.mahghuuuls.leftclickvacation.common.config;

import com.mahghuuuls.leftclickvacation.LeftClickVacationMod;
import com.mahghuuuls.leftclickvacation.common.ConfigValues;

public final class ConfigSanitizer {

    public static final int DEFAULT_FIXED_HUD_DURATION_SECONDS = 3;
    public static final int MIN_FIXED_HUD_DURATION_SECONDS = 1;
    public static final int MAX_FIXED_HUD_DURATION_SECONDS = 10;

    private ConfigSanitizer() {
    }

    public static ConfigValues sanitize(boolean showEnabledMessage, boolean showDisabledMessage,
            boolean showPausedMessage, int fixedHudDurationSeconds) {
        int sanitizedDuration = clamp(fixedHudDurationSeconds, MIN_FIXED_HUD_DURATION_SECONDS,
                MAX_FIXED_HUD_DURATION_SECONDS);
        if (sanitizedDuration != fixedHudDurationSeconds) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid HUD message duration {}. Using clamped value {}.",
                    fixedHudDurationSeconds,
                    sanitizedDuration);
        }
        return new ConfigValues(showEnabledMessage, showDisabledMessage, showPausedMessage, sanitizedDuration);
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
