package net.emilla.action;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_MENU;

import android.content.res.Resources;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.EmillaCommand;
import net.emilla.run.DialogOffering;
import net.emilla.util.Dialogs;

public class Help implements LabeledQuickAction {

    private final AssistActivity mActivity;

    public Help(AssistActivity act) {
        mActivity = act;
    }

    @Override
    public int id() {
        return R.id.action_help;
    }

    @Override
    public int icon() {
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
        if (mActivity.cancelManualIfShowing()) return;
        // it's the double assist action and the manual is already open
        EmillaCommand cmd = mActivity.command();
        // Todo: pull up a general manual when no command.
        Resources res = mActivity.getResources();
        CharSequence msg = res.getText(cmd.summary()) + "\n\n" + res.getText(cmd.manual());
        // TODO: resolve weird whitespace parsing.
        AlertDialog manual = Dialogs.base(mActivity, cmd.name(), msg, android.R.string.ok)
                .setOnDismissListener(dlg -> mActivity.setManual(null))
                .setOnKeyListener((dlg, keyCode, event) -> {
                    if (keyCode == KEYCODE_MENU && event.getAction() == ACTION_UP) {
                        dlg.cancel();
                        return true;
                    }
                    return false;
                }).create();
        mActivity.setManual(manual);
        mActivity.offer(new DialogOffering(mActivity, manual));
    }
}
