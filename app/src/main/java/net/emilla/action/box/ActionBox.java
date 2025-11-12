package net.emilla.action.box;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.emilla.action.InstructyGadget;
import net.emilla.activity.AssistActivity;
import net.emilla.run.CopyGift;
import net.emilla.run.DialogRun;
import net.emilla.run.TextGift;

public abstract class ActionBox extends Fragment implements InstructyGadget {

    protected ActionBox(@LayoutRes int contentLayout) {
        super(contentLayout);
    }

    protected static void offerDialog(AssistActivity act, AlertDialog.Builder dlg) {
        act.offer(new DialogRun(dlg));
    }

    protected static void giveText(AssistActivity act, CharSequence title, CharSequence text) {
        act.give(new TextGift(act, title, text));
    }

    protected static void giveCopy(AssistActivity act, CharSequence text) {
        act.give(new CopyGift(text));
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
