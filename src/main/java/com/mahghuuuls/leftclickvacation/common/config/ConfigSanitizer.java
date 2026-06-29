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
    public static final int MIN_HUD_POSITION = 0;
    public static final int MAX_HUD_POSITION = 10000;
    public static final int DEFAULT_HUD_X = MAX_HUD_POSITION;
    public static final int DEFAULT_HUD_Y = MAX_HUD_POSITION;
    public static final int DEFAULT_HUD_SCALE_PERCENT = 100;
    public static final int MIN_HUD_SCALE_PERCENT = 50;
    public static final int MAX_HUD_SCALE_PERCENT = 300;

    private ConfigSanitizer() {
    }

    public static ConfigValues sanitize(boolean showEnabledMessage, boolean showDisabledMessage,
            boolean showPausedMessage, int fixedHudDurationSeconds, boolean showDebugMessages, int hudX, int hudY,
            int hudScalePercent, int gracePeriodSeconds) {
        int sanitizedDuration = clamp(fixedHudDurationSeconds, MIN_FIXED_HUD_DURATION_SECONDS,
                MAX_FIXED_HUD_DURATION_SECONDS);
        if (sanitizedDuration != fixedHudDurationSeconds) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid HUD message duration {}. Using clamped value {}.",
                    fixedHudDurationSeconds,
                    sanitizedDuration);
        }
        int sanitizedHudX = clamp(hudX, MIN_HUD_POSITION, MAX_HUD_POSITION);
        if (sanitizedHudX != hudX) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid HUD component X position {}. Using clamped value {}.",
                    hudX,
                    sanitizedHudX);
        }
        int sanitizedHudY = clamp(hudY, MIN_HUD_POSITION, MAX_HUD_POSITION);
        if (sanitizedHudY != hudY) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid HUD component Y position {}. Using clamped value {}.",
                    hudY,
                    sanitizedHudY);
        }
        int sanitizedHudScalePercent = clamp(hudScalePercent, MIN_HUD_SCALE_PERCENT, MAX_HUD_SCALE_PERCENT);
        if (sanitizedHudScalePercent != hudScalePercent) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid HUD component scale {}. Using clamped value {}.",
                    hudScalePercent,
                    sanitizedHudScalePercent);
        }
        int sanitizedGracePeriod = clamp(gracePeriodSeconds, MIN_GRACE_PERIOD_SECONDS, MAX_GRACE_PERIOD_SECONDS);
        if (sanitizedGracePeriod != gracePeriodSeconds) {
            LeftClickVacationMod.LOGGER.warn(
                    "Invalid grace period {}. Using clamped value {}.",
                    gracePeriodSeconds,
                    sanitizedGracePeriod);
        }
        return new ConfigValues(showEnabledMessage, showDisabledMessage, showPausedMessage, sanitizedDuration,
                showDebugMessages, sanitizedHudX, sanitizedHudY, sanitizedHudScalePercent, sanitizedGracePeriod);
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
