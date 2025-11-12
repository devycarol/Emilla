package net.emilla.command;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

public enum SubcommandEntry {
    ;

    @StringRes
    public final int name;
    @ArrayRes
    public final int aliases;

    SubcommandEntry(int name, int aliases) {
        this.name = name;
        this.aliases = aliases;
    }

}
