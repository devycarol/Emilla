package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class AospContacts extends AppSearch {

    public static final String PKG = "com.android.contacts";

    private static class ContactsParams extends AppSearchParams {

        private ContactsParams(AppInfo info) {
            super(info, R.string.instruction_contact, R.string.summary_app_aosp_contacts);
        }
    }

    public AospContacts(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new ContactsParams(info));
    }
}
