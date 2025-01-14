package net.emilla.command.core;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.config.ConfigActivity;
import net.emilla.run.AppSuccess;
import net.emilla.settings.Aliases;
import net.emilla.settings.SettingVals;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;

import java.util.HashMap;

public class Bookmark extends CoreCommand {

    public static final String ENTRY = "bookmark";
    @StringRes
    public static final int NAME = R.string.command_bookmark;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_bookmark;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Bookmark::new, ENTRY, NAME, ALIASES);
    }

    public static final String DFLT_BOOKMARKS = """
            Emilla GitHub, emla, https://github.com/devycarol/Emilla
            Open-source software, OSS, https://en.wikipedia.org/wiki/Open_source_software
            Rick, dQw, https://www.youtube.com/watch?v=dQw4w9WgXcQ""";

    private static class BookmarkParams extends CoreParams {

        private BookmarkParams() {
            super(NAME,
                  R.string.instruction_bookmark,
                  R.drawable.ic_bookmark,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_bookmark,
                  R.string.manual_bookmark);
        }
    }

    private final HashMap<String, Intent> mBookmarkMap = new HashMap<>();
    private final AlertDialog.Builder mBookmarkChooser;
    private final boolean mHasBookmarks; // Todo: remove once search is implemented

    public Bookmark(AssistActivity act) {
        super(act, new BookmarkParams());

        String[] lines = SettingVals.bookmarkCsv(prefs()).split("\\s*\n\\s*");
        String[] labels = new String[lines.length];
        Intent[] intents = new Intent[lines.length];
        int idx = -1;
        for (String line : lines) {
            String[] vals = line.split("\\s*,\\s*");
            if (vals.length > 1) {
                int lastIdx = vals.length - 1;
                Intent view = Apps.viewTask(vals[lastIdx]);
                for (int i = 0; i < lastIdx; ++i) mBookmarkMap.put(vals[i].toLowerCase(), view);
                labels[++idx] = vals[0];
                intents[idx] = view;
            }
        }
        mHasBookmarks = idx != -1;
        if (mHasBookmarks) mBookmarkChooser = Dialogs.withIntents(Dialogs.listBase(act, R.string.dialog_media), act, labels, intents);
        else mBookmarkChooser = Dialogs.dual(act, R.string.dialog_no_bookmarks,
                R.string.dlg_msg_choose_media, R.string.dlg_yes_add_bookmarks,
                (dlg, id) -> act.succeed(new AppSuccess(act, Apps.meTask(act, ConfigActivity.class))));
    }

    @Override
    protected void run() {
        offerDialog(mBookmarkChooser);
    }

    @Override
    protected void run(String bookmark) {
        Intent get = mBookmarkMap.get(bookmark.toLowerCase());
        if (get == null) {
            offerDialog(mBookmarkChooser);
            if (mHasBookmarks) toast(string(R.string.dlg_msg_choose_media));
            return;
        }
        appSucceed(get);
    }
}
