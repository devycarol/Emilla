package net.emilla.command;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.app.AppCommand;
import net.emilla.lang.Lang;

public class DefaultCommandWrapper extends EmillaCommand {

    public static class Yielder extends CommandYielder {

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

    private final EmillaCommand mCmd; // Todo: allow app commands

    private static class DefaultWrapperParams implements Params {

        private final EmillaCommand mCmd;

        private DefaultWrapperParams(EmillaCommand cmd) {
            mCmd = cmd;
        }

        @Override
        public CharSequence name(Resources res) {
            return mCmd.name();
        }

        @Override
        public boolean shouldLowercase() {
            return true;
        }

        @Override
        public CharSequence title(Resources res) {
            return Lang.colonConcat(res, R.string.command_default, mCmd.sentenceName());
        }

        @Override
        public Drawable icon(Context ctx) {
            return mCmd.icon();
        }

        @Override
        public boolean usesAppIcon() {
            return mCmd instanceof AppCommand;
        }

        @Override
        public int imeAction() {
            return mCmd.imeAction();
        }

        @Override
        public int summary() {
            return mCmd.summary();
        }

        @Override
        public int manual() {
            return mCmd.manual();
        }
    }

    @Override @Deprecated
    protected String dupeLabel() {
        // Todo: exclude this from the interface for wrappers
        return null;
    }

    protected DefaultCommandWrapper(AssistActivity act, EmillaCommand cmd) {
        super(act, new DefaultWrapperParams(cmd));
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
    protected void run(@NonNull String instruction) {
        mCmd.run(instruction);
    }
}
