package net.emilla.event;

public /*open*/ class Plan {

    public static final int
            POMODORO_WARNING = 1,
            POMODORO_ENDED = 2;

    public final int slot;
    public final long time;

    public Plan(int slot, long time) {
        this.slot = slot;
        this.time = time;
    }
}
