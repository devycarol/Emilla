package net.emilla.sort;

import androidx.annotation.CallSuper;

import net.emilla.annotation.internal;
import net.emilla.annotation.open;
import net.emilla.lang.Lang;

public abstract class SearchItem implements Comparable<SearchItem> {

    public final String displayName;
    @internal final String mSearchKey;

    protected SearchItem(String displayName) {
        this.displayName = displayName;
        mSearchKey = Lang.normalize(displayName);
    }

    @CallSuper
    public @open boolean contains(CharSequence normalizedSearch) {
        return mSearchKey.contains(normalizedSearch);
    }

    @Override
    public final int compareTo(SearchItem other) {
        int cmp = mSearchKey.compareTo(other.mSearchKey);
        if (cmp != 0) {
            return cmp;
        }

        return this.displayName.compareTo(other.displayName);
    }

}
