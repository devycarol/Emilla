package net.emilla.action.box;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import net.emilla.action.InstructyGadget;
import net.emilla.activity.AssistActivity;
import net.emilla.run.TextGift;

public abstract class ActionBox extends Fragment implements InstructyGadget {
    protected ActionBox(@LayoutRes int contentLayout) {
        super(contentLayout);
    }

    protected static void giveText(AssistActivity act, CharSequence title, CharSequence text) {
        act.give(new TextGift(act, title, text));
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
