package net.emilla.command.core;

import net.emilla.activity.AssistActivity;

@FunctionalInterface
interface CoreMaker {

    CoreCommand make(AssistActivity act);

}
