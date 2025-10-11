package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.PEND;
import static net.emilla.chime.Chimer.RESUME;

import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class SelectAll implements LabeledQuickAction {

    private final AssistActivity mActivity;

    public SelectAll(AssistActivity act) {
        mActivity = act;
    }

    @Override @IdRes
    public int id() {
        return R.id.action_select_all;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_select_all;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_select_all;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_select_all;
    }

    @Override
    public void perform() {
        EditText box = mActivity.focusedEditBox();
        if (box.length() != 0) {
            int selStart = box.getSelectionStart();
            int selEnd = box.getSelectionEnd();
            int len = box.length();
            if (selStart != 0 || selEnd != len) {
                box.selectAll();
                mActivity.chime(ACT);
            } else {
                box.setSelection(len, len);
                mActivity.chime(RESUME);
            }
        } else {
            mActivity.chime(PEND);
        }
    }
}
