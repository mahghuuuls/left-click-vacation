package com.mahghuuuls.leftclickvacation.common.config;

import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigSanitizerTest {

    @Test
    void preservesMessageToggles() {
        ConfigValues values = ConfigSanitizer.sanitize(false, true, false,
                ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(false, values.showEnabledMessage());
        assertEquals(true, values.showDisabledMessage());
        assertEquals(false, values.showPausedMessage());
    }

    @Test
    void clampsGracePeriodBelowMinimum() {
        ConfigValues values = ConfigSanitizer.sanitize(true, true, true,
                ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS, 0);

        assertEquals(ConfigSanitizer.MIN_GRACE_PERIOD_SECONDS, values.gracePeriodSeconds());
    }

    @Test
    void clampsGracePeriodAboveMaximum() {
        ConfigValues values = ConfigSanitizer.sanitize(true, true, true,
                ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS, 301);

        assertEquals(ConfigSanitizer.MAX_GRACE_PERIOD_SECONDS, values.gracePeriodSeconds());
    }

    @Test
    void keepsValidGracePeriod() {
        ConfigValues values = ConfigSanitizer.sanitize(true, true, true,
                ConfigSanitizer.DEFAULT_FIXED_HUD_DURATION_SECONDS, 20);

        assertEquals(20, values.gracePeriodSeconds());
    }

    @Test
    void clampsFixedHudDurationBelowMinimum() {
        ConfigValues values = ConfigSanitizer.sanitize(true, true, true, 0,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(ConfigSanitizer.MIN_FIXED_HUD_DURATION_SECONDS, values.fixedHudDurationSeconds());
    }

    @Test
    void clampsFixedHudDurationAboveMaximum() {
        ConfigValues values = ConfigSanitizer.sanitize(true, true, true, 11,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(ConfigSanitizer.MAX_FIXED_HUD_DURATION_SECONDS, values.fixedHudDurationSeconds());
    }

    @Test
    void keepsValidFixedHudDuration() {
        ConfigValues values = ConfigSanitizer.sanitize(true, true, true, 3,
                ConfigSanitizer.DEFAULT_GRACE_PERIOD_SECONDS);

        assertEquals(3, values.fixedHudDurationSeconds());
    }
}
