package net.emilla.command.core;

import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_TEXT;

import android.content.Intent;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.FileFetcher;
import net.emilla.action.MediaFetcher;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;
import net.emilla.util.Files.MimeType;

public class Share extends AttachCommand implements AppChoiceReceiver {

    public static final String ENTRY = "share";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_share;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class ShareParams extends CoreDataParams {

        private ShareParams() {
            super(R.string.command_share,
                  R.string.instruction_app,
                  R.drawable.ic_share,
                  R.string.summary_share,
                  R.string.manual_share,
                  R.string.data_hint_share);
        }
    }

    private FileFetcher mFileFetcher;
    private MediaFetcher mMediaFetcher;

    public Share(AssistActivity act, String instruct) {
        super(act, instruct, new ShareParams());
    }

    @Override
    public void init(boolean updateTitle) {
        super.init(updateTitle);

        if (mFileFetcher == null) mFileFetcher = new FileFetcher(activity, this, "*/*");
        giveAction(mFileFetcher);
        if (mMediaFetcher == null) mMediaFetcher = new MediaFetcher(activity, this);
        giveAction(mMediaFetcher);
    }

    @Override
    public void clean() {
        super.clean();

        removeAction(FileFetcher.ID);
        removeAction(MediaFetcher.ID);
    }

    private Intent makeIntent() {
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
        activity.offerChooser(this, makeIntent(), R.string.command_share);
    }

    @Override
    protected void run(String app) {
        runWithData(app); // TODO: allow to specify app, conversation, and (ideally) person
    }

    @Override
    protected void runWithData(String text) {
        activity.offerChooser(this, makeIntent(text), R.string.command_share);
    }

    @Override
    protected void runWithData(String app, String text) {
        runWithData(app + '\n' + text);
    }

    @Override
    public void provide(boolean chosen) {
        if (chosen) succeed(activity::suppressPendingChime);
        else resume();
    }
}
