package net.emilla.action.box;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import net.emilla.action.Widget;
import net.emilla.activity.AssistActivity;

public abstract class ActionBox extends Fragment implements Widget {
    protected ActionBox(@LayoutRes int contentLayout) {
        super(contentLayout);
    }

    @Override
    public final void load(AssistActivity act) {
        act.giveActionBox(this);
    }

    @Override
    public final void unload(AssistActivity act) {
        act.removeActionBox(this);
    }
}
