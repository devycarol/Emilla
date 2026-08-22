package net.emilla.command.core;

import android.content.Context;
import android.net.Uri;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.box.NotesFragment;
import net.emilla.annotation.internal;
import net.emilla.file.Files;
import net.emilla.file.Folder;
import net.emilla.file.TreeFile;
import net.emilla.wadget.ActionSurface;

final class Note extends CoreDataCommand {
    private final NotesFragment mNotesFragment;

    @internal Note(Context ctx) {
        super(ctx, CoreEntry.NOTE, R.string.data_hint_text);

        mNotesFragment = NotesFragment.newInstance();

        giveGadgets(mNotesFragment);
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        surface.getAssistActivity().offerSaveFile(null, mNotesFragment.folder(), null);
        return Feedback.silence();
        // intrinsic 'pend' by the file manager
    }

    @Override
    protected Feedback run(ActionSurface surface, String filename) {
        Folder folder = mNotesFragment.folder();
        TreeFile existingFile = mNotesFragment.fileNamed(filename);
        if (folder != null && existingFile != null) {
            return Feedback.succeed(existingFile.viewIntent(folder));
        }

        surface.getAssistActivity().offerSaveFile(filename, folder, null);
        return Feedback.silence();
        // intrinsic 'pend' by the file manager
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String text) {
        surface.getAssistActivity().offerSaveFile(null, mNotesFragment.folder(), text);
        return Feedback.silence();
        // intrinsic 'pend' by the file manager
    }

    @Override
    public Feedback runWithData(ActionSurface surface, String filename, String text) {
        Folder folder = mNotesFragment.folder();
        TreeFile existingFile = mNotesFragment.fileNamed(filename);
        if (folder != null && existingFile != null) {
            Uri file = existingFile.uri(folder);
            return Files.appendLine(surface.getContentResolver(), file, text)
                ? Feedback.give()
                : Feedback.fail(R.string.error_cant_use_file)
            ;
        }

        surface.getAssistActivity().offerSaveFile(filename, folder, text);
        return Feedback.silence();
        // intrinsic 'pend' by the file manager
    }
}
