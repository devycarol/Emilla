package net.emilla.command;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppCommand;
import net.emilla.lang.Lang;

public final class DefaultCommandWrapper extends EmillaCommand {

    public static final class Yielder extends CommandYielder {

        private final CommandYielder mYielder;

        public Yielder(CommandYielder yielder) {
            mYielder = yielder;
        }

        @Override
        public boolean isPrefixable() {
            return mYielder.isPrefixable();
        }

        @Override
        protected EmillaCommand makeCommand(AssistActivity act) {
            return new DefaultCommandWrapper(act, mYielder.command(act));
        }
    }

    private record DefaultWrapperParams(EmillaCommand cmd) implements Params {

        @Override
        public String name(Resources res) {
            return cmd.name();
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, R.string.command_default, cmd.sentenceName());
        }

        @Override
        public Drawable icon(Context ctx) {
            return cmd.icon();
        }
    }

    @Override
    protected boolean shouldLowercase() {
        return true; // Todo: exclude this from the interface for wrappers
    }

    @Override @Nullable @Deprecated
    protected String dupeLabel() {
        return null; // Todo: exclude this from the interface for wrappers
    }

    @Override
    public boolean usesAppIcon() {
        return mCmd instanceof AppCommand;
    }

    private final EmillaCommand mCmd; // Todo: allow app and data commands

    private DefaultCommandWrapper(AssistActivity act, EmillaCommand cmd) {
        super(act, new DefaultWrapperParams(cmd), cmd.summary, cmd.manual, cmd.imeAction);

        mCmd = cmd;
    }

    @Override
    protected void onInit() {
        super.onInit();
        mCmd.onInit();
    }

    @Override
    protected void onClean() {
        super.onClean();
        mCmd.onClean();
    }

    @Override
    protected void run() {
        mCmd.run();
    }

    @Override
    protected void run(String instruction) {
        mCmd.run(instruction);
    }
}
