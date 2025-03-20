package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;

import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.settings.Aliases;

public final class Dial extends CoreCommand {

    public static final String ENTRY = "dial";
    @StringRes
    public static final int NAME = R.string.command_dial;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_dial;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Dial::new, ENTRY, NAME, ALIASES);
    }

    public Dial(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_dial,
              R.drawable.ic_dial,
              R.string.summary_dial,
              R.string.manual_dial,
              EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_DIAL));
    }

    @Override
    protected void run(String numberOrPhoneword) {
        appSucceed(new Intent(ACTION_DIAL).setData(Uri.parse("tel:" + numberOrPhoneword)));
    }
}
