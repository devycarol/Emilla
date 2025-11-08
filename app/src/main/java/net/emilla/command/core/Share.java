package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;
import static net.emilla.chime.Chime.RESUME;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Files.MimeType;
import net.emilla.util.MimeTypes;

import java.util.ArrayList;

/*internal*/ final class Share extends CoreDataCommand implements AppChoiceReceiver {

    public static final String ENTRY = "share";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Apps.sendTask(MimeTypes.PLAIN_TEXT));
    }

    private FileFetcher mFileFetcher = null;
    private MediaFetcher mMediaFetcher = null;

    /*internal*/ Share(AssistActivity act) {
        super(act, CoreEntry.SHARE, R.string.data_hint_text);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mFileFetcher == null) mFileFetcher = new FileFetcher(this.activity, ENTRY, "*/*");
        giveAction(mFileFetcher);
        if (mMediaFetcher == null) mMediaFetcher = new MediaFetcher(this.activity, ENTRY);
        giveAction(mMediaFetcher);
    }

    @Override
    protected void onClean() {
        super.onClean();

        removeAction(FileFetcher.ID);
        removeAction(MediaFetcher.ID);
    }

    private Intent makeIntent() {
        ArrayList<Uri> attachments = this.activity.attachments(ENTRY);

        if (attachments == null) return Apps.sendTask(MimeTypes.PLAIN_TEXT);
        if (attachments.size() == 1) {
            Intent in = Apps.sendTask(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments.get(0));
            in.setSelector(Apps.sendTask(MimeType.of(attachments, this.activity)));
            return in;
        }
        Intent in = Apps.sendMultipleTask(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments);
        in.setSelector(Apps.sendMultipleTask(MimeType.of(attachments, this.activity)));
        return in;
    }

    private Intent makeIntent(String text) {
        ArrayList<Uri> attachments = this.activity.attachments(ENTRY);
        if (attachments == null) return Apps.sendTask(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_TEXT, text);

        Intent in;
        if (attachments.size() == 1) {
            in = Apps.sendTask(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments.get(0));
            in.setSelector(Apps.sendTask(MimeType.of(MimeTypes.PLAIN_TEXT, attachments, this.activity)));
        } else {
            in = Apps.sendMultipleTask(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments);
            in.setSelector(Apps.sendMultipleTask(MimeType.of(MimeTypes.PLAIN_TEXT, attachments, this.activity)));
        }
        // Todo: this is essentially saying "good luck" to the user with wildcard MIME types any
        //  time there's more than one kind of attachment. Specifically targeting apps that support
        //  the intersection of all the attachment types will require a custom chooser impl. Then,
        //  remove MIME type union code.
        return in.putExtra(EXTRA_TEXT, text);
    }

    @Override
    protected void run() {
        this.activity.offerChooser(this, makeIntent(), CoreEntry.SHARE.name);
    }

    @Override
    protected void run(String app) {
        runWithData(app); // TODO: allow to specify app, conversation, and (ideally) person
    }

    @Override
    protected void runWithData(String text) {
        this.activity.offerChooser(this, makeIntent(text), CoreEntry.SHARE.name);
    }

    @Override
    protected void runWithData(String app, String text) {
        runWithData(app + '\n' + text);
    }

    @Override
    public void provide(boolean chosen) {
        if (chosen) succeed(act -> {
            act.finishAndRemoveTask();
            act.suppressPendChime();
        });
        else chime(RESUME);
    }

}
