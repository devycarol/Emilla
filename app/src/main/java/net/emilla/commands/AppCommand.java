package net.emilla.commands;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
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
protected final CharSequence mLabel;
private final CharSequence mTitle;
private final String mPackage; // TODO: replace with icons

protected AppCommand(final AssistActivity act, final CharSequence appLabel,
        final CharSequence cmdTitle, final Intent launch) {
    super(act);

    mLaunchIntent = launch;
    mLabel = appLabel;
    mTitle = cmdTitle;
    mPackage = launch.getPackage();
}

public AppCommand(final AssistActivity act, final CharSequence appLabel, final Intent launch) {
    this(act, appLabel, genericTitle(act, appLabel), launch);
}

@Override
public Command cmd() {
    return Command.APP;
}

@Override
protected CharSequence name() {
    return mLabel;
}

@Override
protected CharSequence dupeLabel() {
    return mLabel + " (" + mPackage + ")";
}

@Override
public CharSequence lcName() {
    return mLabel; // Apps are proper names and shouldn't be lowercased
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
