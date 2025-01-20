package net.emilla.command;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public interface DataCommand {

    @StringRes
    int dataHint();
    void execute(@NonNull String data);

    interface DataParams {
        @StringRes
        int hint();
    }
}
