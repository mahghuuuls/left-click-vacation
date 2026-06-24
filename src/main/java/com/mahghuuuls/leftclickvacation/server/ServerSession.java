package com.mahghuuuls.leftclickvacation.server;

import com.mahghuuuls.leftclickvacation.common.ActivationBinding;
import com.mahghuuuls.leftclickvacation.common.AutomationState;

public final class ServerSession {

    private final ActivationBinding binding;
    private AutomationState state;

    public ServerSession(ActivationBinding binding) {
        this.binding = binding;
        this.state = AutomationState.ENABLED;
    }

    public ActivationBinding binding() {
        return binding;
    }

    public AutomationState state() {
        return state;
    }

    public void disable() {
        state = AutomationState.DISABLED;
    }
}
