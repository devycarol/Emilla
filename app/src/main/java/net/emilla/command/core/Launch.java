package net.emilla.command.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;

import net.emilla.annotation.internal;
import net.emilla.command.app.AppEntry;
import net.emilla.util.Intents;

final class Launch extends OpenCommand {

    @internal Launch(Context ctx) {
        super(ctx, CoreEntry.LAUNCH, EditorInfo.IME_ACTION_GO);
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
