package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;
import static android.net.Uri.parse;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.settings.Aliases;

public class Dial extends CoreCommand {

    public static final String ENTRY = "dial";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_dial;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class DialParams extends CoreParams {

        private DialParams() {
            super(R.string.command_dial,
                  R.string.instruction_dial,
                  R.drawable.ic_dial,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_dial,
                  R.string.manual_dial);
        }
    }

    public Dial(AssistActivity act, String instruct) {
        super(act, instruct, new DialParams());
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_DIAL));
    }

    @Override
    protected void run(String numberOrPhoneword) {
        appSucceed(new Intent(ACTION_DIAL).setData(parse("tel:" + numberOrPhoneword)));
    }
}
