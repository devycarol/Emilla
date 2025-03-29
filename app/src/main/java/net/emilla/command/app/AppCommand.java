package net.emilla.command.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppEntry;
import net.emilla.app.Apps;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.config.Aliases;
import net.emilla.lang.Lang;

import java.util.Set;

public /*open*/ class AppCommand extends EmillaCommand {

    @ArrayRes
    public static int aliases(AppEntry app) {
        return switch (app.pkg) {
            case AospContacts.PKG -> AospContacts.ALIASES;
            case Markor.PKG -> app.cls.equals(Markor.CLS_MAIN) ? Markor.ALIASES : 0;
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

    @StringRes
    public static int summary(AppEntry app) {
        return switch (app.pkg) {
            case AospContacts.PKG -> AospContacts.SUMMARY;
            case Markor.PKG -> app.cls.equals(Markor.CLS_MAIN) ? Markor.SUMMARY : 0;
            case Firefox.PKG -> Firefox.SUMMARY;
            case Tor.PKG -> Tor.SUMMARY;
            case Signal.PKG -> Signal.SUMMARY;
            case Newpipe.PKG -> Newpipe.SUMMARY;
            case Tubular.PKG -> Tubular.SUMMARY;
            case Tasker.PKG -> Tasker.SUMMARY;
            case Github.PKG -> Github.SUMMARY;
            case Youtube.PKG -> Youtube.SUMMARY;
            case Discord.PKG -> Discord.SUMMARY;
            case Outlook.PKG -> Outlook.SUMMARY;
            default -> 0;
        };
    }

    public static final class Yielder extends CommandYielder {

        private final AppEntry app;
        private final boolean hasSend;
        private final boolean usesInstruction;

        public Yielder(AppEntry app, PackageManager pm) {
            this.app = app;
            hasSend = Apps.sendToApp(app.pkg).resolveActivity(pm) != null;

            usesInstruction = switch (app.pkg) {
                case AospContacts.PKG, Firefox.PKG, Youtube.PKG, // search commands
                     Tasker.PKG -> true;
                case Tor.PKG -> false; // search/send intents are broken
                case Markor.PKG -> app.cls.equals(Markor.CLS_MAIN);
                // Markor can have multiple launchers, only the main should have the 'send' property.
                default -> hasSend;
            }; // Todo: handle in a more centralized way, this is tedious and error-prone.
        }

        @Override
        public boolean isPrefixable() {
            return usesInstruction;
        }

        @Override
        protected EmillaCommand makeCommand(AssistActivity act) {
            return switch(app.pkg) {
                case AospContacts.PKG -> new AospContacts(act, this);
                case Markor.PKG -> Markor.instance(act, this, app.cls);
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
                default -> hasSend ? new AppSend(act, this)
                                   : new AppCommand(act, this);
                // Todo: generic AppSearchCommand that merges with AppSendCommand
            };
        }

        public String name() {
            return app.label;
        }

        @Nullable
        public Set<String> aliases(SharedPreferences prefs, Resources res) {
            return Aliases.appSet(prefs, res, app);
        }
    }

    protected static /*open*/ class AppParams implements Params {

        protected final AppEntry app;

        protected AppParams(Yielder info) {
            app = info.app;
        }

        @Override
        public final String name(Resources res) {
            return app.label;
        }

        @Override
        public final Drawable icon(Context ctx) {
            return app.icon(ctx.getPackageManager());
        }

        @Override
        public /*open*/ String title(Resources res) {
            return Lang.colonConcat(res, R.string.command_app, app.label);
        }
    }

    protected static final class InstructyParams extends AppParams {

        @StringRes
        private final int mInstruction;

        InstructyParams(Yielder info, @StringRes int instruction) {
            super(info);
            mInstruction = instruction;
        }

        @Override
        public String title(Resources res) {
            return Lang.colonConcat(res, app.label, mInstruction);
        }
    }

    protected final AppEntry app;

    public AppCommand(AssistActivity act, Yielder info) {
        this(act, new AppParams(info),
             R.string.summary_app,
             R.string.manual_app,
             EditorInfo.IME_ACTION_GO);
    }

    protected AppCommand(
        AssistActivity act,
        AppParams params,
        @StringRes int summary,
        @StringRes int manual,
        int imeAction
    ) {
        super(act, params, summary, manual, imeAction);
        app = params.app;
    }

    @Override
    public final boolean shouldLowercase() {
        return false; // App names shouldn't be lowercased.
    }

    @Override @Deprecated
    protected final String dupeLabel() {
        return app.label + " (" + app.pkg + ")";
    }

    @Override
    public final boolean usesAppIcon() {
        return true;
    }

    @Override
    protected final void run() {
        appSucceed(app.launchIntent());
    }

    @Override
    protected /*open*/ void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }
}
