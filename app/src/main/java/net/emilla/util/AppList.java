package net.emilla.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.command.app.AppEntry;
import net.emilla.sort.ArraySearcher;
import net.emilla.struct.IndexedStruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public final class AppList implements IndexedStruct<AppEntry> {

    private final AppEntry[] mApps;
    private final ArraySearcher<AppEntry> mSearcher;

    public static AppList launchers(PackageManager pm) {
        return new AppList(Apps.resolveList(pm), pm);
    }

    public AppList(PackageManager pm, Intent filter) {
        this(Apps.resolveList(pm, filter), pm);
    }

    private AppList(Collection<? extends ResolveInfo> resolveInfos, PackageManager pm) {
        mApps = resolveInfos.stream()
            .map(resolveInfo -> AppEntry.from(pm, resolveInfo))
            .toArray(AppEntry[]::new);
        // this array is sorted by the ArraySearcher
        mSearcher = new ArraySearcher<AppEntry>(mApps, AppEntry[]::new);
    }

    public IndexedStruct<AppEntry> filter(String search) {
        return mSearcher.search(search);
    }

    @Override
    public AppEntry get(int index) {
        return mApps[index];
    }

    @Override
    public int size() {
        return mApps.length;
    }

    @Override
    public boolean isEmpty() {
        return mApps.length == 0;
    }

    @Override
    public Stream<AppEntry> stream() {
        return Arrays.stream(mApps);
    }

}
