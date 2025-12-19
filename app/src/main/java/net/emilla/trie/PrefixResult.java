package net.emilla.trie;

import androidx.annotation.Nullable;

import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.CommandYielder;
import net.emilla.command.DuplicateCommand;
import net.emilla.command.EmillaCommand;

public final class PrefixResult<V, L> {

    @Nullable
    private final V[] mValues;
    @Nullable
    public final L leftovers;
    private final int mLeftoverPosition;

    @internal PrefixResult(@Nullable V[] values, @Nullable L leftovers, int leftoverPosition) {
        mValues = values;
        this.leftovers = leftovers;
        mLeftoverPosition = leftoverPosition;
    }

    @Nullable
    public V value() {
        if (mValues == null) {
            return null;
        }

        if (mValues.length == 1) {
            return mValues[0];
        }

        return null;
    }

    @Nullable
    public static EmillaCommand toEmillaCommand(
        AssistActivity act,
        PrefixResult<CommandYielder, String> result
    ) {
        act.setInstructionPosition(result.mLeftoverPosition);
        // Todo: this is quite brittle

        CommandYielder[] yielders = result.mValues;
        if (yielders == null) {
            return null;
        }

        String instruction = result.leftovers;
        if (yielders.length == 1) {
            return yielders[0].command(act, instruction);
        }

        return new DuplicateCommand(act, yielders, instruction);
    }

}
