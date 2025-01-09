package net.emilla.command;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.app.AppCommand;
import net.emilla.lang.Lang;

public class DefaultCommandWrapper extends EmillaCommand {

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
    }

    @Override @Deprecated
    protected String dupeLabel() {
        // Todo: exclude this from the interface for wrappers
        return null;
    }

    protected DefaultCommandWrapper(AssistActivity act, String instruct, EmillaCommand cmd) {
        super(act, instruct, new DefaultWrapperParams(cmd));
        mCmd = cmd;
    }

    @Override @SuppressLint("MissingSuperCall")
    public void init(boolean updateTitle) {
        activity.updateTitle(title());
        mCmd.init(false);
    }

    @Override @SuppressLint("MissingSuperCall")
    public void clean() {
        mCmd.clean();
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
