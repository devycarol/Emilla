package net.emilla.command.core;

import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.FileReceiver;

import java.util.ArrayList;
import java.util.List;

public abstract class AttachCommand extends CoreDataCommand implements FileReceiver {

    protected ArrayList<Uri> attachments;

    protected AttachCommand(
        AssistActivity act,
        @StringRes int name,
        @StringRes int instruction,
        @DrawableRes int icon,
        @StringRes int summary,
        @StringRes int manual,
        @StringRes int hint
    ) {
        super(act, name, instruction, icon, summary, manual, hint);
    }

    @Override
    public final void provide(@NonNull List<Uri> attachments) {
        if (attachments.isEmpty()) return;
        if (this.attachments == null) this.attachments = new ArrayList<>(attachments);
        else for (Uri attachment : attachments) {
            int index = this.attachments.indexOf(attachment);
            if (index == -1) this.attachments.add(attachment);
            else this.attachments.remove(index);
            // TODO: better attachment management
        }

        int size = this.attachments.size();
        if (size == 0) this.attachments = null;
        toast(quantityString(R.plurals.toast_files_attached, size)); // Todo: better feedback
    }
}
