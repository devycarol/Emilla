package net.emilla.action;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.command.core.Torch;

public class Flashlight implements QuickAction {

    private final AssistActivity mActivity;

    public Flashlight(AssistActivity act) {
        mActivity = act;
    }

    @Override
    public int icon() {
        return R.drawable.ic_torch;
    }

    @Override
    public void perform() {
        new Torch(mActivity, null).execute();
    }
}
