package net.emilla.command;

import androidx.annotation.StringRes;

public interface DataCmd {

    @StringRes
    int dataHint();
    void execute(String data);

    interface DataParams {
        @StringRes
        int hint();
    }
}
