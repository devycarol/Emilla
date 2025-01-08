package net.emilla.command.app;

import static net.emilla.utils.Apps.*;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.utils.Apps;

public class AppCommand extends EmillaCommand {

    @NonNull
    private static String genericTitle(Context ctxt, CharSequence appLabel) {
        return Lang.colonConcat(ctxt.getResources(), R.string.command_app, appLabel);
    }

    @NonNull
    protected static String specificTitle(Context ctxt, CharSequence appLabel,
            @StringRes int instructionId) {
        return Lang.colonConcat(ctxt.getResources(), appLabel, instructionId);
    }

    protected final Intent mLaunchIntent;
    private final CharSequence mTitle;
    protected final AppParams mParams;

    protected AppCommand(AssistActivity act, String instruct, AppParams params, CharSequence title) {
        super(act, instruct);

        mLaunchIntent = Apps.launchIntent(params.pkg, params.cls);
        mTitle = title;
        mParams = params;
    }

    public AppCommand(AssistActivity act, String instruct, AppParams params) {
        this(act, instruct, params, genericTitle(act, params.label));
    }

    @Override
    public CharSequence name() {
        return mParams.label;
    }

    @Override
    protected String dupeLabel() {
        return mParams.label + " (" + mParams.pkg + ")";
    }

    @Override
    public CharSequence sentenceName() {
        return mParams.label; // Apps are proper names and shouldn't be lowercased
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
    protected void run() {
        appSucceed(mLaunchIntent);
    }

    @Override
    protected void run(String ignored) {
        run(); // TODO: instead, this should revert to the default command
    }

    public static class AppParams {

        public final CharSequence label;
        public final String pkg;
        public final String cls;
        public final boolean has_send;
        public final boolean basic;

        public AppParams(ActivityInfo info, PackageManager pm, CharSequence appLabel) {
            label = appLabel;
            pkg = info.packageName;
            cls = info.name;
            has_send = sendToApp(pkg).resolveActivity(pm) != null;
            basic = switch (pkg) {
                case PKG_AOSP_CONTACTS, PKG_FIREFOX, PKG_YOUTUBE -> false;
                case PKG_TOR -> true; // Search/send intents are broken
                case PKG_MARKOR -> !cls.equals(Apps.CLS_MARKOR_MAIN);
                // Markor can have multiple launchers. Only the main one should have the 'send' property.
                default -> !has_send;
            };
            // TODO: just have a generic implementation of AppSearchCommand, this above is risky.
        }
    }
}
