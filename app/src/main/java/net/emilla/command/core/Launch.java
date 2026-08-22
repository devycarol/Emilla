package net.emilla.command.core;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;
import net.emilla.command.app.AppEntry;
import net.emilla.util.Intents;
import net.emilla.wadget.ActionSurface;

final class Launch extends OpenCommand {
    @internal Launch(ActionSurface surface) {
        super(surface, CoreEntry.LAUNCH, EditorInfo.IME_ACTION_GO);
    }

    @Override @Nullable
    protected Intent defaultIntent() {
        return null;
    }

    @Override
    public Intent makeIntent(AppEntry app, PackageManager pm) {
        return Intents.launchApp(app);
    }
}
