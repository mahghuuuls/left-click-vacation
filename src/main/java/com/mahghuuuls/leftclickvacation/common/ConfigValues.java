package com.mahghuuuls.leftclickvacation.common;

public final class ConfigValues {

    private final boolean showEnabledMessage;
    private final boolean showDisabledMessage;
    private final boolean showPausedMessage;
    private final int fixedHudDurationSeconds;

    public ConfigValues(boolean showEnabledMessage, boolean showDisabledMessage, boolean showPausedMessage,
            int fixedHudDurationSeconds) {
        this.showEnabledMessage = showEnabledMessage;
        this.showDisabledMessage = showDisabledMessage;
        this.showPausedMessage = showPausedMessage;
        this.fixedHudDurationSeconds = fixedHudDurationSeconds;
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
}
