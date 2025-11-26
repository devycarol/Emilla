package net.emilla.util;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import net.emilla.BuildConfig;
import net.emilla.command.app.AppEntry;

public final class Apps {

    public static final String MY_PKG = BuildConfig.APPLICATION_ID;

    public static String entry(String pkg, String cls) {
        return pkg + '/' + cls;
    }

    public static boolean canDo(PackageManager pm, Intent intent) {
        return pm.resolveActivity(intent, 0) != null;
    }

    public static Uri packageUri(String pkg) {
        return Uri.parse("package:" + pkg);
    }

    public static AppEntry[] launchers(PackageManager pm) {
        var launcher = new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER);
        return filter(pm, launcher);
    }

    public static AppEntry[] filter(PackageManager pm, Intent filter) {
        return pm.queryIntentActivities(filter, 0).stream()
            .map(resolveInfo -> AppEntry.from(pm, resolveInfo))
            .sorted()
            .toArray(AppEntry[]::new);
    }

    private Apps() {}

}
