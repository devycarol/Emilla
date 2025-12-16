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
import net.emilla.chime.Chime;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.util.MimeType;
import net.emilla.util.MimeTypes;

import java.util.ArrayList;

final class Share extends CoreDataCommand implements AppChoiceReceiver {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.send(MimeTypes.PLAIN_TEXT));
    }

    @Deprecated
    private final AssistActivity mActivity;

    /*internal*/ Share(AssistActivity act) {
        super(act, CoreEntry.SHARE, R.string.data_hint_text);

        mActivity = act;

        String entry = CoreEntry.SHARE.name();
        giveGadgets(
            new FileFetcher(act, entry, "*/*"),
            new MediaFetcher(act, entry)
        );
    }

    private static Intent makeIntent(AssistActivity act) {
        ArrayList<Uri> attachments = act.attachments(CoreEntry.SHARE.name());

        if (attachments == null) {
            return Intents.send(MimeTypes.PLAIN_TEXT);
        }
        if (attachments.size() == 1) {
            Intent in = Intents.send(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments.get(0));
            in.setSelector(Intents.send(MimeType.of(attachments, act)));
            return in;
        }
        Intent in = Intents.sendMultiple(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments);
        in.setSelector(Intents.sendMultiple(MimeType.of(attachments, act)));
        return in;
    }

    private static Intent makeIntent(AssistActivity act, String text) {
        ArrayList<Uri> attachments = act.attachments(CoreEntry.SHARE.name());
        if (attachments == null) return Intents.send(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_TEXT, text);

        Intent in;
        if (attachments.size() == 1) {
            in = Intents.send(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments.get(0));
            in.setSelector(Intents.send(MimeType.of(MimeTypes.PLAIN_TEXT, attachments, act)));
        } else {
            in = Intents.sendMultiple(MimeTypes.PLAIN_TEXT).putExtra(EXTRA_STREAM, attachments);
            in.setSelector(Intents.sendMultiple(MimeType.of(MimeTypes.PLAIN_TEXT, attachments, act)));
        }
        // Todo: this is essentially saying "good luck" to the user with wildcard MIME types any
        //  time there's more than one kind of attachment. Specifically targeting apps that support
        //  the intersection of all the attachment types will require a custom chooser impl. Then,
        //  remove MIME type union code.
        return in.putExtra(EXTRA_TEXT, text);
    }

    @Override
    protected void run(AssistActivity act) {
        act.offerChooser(this, makeIntent(act), CoreEntry.SHARE.name);
    }

    @Override
    protected void run(AssistActivity act, String app) {
        runWithData(act, app); // TODO: allow to specify app, conversation, and (ideally) person
    }

    @Override
    public void runWithData(AssistActivity act, String text) {
        act.offerChooser(this, makeIntent(act, text), CoreEntry.SHARE.name);
    }

    @Override
    public void runWithData(AssistActivity act, String app, String text) {
        runWithData(act, app + '\n' + text);
    }

    @Override
    public void provide(boolean chosen) {
        if (chosen) {
            mActivity.succeed(a -> {
                a.finishAndRemoveTask();
                a.suppressChime(Chime.PEND);
            });
        } else {
            mActivity.chime(RESUME);
        }
    }

}
