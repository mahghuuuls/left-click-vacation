package com.mahghuuuls.leftclickvacation.common.config;

import com.mahghuuuls.leftclickvacation.LeftClickVacationMod;
import com.mahghuuuls.leftclickvacation.common.ConfigValues;

public final class ConfigSanitizer {

    public static final int DEFAULT_FIXED_HUD_DURATION_SECONDS = 3;
    public static final int MIN_FIXED_HUD_DURATION_SECONDS = 1;
    public static final int MAX_FIXED_HUD_DURATION_SECONDS = 10;
    public static final int DEFAULT_GRACE_PERIOD_SECONDS = 20;
    public static final int MIN_GRACE_PERIOD_SECONDS = 1;
    public static final int MAX_GRACE_PERIOD_SECONDS = 300;

    private ConfigSanitizer() {
    }

    public static ConfigValues sanitize(boolean showEnabledMessage, boolean showDisabledMessage,
            boolean showPausedMessage, int fixedHudDurationSeconds, int gracePeriodSeconds) {
        int sanitizedDuration = clamp(fixedHudDurationSeconds, MIN_FIXED_HUD_DURATION_SECONDS,
                MAX_FIXED_HUD_DURATION_SECONDS);
        if (sanitizedDuration != fixedHudDurationSeconds) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid HUD message duration {}. Using clamped value {}.",
                    fixedHudDurationSeconds,
                    sanitizedDuration);
        }
        int sanitizedGracePeriod = clamp(gracePeriodSeconds, MIN_GRACE_PERIOD_SECONDS, MAX_GRACE_PERIOD_SECONDS);
        if (sanitizedGracePeriod != gracePeriodSeconds) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid grace period {}. Using clamped value {}.",
                    gracePeriodSeconds,
                    sanitizedGracePeriod);
        }
        return new ConfigValues(showEnabledMessage, showDisabledMessage, showPausedMessage, sanitizedDuration,
                sanitizedGracePeriod);
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
