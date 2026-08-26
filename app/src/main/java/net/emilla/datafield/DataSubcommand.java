package net.emilla.datafield;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.command.ImeAction;
import net.emilla.wadget.ActionSurface;

import java.util.function.Function;

public record DataSubcommand<A>(
    Function<String, A> generator,
    @DrawableRes int icon,
    @Override ImeAction imeAction,
    @StringRes int hint
) implements Subcommand<A> {
    @Override
    public A action(ActionSurface surface) {
        return generator.apply(surface.dataText());
    }
}
