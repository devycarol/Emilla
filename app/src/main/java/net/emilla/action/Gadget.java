package net.emilla.action;

import androidx.annotation.Nullable;

import net.emilla.wadget.ActionSurface;

public interface Gadget {
    void instruct(ActionSurface surface, @Nullable String instruction);
}
