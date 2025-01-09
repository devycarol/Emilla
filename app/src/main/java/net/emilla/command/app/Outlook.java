package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Outlook extends AppSendData {

    public static final String PKG = "com.microsoft.office.outlook";

    private static class OutlookParams extends AppSendDataParams {

        private OutlookParams(AppInfo info) {
            super(info, R.string.instruction_app_email, R.string.data_hint_email);
        }
    }

    public Outlook(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new OutlookParams(info));
    }
}
