package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.PEND;
import static net.emilla.chime.Chimer.RESUME;
import static java.lang.Math.max;

import android.content.res.Resources;
import android.widget.EditText;

import net.emilla.AssistActivity;
import net.emilla.R;

public class CursorStart implements QuickAction {

    private final AssistActivity mActivity;
    private final Resources mRes;

    public CursorStart(AssistActivity act) {
        mActivity = act;
        mRes = act.getResources();
    }

    @Override
    public int id() {
        return R.id.action_cursor_start;
    }

    @Override
    public int icon() {
        return R.drawable.ic_cursor_start;
    }

    @Override
    public String label() {
        return mRes.getString(R.string.action_cursor_start);
    }

    @Override
    public String description() {
        return mRes.getString(R.string.action_desc_cursor_start);
    }

    @Override
    public void perform() {
        EditText box = mActivity.focusedEditBox();
        int len = box.length();
        if (len == 0) {
            mActivity.chime(PEND);
            return;
        }
        int start = box.getSelectionStart(), end = box.getSelectionEnd();
        if (max(start, end) == 0) {
            box.setSelection(len);
            mActivity.chime(RESUME);
        } else {
            box.setSelection(0);
            mActivity.chime(ACT);
        }
    }
}
