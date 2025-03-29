package net.emilla.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.util.IndexWindow;
import net.emilla.util.SortedArray;

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
        int size = mData.size();

        IndexWindow exacts = mData.windowMatching(app -> search.compareToIgnoreCase(app.label));
        if (exacts != null) return mData.elements(exacts);

        int len = search.length();
        IndexWindow prefixed = mData.windowMatching(app -> prefixCompare(search, app.label, len));

        ArrayList<AppEntry> filtered = new ArrayList<>(size);
        if (prefixed != null) {
            filtered.addAll(0, mData.elements(prefixed));
            for (int i = 0; i < prefixed.start; ++i) {
                AppEntry app = mData.get(i);
                if (app.label.contains(search)) {
                    filtered.add(app);
                }
            }
            for (int i = prefixed.end; i < size; ++i) {
                AppEntry app = mData.get(i);
                if (app.label.contains(search)) {
                    filtered.add(app);
                }
            }
        } else {
            for (AppEntry app : mData) {
                if (app.label.contains(search)) {
                    filtered.add(app);
                }
            }
        }

        return filtered;
    }

    private static int prefixCompare(String prefix, String s, int prefixLen) {
        int len = s.length();
        if (prefixLen > len) return prefixLen - len;

        for (int i = 0; i < prefixLen; ++i) {
            char a = prefix.charAt(i);
            char b = s.charAt(i);
            if (a != b) {
                a = Character.toUpperCase(a);
                b = Character.toUpperCase(b);
                if (a != b) {
                    a = Character.toLowerCase(a);
                    b = Character.toLowerCase(b);
                    if (a != b) return a - b;
                }
            }
        }

        return 0;
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
