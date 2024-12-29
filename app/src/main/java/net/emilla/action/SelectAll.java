package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.PEND;
import static net.emilla.chime.Chimer.RESUME;

import android.content.res.Resources;
import android.widget.EditText;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class SelectAll implements QuickAction {

    private final AssistActivity mActivity;
    private final Resources mRes;

    public SelectAll(AssistActivity act) {
        mActivity = act;
        mRes = act.getResources();
    }

    @Override @IdRes
    public int id() {
        return R.id.action_select_all;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_select_all;
    }

    @Override
    public String label() {
        return mRes.getString(R.string.action_select_all);
    }

    @Override
    public String description() {
        return mRes.getString(R.string.action_desc_select_all);
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
        } else mActivity.chime(PEND);
    }
}
