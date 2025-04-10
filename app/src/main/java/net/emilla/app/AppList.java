package net.emilla.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.struct.sort.SearchableArray;

import java.util.Iterator;
import java.util.List;

public final class AppList implements Iterable<AppEntry> {

    private final SearchableArray<AppEntry> mData;

    public static AppList launchers(PackageManager pm) {
        return new AppList(Apps.resolveList(pm), pm);
    }

    public AppList(PackageManager pm, Intent filter) {
        this(Apps.resolveList(pm, filter), pm);
    }

    private AppList(List<ResolveInfo> resolveInfos, PackageManager pm) {
        mData = new SearchableArray<>(resolveInfos, info -> new AppEntry(pm, info));
    }

    public List<AppEntry> filter(String search) {
        return mData.filter(search);
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
