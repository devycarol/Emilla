package net.emilla.ping;

import net.emilla.annotation.internal;

enum PingChannels {
    ;

    @internal static final int SLOT_UNLIMITED = 0;
    @internal static final int SLOT_POMODORO = 1;

    @internal static final String ID_COMMAND = "command";
    @internal static final String ID_POMODORO_START = "pomodoro_start";
    @internal static final String ID_POMODORO_WARNING = "pomodoro_warn";
    @internal static final String ID_POMODORO_END = "pomodoro_end";
    @internal static final String ID_POMODORO_BREAK_START = "pomodoro_break_start";
    @internal static final String ID_POMODORO_BREAK_WARNING = "pomodoro_break_warn";
    @internal static final String ID_POMODORO_BREAK_END = "pomodoro_break_end";

}
