package net.emilla.command.app;

import static net.emilla.app.AppProperties.ordinary;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.AppProperties;

public final class Outlook extends AppSendData {

    public static final String PKG = "com.microsoft.office.outlook";
    @ArrayRes
    private static final int ALIASES = R.array.aliases_outlook;
    @StringRes
    private static final int SUMMARY = R.string.summary_email;

    public static AppProperties meta() {
        return ordinary(ALIASES, SUMMARY);
    }

    public Outlook(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_app_email,
              R.string.summary_email,
              R.string.data_hint_email);
    }
}
