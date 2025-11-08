package net.emilla.util;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import net.emilla.BuildConfig;
import net.emilla.command.app.AppEntry;
import net.emilla.struct.IndexedStruct;

import java.util.List;

public final class Apps {

    public static final String MY_PKG = BuildConfig.APPLICATION_ID;

    public static String entry(String pkg, String cls) {
        return pkg + '/' + cls;
    }

    public static List<ResolveInfo> resolveList(PackageManager pm) {
        return pm.queryIntentActivities(new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER), 0);
    }

    public static List<ResolveInfo> resolveList(PackageManager pm, Intent filter) {
        return pm.queryIntentActivities(filter, 0);
    }

    public static boolean canDo(PackageManager pm, Intent intent) {
        return pm.resolveActivity(intent, 0) != null;
    }

    public static String[] labels(IndexedStruct<AppEntry> apps) {
        var labels = new String[apps.size()];
        int i = 0;
        for (AppEntry app : apps) {
            labels[i] = app.label;
            ++i;
        }
        return labels;
    }

    public static Uri packageUri(String pkg) {
        return Uri.parse("package:" + pkg);
    }

    private Apps() {}

}
