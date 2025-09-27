package net.emilla.command.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppEntry;
import net.emilla.command.CommandYielder;
import net.emilla.command.EmillaCommand;
import net.emilla.config.Aliases;
import net.emilla.lang.Lang;

import java.util.Set;

public /*open*/ class AppCommand extends EmillaCommand {

    public static final class Yielder extends CommandYielder {

        private final AppEntry app;
        private final boolean usesInstruction;

        public Yielder(AppEntry app) {
            this.app = app;
            usesInstruction = app.usesInstruction();
        }

        @Override
        public boolean isPrefixable() {
            return usesInstruction;
        }

        @Override
        protected EmillaCommand makeCommand(AssistActivity act) {
            return switch (app.pkg) {
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
                default -> app.hasSend() ? new AppSend(act, this)
                         : app.hasSearch() ? new AppSearch(act, this)
                         // Todo: merge AppSearchCommand with AppSendCommand
                         : new AppCommand(act, this);
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

        protected final AppEntry pApp;

        protected AppParams(Yielder info) {
            pApp = info.app;
        }

        @Override
        public final String name(Resources res) {
            return pApp.label;
        }

        @Override
        public final Drawable icon(Context ctx) {
            return pApp.icon(ctx.getPackageManager());
        }

        @Override
        public /*open*/ String title(Resources res) {
            return Lang.colonConcat(res, R.string.command_app, pApp.label);
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
            return Lang.colonConcat(res, pApp.label, mInstruction);
        }
    }

    protected final AppEntry pApp;

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
        pApp = params.pApp;
    }

    @Override
    public final boolean shouldLowercase() {
        return false; // App names shouldn't be lowercased.
    }

    @Override @Deprecated
    protected final String dupeLabel() {
        return pApp.label + " (" + pApp.pkg + ")";
    }

    @Override
    public final boolean usesAppIcon() {
        return true;
    }

    @Override
    protected final void run() {
        appSucceed(pApp.launchIntent());
    }

    @Override
    protected /*open*/ void run(String ignored) {
        run(); // Todo: remove this from the interface for non-instructables.
    }
}
