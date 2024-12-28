package net.emilla.action;

import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.PEND;
import static net.emilla.chime.Chimer.RESUME;

import android.widget.EditText;

import net.emilla.AssistActivity;
import net.emilla.R;

public class SelectAll implements QuickAction {

    private final AssistActivity mActivity;

    public SelectAll(AssistActivity act) {
        mActivity = act;
    }

    @Override
    public int icon() {
        return R.drawable.ic_select_all;
    }

    @Override
    public void perform() {
        EditText field = mActivity.focusedEditBox();
        if (field.length() != 0) {
            int selStart = field.getSelectionStart();
            int selEnd = field.getSelectionEnd();
            int len = field.length();
            if (selStart != 0 || selEnd != len) {
                field.selectAll();
                mActivity.chime(ACT);
            } else {
                field.setSelection(len, len);
                mActivity.chime(RESUME);
            }
        } else mActivity.chime(PEND);
    }
}
