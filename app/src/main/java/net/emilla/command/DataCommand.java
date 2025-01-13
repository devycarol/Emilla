package net.emilla.command;

import androidx.annotation.StringRes;

public interface DataCommand {

    @StringRes
    int dataHint();
    void execute(String data);

    interface DataParams {
        @StringRes
        int hint();
    }
}
