package net.emilla.action.box;

import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import net.emilla.config.SettingVals;
import net.emilla.struct.sort.IndexWindow;
import net.emilla.struct.sort.SortedArray;

import java.util.Set;

/*internal*/ final class SnippetsViewModel extends ViewModel {

    public final SharedPreferences prefs;
    private final SortedArray<String> mLabels;

    private SnippetsViewModel(SharedPreferences prefs) {
        this.prefs = prefs;
        Set<String> snippets = SettingVals.snippets(prefs);
        mLabels = new SortedArray<String>(snippets);
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
        IndexWindow range = mLabels.replace(label, label);
        if (range == null) return;

        if (range.size > 1) adapter.notifyItemMoved(range.start, range.last);
        adapter.notifyItemChanged(range.start);

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

        private final SharedPreferences prefs;

        public Factory(SharedPreferences prefs) {
            this.prefs = prefs;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new SnippetsViewModel(prefs);
        }
    }
}
