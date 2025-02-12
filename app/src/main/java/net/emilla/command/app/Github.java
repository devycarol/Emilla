package net.emilla.command.app;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;

public final class Github extends AppSendData {

    public static final String PKG = "com.github.android";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_github;

    private static final class GithubParams extends AppSendDataParams {

        private GithubParams(Yielder info) {
            super(info,
                  R.string.instruction_issue,
                  R.string.summary_issues,
                  R.string.data_hint_issue);
        }
    }

    public Github(AssistActivity act, Yielder info) {
        super(act, new GithubParams(info));
    }
}
