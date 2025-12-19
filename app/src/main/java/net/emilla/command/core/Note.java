package net.emilla.command.core;

import android.content.Context;
import android.net.Uri;

import net.emilla.R;
import net.emilla.action.box.NotesFragment;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.exception.EmillaException;
import net.emilla.file.Files;
import net.emilla.file.Folder;
import net.emilla.file.TreeFile;

final class Note extends CoreDataCommand {

    private final NotesFragment mNotesFragment;

    @internal Note(Context ctx) {
        super(ctx, CoreEntry.NOTE, R.string.data_hint_text);

        mNotesFragment = NotesFragment.newInstance();

        giveGadgets(mNotesFragment);
    }

    @Override
    protected void run(AssistActivity act) {
        act.offerSaveFile(null, mNotesFragment.folder(), null);
    }

    @Override
    protected void run(AssistActivity act, String filename) {
        Folder folder = mNotesFragment.folder();
        TreeFile existingFile = mNotesFragment.fileNamed(filename);
        if (folder != null && existingFile != null) {
            appSucceed(act, existingFile.viewIntent(folder));
            return;
        }

        act.offerSaveFile(filename, folder, null);
    }

    @Override
    public void runWithData(AssistActivity act, String text) {
        act.offerSaveFile(null, mNotesFragment.folder(), text);
    }

    @Override
    public void runWithData(AssistActivity act, String filename, String text) {
        Folder folder = mNotesFragment.folder();
        TreeFile existingFile = mNotesFragment.fileNamed(filename);
        if (folder != null && existingFile != null) {
            appendToFile(act, existingFile.uri(folder), text);
            return;
        }

        act.offerSaveFile(filename, folder, text);
    }

    private static void appendToFile(AssistActivity act, Uri file, String text) {
        if (Files.appendLine(act.getContentResolver(), file, text)){
            act.give(a -> {});
        } else {
            throw new EmillaException(R.string.error_cant_use_file);
        }
    }

}
