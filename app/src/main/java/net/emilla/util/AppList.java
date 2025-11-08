package net.emilla.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.command.app.AppEntry;
import net.emilla.struct.IndexedStruct;
import net.emilla.struct.sort.SearchResult;
import net.emilla.struct.sort.SearchableArray;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class AppList implements IndexedStruct<AppEntry> {

    private final SearchableArray<AppEntry> mApps;

    public static AppList launchers(PackageManager pm) {
        return new AppList(Apps.resolveList(pm), pm);
    }

    public AppList(PackageManager pm, Intent filter) {
        this(Apps.resolveList(pm, filter), pm);
    }

    private AppList(List<? extends ResolveInfo> resolveInfos, PackageManager pm) {
        mApps = new SearchableArray<AppEntry>(resolveInfos, info -> new AppEntry(pm, info));
    }

    public SearchResult<AppEntry> filter(String search) {
        return mApps.filter(search);
    }

    @Override
    public AppEntry get(int index) {
        return mApps.get(index);
    }

    @Override
    public int size() {
        return mApps.size();
    }

    @Override
    public boolean isEmpty() {
        return mApps.isEmpty();
    }

    @Override
    public Stream<AppEntry> stream() {
        return mApps.stream();
    }

    @Override
    public Iterator<AppEntry> iterator() {
        return mApps.iterator();
    }

}
