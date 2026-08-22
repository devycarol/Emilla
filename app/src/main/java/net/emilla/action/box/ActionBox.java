package net.emilla.action.box;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import net.emilla.action.InstructyGadget;
import net.emilla.activity.AssistActivity;

public abstract class ActionBox extends Fragment implements InstructyGadget {
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
