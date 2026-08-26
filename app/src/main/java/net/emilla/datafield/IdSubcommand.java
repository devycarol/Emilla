package net.emilla.datafield;

import androidx.annotation.DrawableRes;

import net.emilla.command.ImeAction;
import net.emilla.wadget.ActionSurface;

public record IdSubcommand<A>(
    A id,
    @DrawableRes int icon,
    @Override ImeAction imeAction
) implements Subcommand<A> {
    @Override
    public A action(ActionSurface surface) {
        return id;
    }
}
