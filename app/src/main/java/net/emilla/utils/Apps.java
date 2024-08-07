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

import net.emilla.EmillaActivity;

import java.util.List;

public class Apps {
public static final String
    PKG = "net.emilla.nebula",
    PKG_AOSP_CONTACTS = "com.android.contacts",
    PKG_MARKOR = "net.gsantner.markor",
    PKG_FIREFOX = "org.mozilla.firefox",
    PKG_TOR = "org.torproject.torbrowser",
    PKG_SIGNAL = "org.thoughtcrime.securesms",
    PKG_NEWPIPE = "org.schabi.newpipe",
    PKG_TUBULAR = "org.polymorphicshade.tubular",
    PKG_GITHUB = "com.github.android",
    PKG_YOUTUBE = "com.google.android.youtube",
    PKG_DISCORD = "com.discord";

@NonNull
public static List<ResolveInfo> resolveList(final PackageManager pm) {
    return pm.queryIntentActivities(new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER), 0);
}

@NonNull
public static List<ResolveInfo> resolveList(final PackageManager pm, final String category) {
    return pm.queryIntentActivities(categoryIntent(category), 0);
}

public static Intent launchIntent(final String pkg, final String cls) {
    final ComponentName cn = new ComponentName(pkg, cls);
    return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).setPackage(pkg).setComponent(cn)
            .addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent launchIntent(final ActivityInfo info) {
    return launchIntent(info.packageName, info.name);
}

public static Intent newTask(final String action) {
    return new Intent(action).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent newTask(final String action, final Uri data) {
    return new Intent(action, data).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent newTask(final String action, final String type) {
    return new Intent(action).setType(type).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent newTask(final String action, final Uri data, final String type) {
    return new Intent(action).setDataAndType(data, type).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent categoryIntent(final String category) {
    return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).addCategory(category);
}

public static Intent sendTask(final String pkg) {
    // Todo: attachments
    return new Intent(ACTION_SEND).setType("text/plain").setPackage(pkg)
            .addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent searchTask(final String pkg) {
    return new Intent(ACTION_SEARCH).setPackage(pkg).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent viewTask(final String uriStr) {
    return new Intent(ACTION_VIEW, Uri.parse(uriStr)).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static Intent meTask(final Context ctxt, final Class<? extends EmillaActivity> cls) {
    return new Intent(ctxt, cls).addFlags(FLAG_ACTIVITY_NEW_TASK);
}

public static CharSequence[] labels(final List<ResolveInfo> appList, final PackageManager pm) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return appList.parallelStream()
            .map(ri -> ri.activityInfo.loadLabel(pm)).toArray(CharSequence[]::new);
    final CharSequence[] labels = new CharSequence[appList.size()];
    int i = -1;
    for (final ResolveInfo ri : appList) labels[++i] = ri.activityInfo.packageName;
    return labels;
}

public static Intent[] intents(final List<ResolveInfo> appList) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return appList.parallelStream()
            .map(ri -> launchIntent(ri.activityInfo)).toArray(Intent[]::new);
    final Intent[] intents = new Intent[appList.size()];
    int i = -1;
    for (final ResolveInfo ri : appList) intents[++i] = launchIntent(ri.activityInfo);
    return intents;
}

public static Uri pkgUri(final String pkg) {
    return Uri.parse("package:" + pkg);
}

public static Intent uninstallIntent(final String pkg, final PackageManager pm) {
    final ApplicationInfo info;
try {
    info = pm.getApplicationInfo(pkg, 0);
    final boolean uninstallable = (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    if (uninstallable) return newTask(ACTION_UNINSTALL_PACKAGE, pkgUri(pkg));
    // Todo: ACTION_UNINSTALL_PACKAGE is deprecated.
    final Intent appInfo = newTask(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri(pkg));
    if (appInfo.resolveActivity(pm) != null) return appInfo;
    final Intent settings = newTask(Settings.ACTION_SETTINGS);
    if (appInfo.resolveActivity(pm) != null) return settings;
} catch (PackageManager.NameNotFoundException ignored) {}
    return null;
}

public static Intent[] uninstalls(final List<ResolveInfo> appList, final PackageManager pm) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) return appList.parallelStream()
            .map(ri -> uninstallIntent(ri.activityInfo.packageName, pm)).toArray(Intent[]::new);
    final Intent[] intents = new Intent[appList.size()];
    int i = -1;
    for (final ResolveInfo ri : appList) intents[++i] = uninstallIntent(ri.activityInfo.packageName, pm);
    return intents;
}
}
