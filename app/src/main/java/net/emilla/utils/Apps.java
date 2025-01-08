package net.emilla.utils;

import static android.content.Intent.*;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import net.emilla.BuildConfig;
import net.emilla.EmillaActivity;

import java.util.List;

public class Apps {

    public static final String
            MY_PKG = BuildConfig.APPLICATION_ID,
            PKG_AOSP_CONTACTS = "com.android.contacts",
            PKG_MARKOR = "net.gsantner.markor",
            CLS_MARKOR_MAIN = PKG_MARKOR + ".activity.MainActivity",
            PKG_FIREFOX = "org.mozilla.firefox",
            PKG_TOR = "org.torproject.torbrowser",
            PKG_SIGNAL = "org.thoughtcrime.securesms",
            PKG_NEWPIPE = "org.schabi.newpipe",
            PKG_TUBULAR = "org.polymorphicshade.tubular",
            PKG_GITHUB = "com.github.android",
            PKG_YOUTUBE = "com.google.android.youtube",
            PKG_DISCORD = "com.discord";

    @NonNull
    public static List<ResolveInfo> resolveList(PackageManager pm) {
        return pm.queryIntentActivities(new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER), 0);
    }

    @NonNull
    public static List<ResolveInfo> resolveList(PackageManager pm, String category) {
        return pm.queryIntentActivities(categoryIntent(category), 0);
    }

    public static Intent launchIntent(String pkg, String cls) {
        ComponentName cn = new ComponentName(pkg, cls);
        return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).setPackage(pkg).setComponent(cn);
    }

    public static Intent launchIntent(ActivityInfo info) {
        return launchIntent(info.packageName, info.name);
    }

    public static Intent categoryIntent(String category) {
        return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).addCategory(category);
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

    public static Intent viewTask(Uri uri) {
        return new Intent(ACTION_VIEW, uri);
    }

    public static Intent viewTask(String uriStr) {
        return viewTask(Uri.parse(uriStr));
    }

    public static Intent editTask(Uri uri) {
        return new Intent(ACTION_EDIT, uri);
    }

    public static Intent insertTask(String type) {
        return new Intent(ACTION_INSERT).setType(type);
    }

    public static Intent insertTask(Uri data, String type) {
        return new Intent(ACTION_INSERT).setDataAndType(data, type);
    }

    public static Intent meTask(Context ctx, Class<? extends EmillaActivity> cls) {
        return new Intent(ctx, cls);
    }

    public static CharSequence[] labels(List<ResolveInfo> appList, PackageManager pm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return appList.parallelStream()
                .map(ri -> ri.activityInfo.loadLabel(pm)).toArray(CharSequence[]::new);
        CharSequence[] labels = new CharSequence[appList.size()];
        int i = -1;
        for (ResolveInfo ri : appList) labels[++i] = ri.activityInfo.packageName;
        return labels;
    }

    public static Intent[] intents(List<ResolveInfo> appList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return appList.parallelStream()
                .map(ri -> launchIntent(ri.activityInfo)).toArray(Intent[]::new);
        Intent[] intents = new Intent[appList.size()];
        int i = -1;
        for (ResolveInfo ri : appList) intents[++i] = launchIntent(ri.activityInfo);
        return intents;
    }

    public static Uri pkgUri(String pkg) {
        return Uri.parse("package:" + pkg);
    }

    public static Intent uninstallIntent(String pkg, PackageManager pm) {
        ApplicationInfo info;
    try {
        info = pm.getApplicationInfo(pkg, 0);
        boolean uninstallable = (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
        if (uninstallable) return new Intent(ACTION_UNINSTALL_PACKAGE, pkgUri(pkg));
        // Todo: ACTION_UNINSTALL_PACKAGE is deprecated.
        Uri data = pkgUri(pkg);
        Intent appInfo = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, data);
        if (appInfo.resolveActivity(pm) != null) return appInfo;
        Intent settings = new Intent(Settings.ACTION_SETTINGS);
        if (appInfo.resolveActivity(pm) != null) return settings;
    } catch (PackageManager.NameNotFoundException ignored) {}
        return null;
    }

    public static Intent[] uninstalls(List<ResolveInfo> appList, PackageManager pm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return appList.parallelStream()
                .map(ri -> uninstallIntent(ri.activityInfo.packageName, pm)).toArray(Intent[]::new);
        Intent[] intents = new Intent[appList.size()];
        int i = -1;
        for (ResolveInfo ri : appList) intents[++i] = uninstallIntent(ri.activityInfo.packageName, pm);
        return intents;
    }

    private Apps() {}
}
