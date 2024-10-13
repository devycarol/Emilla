package net.emilla.commands;

import androidx.annotation.StringRes;

public interface DataCmd {
@StringRes int dataHint();
void execute(String data);
}
