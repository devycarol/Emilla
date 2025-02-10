package net.emilla.command.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.lang.Lang;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;

import java.util.Set;

public class AppCommand extends EmillaCommand {

    private static class BasicAppParams extends AppParams {

        private BasicAppParams(Yielder info) {
            super(info,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_app,
                  R.string.manual_app);
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, R.string.command_app, name);
        }
    }

    private final CharSequence mName;
    private final ComponentName mComponentName;
    protected final String packageName;

    public AppCommand(AssistActivity act, Yielder info) {
        this(act, new BasicAppParams(info));
    }

    protected AppCommand(AssistActivity act, AppParams params) {
        super(act, params);
        mName = params.name;
        mComponentName = params.mComponentName;
        packageName = params.mPkg;
    }

    @Override @Deprecated
    protected String dupeLabel() {
        return mName + " (" + packageName + ")";
    }

    @Override
    protected final void run() {
        appSucceed(launchIntent());
    }

    protected final Intent launchIntent() {
        return Apps.launchIntent(mComponentName);
    }

    @Override
    protected void run(@NonNull String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }

    public static class Yielder extends CommandYielder {

        private final String mPkg;
        public final String cls;
        private final boolean mHasSend;
        private final boolean mUsesInstruction;
        private final CharSequence mName;

        public Yielder(ActivityInfo info, PackageManager pm) {
            mPkg = info.packageName;
            cls = info.name;
            mHasSend = Apps.sendToApp(mPkg).resolveActivity(pm) != null;

            mUsesInstruction = switch (mPkg) {
                case AospContacts.PKG, Firefox.PKG, Youtube.PKG, // search commands
                     Tasker.PKG -> true;
                case Tor.PKG -> false; // search/send intents are broken
                case Markor.PKG -> cls.equals(Markor.CLS_MAIN);
                // Markor can have multiple launchers, only the main should have the 'send' property.
                default -> mHasSend;
            }; // Todo: handle in a more centralized way, this is tedious and error-prone.

            mName = info.loadLabel(pm);
            // TODO: this is the biggest performance bottleneck I've found so far. Look into how the
            //  launcher caches labels for ideas on how to improve the performance of this critical
            //  onCreate task. That is, if they do to begin with..
        }

        @Override
        public boolean isPrefixable() {
            return mUsesInstruction;
        }

        @Override
        protected EmillaCommand makeCommand(AssistActivity act) {
            return switch(mPkg) {
                case AospContacts.PKG -> new AospContacts(act, this);
                case Markor.PKG -> Markor.instance(act, this);
                case Firefox.PKG -> new Firefox(act, this);
                case Tor.PKG -> new Tor(act, this);
                case Signal.PKG -> new Signal(act, this);
                case Newpipe.PKG -> new Newpipe(act, this);
                case Tubular.PKG -> new Tubular(act, this);
                case Tasker.PKG -> new Tasker(act, this);
                case Github.PKG -> new Github(act, this);
                case Youtube.PKG -> new Youtube(act, this);
                case Discord.PKG -> new Discord(act, this);
                case Outlook.PKG -> new Outlook(act, this);
                default -> mHasSend ? new AppSend(act, this)
                        : new AppCommand(act, this);
                // Todo: generic AppSearchCommand that merges with AppSendCommand
            };
        }

        public final String name() {
            return mName.toString();
        }

        public final ComponentName componentName() {
            return new ComponentName(mPkg, cls);
        }

        @Nullable
        public final Set<String> aliases(SharedPreferences prefs, Resources res) {
            return Aliases.appSet(prefs, res, mPkg, aliasId(mPkg, cls));
        }
    }

    @ArrayRes
    public static int aliasId(String pkg, String cls) {
        return switch (pkg) {
            case AospContacts.PKG -> AospContacts.ALIASES;
            case Markor.PKG -> cls.equals(Markor.CLS_MAIN) ? Markor.ALIASES : 0;
            // Markor can have multiple launchers, only the main one should have the aliases.
            case Firefox.PKG -> Firefox.ALIASES;
            case Tor.PKG -> Tor.ALIASES;
            case Signal.PKG -> Signal.ALIASES;
            case Newpipe.PKG -> Newpipe.ALIASES;
            case Tubular.PKG -> Tubular.ALIASES;
            case Tasker.PKG -> Tasker.ALIASES;
            case Github.PKG -> Github.ALIASES;
            case Youtube.PKG -> Youtube.ALIASES;
            case Discord.PKG -> Discord.ALIASES;
            case Outlook.PKG -> Outlook.ALIASES;
            default -> 0;
        };
    }

    protected static abstract class AppParams implements Params {

        private final int mImeAction;
        protected final CharSequence name;
        private final String mPkg;
        private final ComponentName mComponentName;
        @StringRes
        private final int mSummary, mManual;

        protected AppParams(Yielder info, int imeAction, @StringRes int summary,
                @StringRes int manual) {
            mImeAction = imeAction;
            name = info.mName;
            mPkg = info.mPkg;
            mComponentName = info.componentName();
            mSummary = summary;
            mManual = manual;
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
            return ctx.getPackageManager().getActivityIcon(mComponentName);
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

        @Override
        public int summary() {
            return mSummary;
        }

        @Override
        public int manual() {
            return mManual;
        }
    }
}
