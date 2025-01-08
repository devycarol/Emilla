package net.emilla.command.core;

import androidx.annotation.ArrayRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class Note extends CoreDataCommand {

    public static final String ENTRY = "note";

    @Override @ArrayRes
    public int details() {
        return R.array.details_note;
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_note;
    }

    public Note(AssistActivity act, String instruct) {
        super(act, instruct, R.string.command_note, R.string.instruction_file);
    }

    @Override @StringRes
    public int dataHint() {
        return R.string.data_hint_note;
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
