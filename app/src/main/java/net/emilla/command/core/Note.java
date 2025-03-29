package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;

public final class Note extends CoreDataCommand {

    public static final String ENTRY = "note";
    @StringRes
    public static final int NAME = R.string.command_note;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_note;

    public static Yielder yielder() {
        return new Yielder(true, Note::new, ENTRY, NAME, ALIASES);
    }

    public static boolean possible() {
        return true;
    }

    private Note(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_file,
              R.drawable.ic_note,
              R.string.summary_note,
              R.string.manual_note,
              R.string.data_hint_note);
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
