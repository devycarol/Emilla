package net.emilla.action;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_MENU;

import android.content.res.Resources;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.EmillaCommand;
import net.emilla.util.Dialogs;

public final class Help implements LabeledQuickAction {
    private final AssistActivity mActivity;

    public Help(AssistActivity act) {
        mActivity = act;
    }

    @Override @IdRes
    public int id() {
        return R.id.action_help;
    }

    @Override @DrawableRes
    public int symbol() {
        return R.drawable.ic_help;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_help;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_help;
    }

    @Override
    public void perform() {
        perform(mActivity);
    }

    public static void perform(AssistActivity act) {
        if (act.cancelManualIfShowing()) {
            return;
        }

        // it's the double assist action and the manual is already open
        EmillaCommand cmd = act.command();
        // Todo: pull up a general manual when no command.
        Resources res = act.getResources();
        String msg = res.getText(cmd.summary) + "\n\n" + res.getText(cmd.manual);
        // TODO: resolve weird whitespace parsing.
        AlertDialog manual = Dialogs.message(act, cmd.name, msg)
            .setOnDismissListener(dlg -> act.setManual(null))
            .setOnKeyListener((dlg, keyCode, event) -> {
                // this is necessary because the AlertDialog becomes the consumer of key-events
                if (keyCode == KEYCODE_MENU && event.getAction() == ACTION_UP) {
                    dlg.cancel();
                    return true;
                }
                return false;
            })
            .create()
        ;
        act.setManual(manual);
        act.offer(a -> {
            manual.setOnCancelListener(dlg -> {
                act.onCloseDialog(); // Todo: don't require this
                act.resume();
            });
            act.prepareForDialog();
            manual.show();
        });
    }
}
