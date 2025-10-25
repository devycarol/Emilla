package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.PEND;
import static net.emilla.chime.Chimer.RESUME;

import android.widget.EditText;

import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class CursorStart implements LabeledQuickAction {

    private final AssistActivity mActivity;

    public CursorStart(AssistActivity act) {
        mActivity = act;
    }

    @Override
    public int id() {
        return R.id.action_cursor_start;
    }

    @Override
    public int icon() {
        return R.drawable.ic_cursor_start;
    }

    @Override @StringRes
    public int label() {
        return R.string.action_cursor_start;
    }

    @Override @StringRes
    public int description() {
        return R.string.action_desc_cursor_start;
    }

    @Override
    public void perform() {
        EditText box = mActivity.focusedEditBox();
        int len = box.length();
        if (len == 0) {
            mActivity.chime(PEND);
            return;
        }
        int start = box.getSelectionStart();
        int end = box.getSelectionEnd();
        if (Math.max(start, end) == 0) {
            box.setSelection(len);
            mActivity.chime(RESUME);
        } else {
            box.setSelection(0);
            mActivity.chime(ACT);
        }
    }
}
