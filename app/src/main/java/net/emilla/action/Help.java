package net.emilla.action;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.EmillaCommand;
import net.emilla.run.DialogOffering;
import net.emilla.utils.Dialogs;

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
        // Todo: be able to include app-specific information
        EmillaCommand cmd = mActivity.command();
        AlertDialog.Builder manual = Dialogs.base(mActivity, cmd.name(), cmd.manual(),
                android.R.string.ok);
        mActivity.offer(new DialogOffering(mActivity, manual));
    }
}
