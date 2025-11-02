package net.emilla.command.core;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Note extends CoreDataCommand {

    public static final String ENTRY = "note";

    public static Yielder yielder() {
        return new Yielder(CoreEntry.NOTE, true);
    }

    public static boolean possible() {
        return true;
    }

    /*internal*/ Note(AssistActivity act) {
        super(act, CoreEntry.NOTE, R.string.data_hint_note);
    }

    @Override
    protected void run() {
        throw badCommand(R.string.error_unfinished_notes);
    }

    @Override
    protected void run(String title) {
        throw badCommand(R.string.error_unfinished_notes);
    }

    @Override
    protected void runWithData(String text) {
        throw badCommand(R.string.error_unfinished_notes);
    }

    @Override
    protected void runWithData(String title, String text) {
        throw badCommand(R.string.error_unfinished_notes);
    }
}
