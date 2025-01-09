package net.emilla.command.app;

import static net.emilla.utils.Apps.*;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.EditorInfo;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.utils.Apps;

public class AppCommand extends EmillaCommand {

    private static class BasicAppParams extends AppParams {

        private BasicAppParams(AppInfo info) {
            super(info, EditorInfo.IME_ACTION_GO);
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, R.string.command_app, name);
        }
    }

    private final CharSequence mName;
    protected final String packageName;
    private final String mClassName;

    public AppCommand(AssistActivity act, String instruct, AppInfo info) {
        this(act, instruct, new BasicAppParams(info));
    }

    protected AppCommand(AssistActivity act, String instruct, AppParams params) {
        super(act, instruct, params);
        mName = params.name;
        packageName = params.mInfo.pkg;
        mClassName = params.mInfo.cls;
    }

    @Override @Deprecated
    protected String dupeLabel() {
        return mName + " (" + packageName + ")";
    }

    @Override
    protected final void run() {
        appSucceed(Apps.launchIntent(packageName, mClassName));
    }

    @Override
    protected void run(String ignored) {
        run(); // TODO: instead, this should revert to the default command
    }

    public static class AppInfo {

        public final String pkg;
        public final String cls;
        public final boolean hasSend;
        public final boolean basic;
        private final CharSequence mName;

        public AppInfo(ActivityInfo info, PackageManager pm, CharSequence name) {
            this.pkg = info.packageName;
            this.cls = info.name;
            this.hasSend = sendToApp(pkg).resolveActivity(pm) != null;
            this.basic = switch (pkg) {
                case AospContacts.PKG, Firefox.PKG, Youtube.PKG -> false; // search commands
                case Tor.PKG -> true; // search/send intents are broken
                case Markor.PKG -> !cls.equals(Markor.CLS_MAIN);
                // Markor can have multiple launchers, only the main should have the 'send' property.
                default -> !hasSend;
            };
            // TODO: just have a generic implementation of AppSearchCommand, this above is risky.
            mName = name;
        }
    }

    protected static abstract class AppParams implements Params {

        private final AppInfo mInfo;
        private final int mImeAction;
        protected final CharSequence name;

        protected AppParams(AppInfo info, int imeAction) {
            mInfo = info;
            mImeAction = imeAction;
            name = info.mName;
        }

        @Override
        public final CharSequence name(Resources res) {
            return name;
        }

        @Override
        public final boolean shouldLowercase() {
            return false; // App names shouldn't be lowercased.
        }

        @Override
        public final Drawable icon(Context ctx) { try {
            return ctx.getPackageManager().getActivityIcon(new ComponentName(mInfo.pkg, mInfo.cls));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Activity wasn't found.", e);
        }}

        @Override
        public final boolean usesAppIcon() {
            return true;
        }

        @Override
        public final int imeAction() {
            return mImeAction;
        }
    }
}
