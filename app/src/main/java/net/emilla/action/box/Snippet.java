package net.emilla.action.box;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;

import net.emilla.R;
import net.emilla.config.SettingVals;
import net.emilla.sort.ItemSearchAdapter;
import net.emilla.sort.SearchItem;
import net.emilla.util.Toasts;

import java.util.function.Consumer;

public final class Snippet extends SearchItem {

    public Snippet(String name) {
        super(name);
    }

    /*internal*/ static ItemSearchAdapter<Snippet> adapter(
        LayoutInflater inflater,
        SharedPreferences prefs,
        Consumer<Snippet> itemClickAction
    ) {
        return new ItemSearchAdapter<Snippet>(
            inflater,
            SettingVals.snippets(prefs),
            itemClickAction,
            Snippet[]::new
        );
    }

    public String text(SharedPreferences prefs) {
        return SettingVals.snippet(prefs, this.displayName);
    }

    public void saveNew(Context ctx, SharedPreferences prefs, String text) {
        SettingVals.addSnippet(prefs, this.displayName, text);
        Toasts.show(ctx, R.string.toast_saved);
    }

    public void overwrite(Context ctx, SharedPreferences prefs, String text) {
        SettingVals.replaceSnippet(prefs, this.displayName, text);
        Toasts.show(ctx, R.string.toast_saved);
    }

    public void delete(Context ctx, SharedPreferences prefs) {
        SettingVals.removeSnippet(prefs, this.displayName);
        var res = ctx.getResources();
        Toasts.show(ctx, res.getString(R.string.toast_snippet_deleted, this.displayName));
    }

}
