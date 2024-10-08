package net.emilla.commands;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.utils.Apps;
import net.emilla.utils.Lang;

public class AppCommand extends EmillaCommand {
@NonNull
private static String genericTitle(final Context ctxt, final CharSequence appLabel) {
    return Lang.colonConcat(ctxt.getResources(), R.string.command_app, appLabel);
}

@NonNull
protected static String specificTitle(final Context ctxt, final CharSequence appLabel,
        @StringRes final int instructionId) {
    return Lang.colonConcat(ctxt.getResources(), appLabel, instructionId);
}

protected final Intent mLaunchIntent;
private final CharSequence mTitle;
protected final AppCmdInfo mInfo; // TODO: replace with icons

protected AppCommand(final AssistActivity act, final AppCmdInfo info, final CharSequence cmdTitle) {
    super(act);

    mLaunchIntent = Apps.launchIntent(info.pkg, info.cls);
    mTitle = cmdTitle;
    mInfo = info;
}

public AppCommand(final AssistActivity act, final AppCmdInfo info) {
    this(act, info, genericTitle(act, info.label));
}

@Override
protected CharSequence name() {
    return mInfo.label;
}

@Override
protected CharSequence dupeLabel() {
    return mInfo.label + " (" + mInfo.pkg + ")";
}

@Override
public CharSequence lcName() {
    return mInfo.label; // Apps are proper names and shouldn't be lowercased
}

@Override
public CharSequence title() {
    return mTitle;
}

@Override @DrawableRes
public int icon() {
    return R.drawable.ic_app;
}

@Override
public int imeAction() {
    return EditorInfo.IME_ACTION_GO;
}

@Override
public void run() {
    succeed(mLaunchIntent);
}

@Override
public void run(final String ignored) {
    run(); // TODO: instead, this should revert to the default command
}
}
