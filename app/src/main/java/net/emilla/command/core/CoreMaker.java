package net.emilla.command.core;

import net.emilla.command.EmillaCommand;
import net.emilla.wadget.ActionSurface;

@FunctionalInterface
interface CoreMaker {
    EmillaCommand make(ActionSurface surface);
}
