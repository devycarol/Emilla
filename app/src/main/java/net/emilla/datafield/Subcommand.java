package net.emilla.datafield;

import net.emilla.command.ImeAction;
import net.emilla.wadget.ActionSurface;

public sealed interface Subcommand<A> permits IdSubcommand, DataSubcommand {
    ImeAction imeAction();
    A action(ActionSurface surface);
}
