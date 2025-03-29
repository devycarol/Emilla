package net.emilla.command.app;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Github extends AppSendData {

    public static final String PKG = "com.github.android";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_github;
    @StringRes
    public static final int SUMMARY = R.string.summary_issues;

    public Github(AssistActivity act, Yielder info) {
        super(act, info,
              R.string.instruction_issue,
              R.string.summary_issues,
              R.string.data_hint_issue);
    }
}
