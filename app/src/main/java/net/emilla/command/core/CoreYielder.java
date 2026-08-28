package net.emilla.command.core;

import net.emilla.annotation.internal;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.wadget.ActionSurface;

final class CoreYielder extends CommandYielder {
    private final CoreEntry mCoreEntry;

    @internal CoreYielder(CoreEntry coreEntry) {
        mCoreEntry = coreEntry;
    }

    @Override
    public boolean usesInstruction() {
        return mCoreEntry.usesInstruction;
    }

    @Override
    protected EmillaCommand makeCommand(ActionSurface surface) {
        return mCoreEntry.mMaker.make(surface);
    }
}
