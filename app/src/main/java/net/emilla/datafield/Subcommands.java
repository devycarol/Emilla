package net.emilla.datafield;

import androidx.annotation.StringRes;

import net.emilla.annotation.internal;
import net.emilla.command.ImeAction;
import net.emilla.wadget.ActionSurface;

public final class Subcommands<A> implements DataDirective {
    private final Subcommand<A>[] mArray;
    private int mChoice = 0;

    @SafeVarargs
    public Subcommands(Subcommand<A>... array) {
        mArray = array;
    }

    @internal void choose(int choice) {
        mChoice = choice;
    }

    public A get(ActionSurface surface) {
        return mArray[mChoice].action(surface);
    }

    public ImeAction imeAction() {
        return mArray[mChoice].imeAction();
    }

    @StringRes
    public int dataHint() {
        return switch (mArray[mChoice]) {
            case IdSubcommand<A> __ -> 0;
            case DataSubcommand<A> data -> data.hint();
        };
    }
}
