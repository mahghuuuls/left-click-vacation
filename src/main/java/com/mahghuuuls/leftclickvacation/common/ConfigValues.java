package com.mahghuuuls.leftclickvacation.common;

public final class ConfigValues {

    private final boolean showEnabledMessage;
    private final boolean showDisabledMessage;
    private final boolean showPausedMessage;
    private final int fixedHudDurationSeconds;
    private final boolean showDebugMessages;
    private final int hudX;
    private final int hudY;
    private final int hudScalePercent;
    private final int gracePeriodSeconds;

    public ConfigValues(boolean showEnabledMessage, boolean showDisabledMessage, boolean showPausedMessage,
            int fixedHudDurationSeconds, boolean showDebugMessages, int hudX, int hudY, int hudScalePercent,
            int gracePeriodSeconds) {
        this.showEnabledMessage = showEnabledMessage;
        this.showDisabledMessage = showDisabledMessage;
        this.showPausedMessage = showPausedMessage;
        this.fixedHudDurationSeconds = fixedHudDurationSeconds;
        this.showDebugMessages = showDebugMessages;
        this.hudX = hudX;
        this.hudY = hudY;
        this.hudScalePercent = hudScalePercent;
        this.gracePeriodSeconds = gracePeriodSeconds;
    }

    public boolean showEnabledMessage() {
        return showEnabledMessage;
    }

    public boolean showDisabledMessage() {
        return showDisabledMessage;
    }

    public boolean showPausedMessage() {
        return showPausedMessage;
    }

    public int fixedHudDurationSeconds() {
        return fixedHudDurationSeconds;
    }

    public boolean showDebugMessages() {
        return showDebugMessages;
    }

    public int hudX() {
        return hudX;
    }

    public int hudY() {
        return hudY;
    }

    public int hudScalePercent() {
        return hudScalePercent;
    }

    public int gracePeriodSeconds() {
        return gracePeriodSeconds;
    }
}
