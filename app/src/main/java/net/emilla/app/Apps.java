package net.emilla.app;

import static android.content.Intent.ACTION_EDIT;
import static android.content.Intent.ACTION_INSERT;
import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.ACTION_SEARCH;
import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_SEND_MULTIPLE;
import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_LAUNCHER;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import net.emilla.BuildConfig;
import net.emilla.activity.EmillaActivity;

import java.util.List;

public final class Apps {

    public static final String MY_PKG = BuildConfig.APPLICATION_ID;

    public static String entry(String pkg, String cls) {
        return pkg + "/" + cls;
    }

    public static List<ResolveInfo> resolveList(PackageManager pm) {
        return pm.queryIntentActivities(new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER), 0);
    }

    public static List<ResolveInfo> resolveList(PackageManager pm, Intent filter) {
        return pm.queryIntentActivities(filter, 0);
    }

    public static Intent launchIntent(AppEntry app) {
        return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER)
                .setPackage(app.pkg).setComponent(app.componentName());
    }

    public static Intent categoryTask(String category) {
        return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).addCategory(category);
    }

    public static Intent sendTask(Uri scheme) {
        return new Intent(ACTION_SEND, scheme);
    }

    public static Intent sendTask(String type) {
        return new Intent(ACTION_SEND).setType(type);
    }

    public static Intent sendMultipleTask(String type) {
        return new Intent(ACTION_SEND_MULTIPLE).setType(type);
    }

    public static Intent sendToApp(String pkg) {
        return sendTask("text/plain").setPackage(pkg);
    }

    public static Intent searchTask(String pkg) {
        return new Intent(ACTION_SEARCH).setPackage(pkg);
    }

    public static Intent viewTask(String scheme) {
        return viewTask(Uri.parse(scheme));
    }

    public static Intent viewTask(Uri uri) {
        return new Intent(ACTION_VIEW, uri);
    }

    public static Intent viewTask(Uri data, String type) {
        return new Intent(ACTION_VIEW).setDataAndType(data, type);
    }

    public static Intent editTask(Uri uri) {
        return new Intent(ACTION_EDIT, uri);
    }

    public static Intent editTask(Uri data, String type) {
        return new Intent(ACTION_EDIT).setDataAndType(data, type);
    }

    public static Intent insertTask(String type) {
        return new Intent(ACTION_INSERT).setType(type);
    }

    public static Intent insertTask(Uri data, String type) {
        return new Intent(ACTION_INSERT).setDataAndType(data, type);
    }

    public static Intent infoTask() {
        return infoTask(MY_PKG);
    }

    public static Intent infoTask(String pkg) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri(pkg));
    }

    public static Intent meTask(Context ctx, Class<? extends EmillaActivity> cls) {
        return new Intent(ctx, cls);
    }

    public static String[] labels(AppList apps) {
        var labels = new String[apps.size()];
        int i = 0;
        for (AppEntry app : apps) {
            labels[i] = app.label;
            ++i;
        }
        return labels;
    }

    public static Intent[] launches(AppList appList) {
        var intents = new Intent[appList.size()];
        int i = 0;
        for (AppEntry app : appList) {
            intents[i] = launchIntent(app);
            ++i;
        }
        return intents;
    }

    public static Uri pkgUri(String pkg) {
        return Uri.parse("package:" + pkg);
    }

    public static Intent[] uninstalls(AppList appList, PackageManager pm) {
        var intents = new Intent[appList.size()];
        int i = 0;
        for (AppEntry app : appList) {
            intents[i] = uninstallIntent(app.pkg, pm);
            ++i;
        }
        return intents;
    }

    public static Intent uninstallIntent(String pkg, PackageManager pm) {
    try {
        var info = pm.getApplicationInfo(pkg, 0);
        if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return new Intent(ACTION_UNINSTALL_PACKAGE, pkgUri(pkg));
            // Todo: ACTION_UNINSTALL_PACKAGE is deprecated.
        }
        Intent appInfo = infoTask(pkg);
        if (appInfo.resolveActivity(pm) != null) return appInfo;
        var settings = new Intent(Settings.ACTION_SETTINGS);
        if (appInfo.resolveActivity(pm) != null) return settings;
    } catch (PackageManager.NameNotFoundException ignored) {}
        throw new IllegalStateException();
    }

    private Apps() {}
}
