package net.emilla.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.command.app.AppEntry;
import net.emilla.struct.sort.SearchResult;
import net.emilla.struct.sort.SearchableArray;

import java.util.Iterator;
import java.util.List;

public final class AppList implements Iterable<AppEntry> {

    private final SearchableArray<AppEntry> mApps;

    public static AppList launchers(PackageManager pm) {
        return new AppList(Apps.resolveList(pm), pm);
    }

    public AppList(PackageManager pm, Intent filter) {
        this(Apps.resolveList(pm, filter), pm);
    }

    private AppList(List<ResolveInfo> resolveInfos, PackageManager pm) {
        mApps = new SearchableArray<AppEntry>(resolveInfos, info -> new AppEntry(pm, info));
    }

    public SearchResult<AppEntry> filter(String search) {
        return mApps.filter(search);
    }

    public AppEntry get(int index) {
        return mApps.get(index);
    }

    public int size() {
        return mApps.size();
    }

    @Override
    public Iterator<AppEntry> iterator() {
        return mApps.iterator();
    }

}
