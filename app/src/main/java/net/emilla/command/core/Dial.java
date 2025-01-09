package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;
import static android.net.Uri.parse;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Dial extends CoreCommand {

    public static final String ENTRY = "dial";

    private static class DialParams extends CoreParams {

        private DialParams() {
            super(R.string.command_dial,
                  R.string.instruction_dial,
                  R.drawable.ic_dial,
                  EditorInfo.IME_ACTION_GO);
        }
    }

    @Override @ArrayRes
    public int details() {
        return R.array.details_call_phone;
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
