package net.emilla.command.core;

import android.net.Uri;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.Gadget;
import net.emilla.action.Widget;
import net.emilla.action.box.NotesFragment;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.datafield.DataDirective;
import net.emilla.datafield.DataField;
import net.emilla.file.Files;
import net.emilla.file.Folder;
import net.emilla.file.TreeFile;
import net.emilla.wadget.ActionSurface;

final class Note extends EmillaCommand {
    private final NotesFragment mNotesFragment = NotesFragment.newInstance();

    @internal Note(ActionSurface surface) {
        super(surface, CoreEntry.NOTE);
    }

    @Override
    public DataDirective dataDirective() {
        return new DataField(R.string.data_hint_text);
    }

    @Override
    protected Widget[] widgets() {
        return new Widget[] {
            mNotesFragment,
        };
    }

    @Override
    protected Gadget[] gadgets() {
        return new Gadget[] {
            mNotesFragment,
        };
    }

    @Override
    protected Feedback run(ActionSurface surface) {
        String text = surface.dataText();
        surface.getAssistActivity()
            .offerSaveFile(null, mNotesFragment.folder(), text)
        ;
        return Feedback.silence();
        // intrinsic 'pend' by the file manager
    }

    @Override
    protected Feedback run(ActionSurface surface, String filename) {
        String text = surface.dataText();
        Folder folder = mNotesFragment.folder();
        TreeFile existingFile = mNotesFragment.fileNamed(filename);
        if (folder != null && existingFile != null) {
            if (text == null) {
                return Feedback.succeed(existingFile.viewIntent(folder));
            }

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
