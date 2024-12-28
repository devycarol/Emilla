package net.emilla.action;

import static net.emilla.chime.Chimer.PEND;

import net.emilla.AssistActivity;
import net.emilla.R;

public class NoAction implements QuickAction {

    private final AssistActivity mActivity;

    public NoAction(AssistActivity act) {
        mActivity = act;
    }

    @Override
    public int icon() {
        return R.drawable.ic_assistant;
    }

    @Override
    public void perform() {
        mActivity.chime(PEND);
    }
}
