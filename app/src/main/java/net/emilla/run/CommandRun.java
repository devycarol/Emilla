package net.emilla.run;

import net.emilla.activity.AssistActivity;

@FunctionalInterface
public interface CommandRun {
    void run(AssistActivity act);
}
