package net.emilla.command.core;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Note extends CoreDataCommand {

    public static final String ENTRY = "note";

    private static class NoteParams extends CoreDataParams {

        private NoteParams() {
            super(R.string.command_note,
                  R.string.instruction_file,
                  R.drawable.ic_note,
                  R.string.data_hint_note);
        }
    }

    @Override @ArrayRes
    public int details() {
        return R.array.details_note;
    }

    public Note(AssistActivity act, String instruct) {
        super(act, instruct, new NoteParams());
    }

    @Override
    protected void run() {
        throw new EmlaBadCommandException(R.string.command_note, R.string.error_unfinished_notes);
    }

    @Override
    protected void run(String title) {
        throw new EmlaBadCommandException(R.string.command_note, R.string.error_unfinished_notes);
    }

    @Override
    protected void runWithData(String text) {
        throw new EmlaBadCommandException(R.string.command_note, R.string.error_unfinished_notes);
    }

    @Override
    protected void runWithData(String title, String text) {
        throw new EmlaBadCommandException(R.string.command_note, R.string.error_unfinished_notes);
    }
}
