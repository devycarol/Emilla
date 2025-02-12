package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public final class Outlook extends AppSendData {

    public static final String PKG = "com.microsoft.office.outlook";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_outlook;

    private static final class OutlookParams extends AppSendDataParams {

        private OutlookParams(Yielder info) {
            super(info,
                  R.string.instruction_app_email,
                  R.string.summary_email,
                  R.string.data_hint_email);
        }
    }

    public Outlook(AssistActivity act, Yielder info) {
        super(act, new OutlookParams(info));
    }
}
