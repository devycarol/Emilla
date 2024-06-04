package net.emilla.commands;

import android.app.AlertDialog;
import android.content.Intent;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.config.ConfigActivity;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.utils.Apps;
import net.emilla.utils.Dialogs;

import java.util.HashMap;

public class CommandView extends CoreCommand {
public static final String DFLT_MEDIA = "Emilla GitHub, emla, https://github.com/devycarol/Emilla\n" +
        "Open-source software, OSS, https://en.wikipedia.org/wiki/Open_source_software\n" +
        "Rick, dQw, https://www.youtube.com/watch?v=dQw4w9WgXcQ";

private final HashMap<String, Intent> mBookmarkMap = new HashMap<>();
private final AlertDialog mBookmarkChooser;
private final boolean mHasBookmarks; // Todo: remove once search is implemented

public CommandView(final AssistActivity act) {
    super(act, R.string.command_view, R.string.instruction_view);

    final String[] lines = act.mediaCsv().split("\\s*\n\\s*");
    final CharSequence[] labels = new String[lines.length];
    final Intent[] intents = new Intent[lines.length];
    int idx = -1;
    for (final String line : lines) {
        final String[] vals = line.split("\\s*,\\s*");
        if (vals.length > 1) {
            final int lastIdx = vals.length - 1;
            final Intent view = Apps.viewTask(vals[lastIdx]);
            for (int i = 0; i < lastIdx; ++i) mBookmarkMap.put(vals[i].toLowerCase(), view);
            labels[++idx] = vals[0];
            intents[idx] = view;
        }
    }
    mHasBookmarks = idx != -1;
    if (mHasBookmarks) mBookmarkChooser = Dialogs.withIntents(Dialogs.base(act, R.string.dialog_media), act, labels, intents).create();
    else mBookmarkChooser = Dialogs.okCancelMsg(act, R.string.dialog_no_bookmarks,
            R.string.dlg_msg_choose_media, R.string.dlg_yes_add_bookmarks,
            (dialog, id) -> act.succeed(Apps.meTask(act, ConfigActivity.class))).create();
}

@Override
public Command cmd() {
    return Command.VIEW;
}

@Override
public void run() {
    offer(mBookmarkChooser);
}

@Override
public void run(final String media) {
    final Intent get = mBookmarkMap.get(media.toLowerCase());
    if (get == null) {
        offer(mBookmarkChooser); // TODO: rare as it may be, this is not yet resolve-safe as is below
        if (mHasBookmarks) toast(resources().getString(R.string.dlg_msg_choose_media), false);
        return;
    }
    if (get.resolveActivity(packageManager()) == null) throw new EmlaAppsException("No app found to view media."); // todo handle at mapping
    succeed(get);
}
}
