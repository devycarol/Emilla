package net.emilla.command.core;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.util.Dialogs;

import java.util.Random;

public final class Roll extends CoreCommand {

    public static final String ENTRY = "roll";
    @StringRes
    public static final int NAME = R.string.command_roll;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_roll;

    public static Yielder yielder() {
        return new Yielder(true, Roll::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Roll(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_text,
              R.drawable.ic_roll,
              R.string.summary_roll,
              R.string.manual_roll,
              EditorInfo.IME_ACTION_DONE);
    }

    @Override
    protected void run() {
        var rand = new Random();
        @StringRes int msg = rand.nextBoolean() ? R.string.heads : R.string.tails;
        giveDialog(Dialogs.message(activity, NAME, msg));
    }

    @Override
    protected void run(String roll) {
        var dices = Lang.dices(roll, NAME);
        var rand = new Random();
        var msg = String.valueOf(dices.roll(rand));
        giveDialog(Dialogs.message(activity, NAME, msg));
    }
}
