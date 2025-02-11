package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

public class Note extends CoreDataCommand {

    public static final String ENTRY = "note";
    @StringRes
    public static final int NAME = R.string.command_note;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_note;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Note::new, ENTRY, NAME, ALIASES);
    }

    private static class NoteParams extends CoreDataParams {

        private NoteParams() {
            super(NAME,
                  R.string.instruction_file,
                  R.drawable.ic_note,
                  R.string.summary_note,
                  R.string.manual_note,
                  R.string.data_hint_note);
        }
    }

    public Note(AssistActivity act) {
        super(act, new NoteParams());
    }

    @Override
    protected void run() {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_notes);
    }

    @Override
    protected void run(String title) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_notes);
    }

    @Override
    protected void runWithData(String text) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_notes);
    }

    @Override
    protected void runWithData(String title, String text) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_notes);
    }
}
