package net.emilla.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.util.Chars;
import net.emilla.util.IndexWindow;
import net.emilla.util.SortedArray;
import net.emilla.util.Strings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AppList implements Iterable<AppEntry> {

    private final SortedArray<AppEntry> mData;

    public static AppList launchers(PackageManager pm) {
        return new AppList(Apps.resolveList(pm), pm);
    }

    public AppList(PackageManager pm, Intent filter) {
        this(Apps.resolveList(pm, filter), pm);
    }

    private AppList(List<ResolveInfo> resolveInfos, PackageManager pm) {
        mData = new SortedArray<>(resolveInfos, info -> new AppEntry(pm, info));
    }

    public List<AppEntry> filter(String search) {
        IndexWindow exacts = mData.windowMatching(new ExactSearcher(search));
        if (exacts != null) return mData.elements(exacts);

        IndexWindow prefixed = mData.windowMatching(new PrefixSearcher(search));

        int size = mData.size();
        var filtered = new ArrayList<AppEntry>(size);
        if (prefixed != null) {
            filtered.addAll(0, mData.elements(prefixed));
            for (int i = 0; i < prefixed.start; ++i) {
                AppEntry app = mData.get(i);
                if (Strings.containsIgnoreCase(app.label, search)) {
                    filtered.add(app);
                }
            }
            for (int i = prefixed.end; i < size; ++i) {
                AppEntry app = mData.get(i);
                if (Strings.containsIgnoreCase(app.label, search)) {
                    filtered.add(app);
                }
            }
        } else {
            for (AppEntry app : mData) {
                if (Strings.containsIgnoreCase(app.label, search)) {
                    filtered.add(app);
                }
            }
        }

        return filtered;
    }

    private static final class ExactSearcher implements Comparable<AppEntry> {

        private final String mSearch;

        public ExactSearcher(String search) {
            mSearch = search;
        }

        @Override
        public int compareTo(AppEntry app) {
            return mSearch.compareToIgnoreCase(app.label);
        }
    }

    private static final class PrefixSearcher implements Comparable<AppEntry> {

        private final char[] mPrefix;
        private final int mLength;

        public PrefixSearcher(String prefix) {
            mPrefix = prefix.toCharArray();
            mLength = prefix.length();
        }

        @Override
        public int compareTo(AppEntry app) {
            int len = app.label.length();
            if (mLength > len) return mLength - len;

            char[] label = app.label.toCharArray();
            for (int i = 0; i < mLength; ++i) {
                int cmp = Chars.compareIgnoreCase(mPrefix[i], label[i]);
                if (cmp != 0) return cmp;
            }

            return 0;
        }
    }

    public AppEntry get(int index) {
        return mData.get(index);
    }

    public int size() {
        return mData.size();
    }

    @Override
    public Iterator<AppEntry> iterator() {
        return mData.iterator();
    }
}
