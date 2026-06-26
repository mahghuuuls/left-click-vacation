package com.mahghuuuls.leftclickvacation.common.config;

import com.mahghuuuls.leftclickvacation.common.ConfigValues;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigSanitizerTest {

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
}
