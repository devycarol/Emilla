package net.emilla.command.core;

import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;

@FunctionalInterface
interface CoreMaker {
    EmillaCommand make(AssistActivity act);
}
