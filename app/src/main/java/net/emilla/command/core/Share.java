package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;
import static net.emilla.chime.Chimer.RESUME;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.activity.AssistActivity;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.settings.Aliases;
import net.emilla.app.Apps;
import net.emilla.util.Files.MimeType;

import java.util.ArrayList;

public final class Share extends CoreDataCommand implements AppChoiceReceiver {

    public static final String ENTRY = "share";
    @StringRes
    public static final int NAME = R.string.command_share;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_share;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Share::new, ENTRY, NAME, ALIASES);
    }

    private FileFetcher mFileFetcher;
    private MediaFetcher mMediaFetcher;

    public Share(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_app,
              R.drawable.ic_share,
              R.string.summary_share,
              R.string.manual_share,
              R.string.data_hint_text);
    }

    @Override
    protected void onInit() {
        super.onInit();

        if (mFileFetcher == null) mFileFetcher = new FileFetcher(activity, ENTRY, "*/*");
        giveAction(mFileFetcher);
        if (mMediaFetcher == null) mMediaFetcher = new MediaFetcher(activity, ENTRY);
        giveAction(mMediaFetcher);
    }

    @Override
    protected void onClean() {
        super.onClean();

        removeAction(FileFetcher.ID);
        removeAction(MediaFetcher.ID);
    }

    private Intent makeIntent() {
        ArrayList<Uri> attachments = activity.attachments(ENTRY);

        if (attachments == null) return Apps.sendTask("text/plain");
        if (attachments.size() == 1) {
            Intent in = Apps.sendTask("text/plain").putExtra(EXTRA_STREAM, attachments.get(0));
            in.setSelector(Apps.sendTask(MimeType.of(attachments, activity)));
            return in;
        }
        Intent in = Apps.sendMultipleTask("text/plain").putExtra(EXTRA_STREAM, attachments);
        in.setSelector(Apps.sendMultipleTask(MimeType.of(attachments, activity)));
        return in;
    }

    private Intent makeIntent(String text) {
        ArrayList<Uri> attachments = activity.attachments(ENTRY);

        if (attachments == null) return Apps.sendTask("text/plain").putExtra(EXTRA_TEXT, text);
        Intent in;
        if (attachments.size() == 1) {
            in = Apps.sendTask("text/plain").putExtra(EXTRA_STREAM, attachments.get(0));
            in.setSelector(Apps.sendTask(MimeType.of("text/plain", attachments, activity)));
        } else {
            in = Apps.sendMultipleTask("text/plain").putExtra(EXTRA_STREAM, attachments);
            in.setSelector(Apps.sendMultipleTask(MimeType.of("text/plain", attachments, activity)));
        }
        // Todo: this is essentially saying "good luck" to the user with wildcard MIME types any
        //  time there's more than one kind of attachment. Specifically targeting apps that support
        //  the intersection of all the attachment types will require a custom chooser impl. Then,
        //  remove MIME type union code.
        return in.putExtra(EXTRA_TEXT, text);
    }

    @Override
    protected void run() {
        activity.offerChooser(this, makeIntent(), NAME);
    }

    @Override
    protected void run(String app) {
        runWithData(app); // TODO: allow to specify app, conversation, and (ideally) person
    }

    @Override
    protected void runWithData(String text) {
        activity.offerChooser(this, makeIntent(text), NAME);
    }

    @Override
    protected void runWithData(String app, String text) {
        runWithData(app + '\n' + text);
    }

    @Override
    public void provide(boolean chosen) {
        if (chosen) succeed(() -> {
            activity.finishAndRemoveTask();
            activity.suppressPendChime();
        });
        else chime(RESUME);
    }
}
