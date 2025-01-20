package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;
import static android.net.Uri.parse;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public class Dial extends CoreCommand {

    public static final String ENTRY = "dial";
    @StringRes
    public static final int NAME = R.string.command_dial;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_dial;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Dial::new, ENTRY, NAME, ALIASES);
    }

    private static class DialParams extends CoreParams {

        private DialParams() {
            super(NAME,
                  R.string.instruction_dial,
                  R.drawable.ic_dial,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_dial,
                  R.string.manual_dial);
        }
    }

    public Dial(AssistActivity act) {
        super(act, new DialParams());
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_DIAL));
    }

    @Override
    protected void run(@NonNull String numberOrPhoneword) {
        appSucceed(new Intent(ACTION_DIAL).setData(parse("tel:" + numberOrPhoneword)));
    }
}
