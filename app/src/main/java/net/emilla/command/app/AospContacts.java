package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public final class AospContacts extends AppSearch {

    public static final String PKG = "com.android.contacts";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_aosp_contacts;

    public AospContacts(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_contact,
              R.string.summary_app_aosp_contacts);
    }
}
