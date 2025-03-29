package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class AospContacts extends AppSearch {

    public static final String PKG = "com.android.contacts";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_aosp_contacts;
    @StringRes
    public static final int SUMMARY = R.string.summary_app_aosp_contacts;

    public AospContacts(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_contact,
              SUMMARY);
    }
}
