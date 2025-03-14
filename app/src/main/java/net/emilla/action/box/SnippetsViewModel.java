package net.emilla.action.box;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import net.emilla.settings.SettingVals;
import net.emilla.util.ReplaceRange;
import net.emilla.util.SortedArray;

import java.util.Set;

/*internal*/ final class SnippetsViewModel extends ViewModel {

    public final SharedPreferences prefs;
    private final SortedArray<String> mLabels;

    public SnippetsViewModel(SharedPreferences prefs) {
        this.prefs = prefs;
        Set<String> snippets = SettingVals.snippets(prefs);
        mLabels = new SortedArray<>(snippets);
    }

    public SortedArray<String> snippetLabels() {
        return mLabels;
    }

    public String labelAt(int index) {
        return mLabels.get(index);
    }

    public void addSnippet(String label, String text, SnippetAdapter adapter) {
        int pos = mLabels.add(label);
        adapter.notifyItemInserted(pos);
        SettingVals.addSnippet(prefs, label, text);
    }

    public void replaceSnippet(String label, String text, SnippetAdapter adapter) {
        ReplaceRange range = mLabels.replace(label, label);
        if (range == null) return;

        if (!range.singleItem()) adapter.notifyItemMoved(range.start(), range.end());
        adapter.notifyItemChanged(range.start());

        SettingVals.replaceSnippet(prefs, label, text);
    }

    public void removeSnippet(String label, SnippetAdapter adapter) {
        int pos = mLabels.remove(label);
        if (pos >= 0) {
            adapter.notifyItemRemoved(pos);
            SettingVals.removeSnippet(prefs, label);
        }
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final Context ctx;

        public Factory(Context ctx) {
            this.ctx = ctx;
        }

        @Override @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            var prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            return (T) new SnippetsViewModel(prefs);
        }
    }
}
