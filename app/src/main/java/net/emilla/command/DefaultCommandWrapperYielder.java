package net.emilla.command;

import net.emilla.activity.AssistActivity;
import net.emilla.command.core.CoreEntry;

public final class DefaultCommandWrapperYielder extends CommandYielder {

    private final CommandYielder mYielder;

    public DefaultCommandWrapperYielder(CoreEntry coreEntry) {
        mYielder = coreEntry.yielder();
    }

    @Override
    public boolean isPrefixable() {
        return mYielder.isPrefixable();
    }

    @Override
    protected EmillaCommand makeCommand(AssistActivity act) {
        return new DefaultCommandWrapper(act, mYielder.command(act));
    }

}
