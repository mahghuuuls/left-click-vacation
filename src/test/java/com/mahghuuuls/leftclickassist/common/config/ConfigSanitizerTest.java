package com.mahghuuuls.leftclickassist.common.config;

import com.mahghuuuls.leftclickassist.common.ConfigValues;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigSanitizerTest {

    @Test
    void preservesMessageToggles() {
        ConfigValues values = sanitize(false, true, false, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS,
                false, ConfigSanitizer.DEFAULT_HUD_X, ConfigSanitizer.DEFAULT_HUD_Y,
                ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT, ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(false, values.showEnabledMessage());
        assertEquals(true, values.showDisabledMessage());
        assertEquals(false, values.showPausedMessage());
    }

    @Test
    void preservesDebugMessageToggle() {
        ConfigValues values = sanitize(true, true, true, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS,
                false, ConfigSanitizer.DEFAULT_HUD_X, ConfigSanitizer.DEFAULT_HUD_Y,
                ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT, ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(false, values.showDebugMessages());
    }

    @Test
    void defaultsHudPositionToBottomRightClampValue() {
        assertEquals(ConfigSanitizer.MAX_HUD_POSITION, ConfigSanitizer.DEFAULT_HUD_X);
        assertEquals(ConfigSanitizer.MAX_HUD_POSITION, ConfigSanitizer.DEFAULT_HUD_Y);
    }

    @Test
    void clampsGracePeriodBelowMinimum() {
        ConfigValues values = sanitizeWithGracePeriod(0);

        assertEquals(ConfigSanitizer.MIN_GRACE_PERIOD_SECONDS, values.gracePeriodSeconds());
    }

    @Test
    void clampsGracePeriodAboveMaximum() {
        ConfigValues values = sanitizeWithGracePeriod(301);

        assertEquals(ConfigSanitizer.MAX_GRACE_PERIOD_SECONDS, values.gracePeriodSeconds());
    }

    @Test
    void keepsValidGracePeriod() {
        ConfigValues values = sanitizeWithGracePeriod(20);

        assertEquals(20, values.gracePeriodSeconds());
    }

    @Test
    void clampsFixedHudDurationBelowMinimum() {
        ConfigValues values = sanitizeWithFixedHudDuration(0);

        assertEquals(ConfigSanitizer.MIN_FIXED_HUD_DURATION_SECONDS, values.fixedHudDurationSeconds());
    }

    @Test
    void clampsFixedHudDurationAboveMaximum() {
        ConfigValues values = sanitizeWithFixedHudDuration(11);

        assertEquals(ConfigSanitizer.MAX_FIXED_HUD_DURATION_SECONDS, values.fixedHudDurationSeconds());
    }

    @Test
    void keepsValidFixedHudDuration() {
        ConfigValues values = sanitizeWithFixedHudDuration(3);

        assertEquals(3, values.fixedHudDurationSeconds());
    }

    @Test
    void clampsHudPositionBelowMinimum() {
        ConfigValues values = sanitize(true, true, true, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS,
                false, -1, -2, ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(ConfigSanitizer.MIN_HUD_POSITION, values.hudX());
        assertEquals(ConfigSanitizer.MIN_HUD_POSITION, values.hudY());
    }

    @Test
    void clampsHudPositionAboveMaximum() {
        ConfigValues values = sanitize(true, true, true, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS,
                false, 10001, 10002, ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(ConfigSanitizer.MAX_HUD_POSITION, values.hudX());
        assertEquals(ConfigSanitizer.MAX_HUD_POSITION, values.hudY());
    }

    @Test
    void keepsValidHudPosition() {
        ConfigValues values = sanitize(true, true, true, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS,
                false, 12, 34, ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(12, values.hudX());
        assertEquals(34, values.hudY());
    }

    @Test
    void clampsHudScaleBelowMinimum() {
        ConfigValues values = sanitizeWithHudScale(49);

        assertEquals(ConfigSanitizer.MIN_HUD_SCALE_PERCENT, values.hudScalePercent());
    }

    @Test
    void clampsHudScaleAboveMaximum() {
        ConfigValues values = sanitizeWithHudScale(301);

        assertEquals(ConfigSanitizer.MAX_HUD_SCALE_PERCENT, values.hudScalePercent());
    }

    @Test
    void keepsValidHudScale() {
        ConfigValues values = sanitizeWithHudScale(125);

        assertEquals(125, values.hudScalePercent());
    }

    private ConfigValues sanitizeWithGracePeriod(int gracePeriodSeconds) {
        return sanitize(true, true, true, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS, false,
                ConfigSanitizer.DEFAULT_HUD_X, ConfigSanitizer.DEFAULT_HUD_Y,
                ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT, gracePeriodSeconds);
    }

    private ConfigValues sanitizeWithFixedHudDuration(int fixedHudDurationSeconds) {
        return sanitize(true, true, true, fixedHudDurationSeconds, false, ConfigSanitizer.DEFAULT_HUD_X,
                ConfigSanitizer.DEFAULT_HUD_Y, ConfigSanitizer.DEFAULT_HUD_SCALE_PERCENT,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);
    }

    private ConfigValues sanitizeWithHudScale(int hudScalePercent) {
        return sanitize(true, true, true, ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS, false,
                ConfigSanitizer.DEFAULT_HUD_X, ConfigSanitizer.DEFAULT_HUD_Y, hudScalePercent,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);
    }

    private ConfigValues sanitize(boolean showEnabledMessage, boolean showDisabledMessage,
            boolean showPausedMessage, int fixedHudDurationSeconds, boolean showDebugMessages, int hudX, int hudY,
            int hudScalePercent, int gracePeriodSeconds) {
        return ConfigSanitizer.sanitize(showEnabledMessage, showDisabledMessage, showPausedMessage,
                fixedHudDurationSeconds, showDebugMessages, hudX, hudY, hudScalePercent, gracePeriodSeconds);
    }
}
