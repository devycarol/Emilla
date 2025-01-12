package net.emilla.command.app;

import net.emilla.AssistActivity;
import net.emilla.R;

public class Github extends AppSendData {

    public static final String PKG = "com.github.android";

    private static class GithubParams extends AppSendDataParams {

        private GithubParams(AppInfo info) {
            super(info,
                  R.string.instruction_issue,
                  R.string.summary_issues,
                  R.string.data_hint_issue);
        }
    }

    public Github(AssistActivity act, String instruct, AppInfo info) {
        super(act, instruct, new GithubParams(info));
    }
}
