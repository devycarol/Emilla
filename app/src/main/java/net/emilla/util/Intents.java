package net.emilla.util;

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
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import net.emilla.activity.EmillaActivity;
import net.emilla.command.app.AppEntry;

public final class Intents {

    public static Intent launchApp(AppEntry app) {
        return new Intent(ACTION_MAIN)
            .setPackage(app.pkg)
            .setComponent(app.componentName())
            .addCategory(CATEGORY_LAUNCHER);
    }

    public static Intent categoryTask(String category) {
        return new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).addCategory(category);
    }

    public static Intent send(Uri scheme) {
        return new Intent(ACTION_SEND, scheme);
    }

    public static Intent send(String type) {
        return new Intent(ACTION_SEND).setType(type);
    }

    public static Intent sendToApp(String pkg) {
        return send(MimeTypes.PLAIN_TEXT).setPackage(pkg);
    }

    public static Intent sendMultiple(String type) {
        return new Intent(ACTION_SEND_MULTIPLE).setType(type);
    }

    public static Intent searchToApp(String pkg) {
        return new Intent(ACTION_SEARCH).setPackage(pkg);
    }

    public static Intent view(String scheme) {
        return view(Uri.parse(scheme));
    }

    public static Intent view(Uri uri) {
        return new Intent(ACTION_VIEW, uri);
    }

    public static Intent view(Uri data, String type) {
        return new Intent(ACTION_VIEW).setDataAndType(data, type);
    }

    public static Intent edit(Uri uri) {
        return new Intent(ACTION_EDIT, uri);
    }

    public static Intent edit(Uri data, String type) {
        return new Intent(ACTION_EDIT).setDataAndType(data, type);
    }

    public static Intent insert(String type) {
        return new Intent(ACTION_INSERT).setType(type);
    }

    public static Intent insert(Uri data, String type) {
        return new Intent(ACTION_INSERT).setDataAndType(data, type);
    }

    public static Intent appInfo() {
        return appInfo(Apps.MY_PKG);
    }

    public static Intent appInfo(String pkg) {
        return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Apps.packageUri(pkg));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Intent notificationSettings() {
        return notificationSettings(Apps.MY_PKG);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Intent notificationSettings(String pkg) {
        return new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, pkg);
    }

    public static Intent me(Context ctx, Class<? extends EmillaActivity> cls) {
        return new Intent(ctx, cls);
    }

    public static Intent[] appLaunches(AppList appList) {
        int appCount = appList.size();
        var intents = new Intent[appCount];

        for (int i = 0; i < appCount; ++i) {
            AppEntry app = appList.get(i);
            intents[i] = launchApp(app);
        }

        return intents;
    }

    public static Intent[] appUninstalls(AppList appList, PackageManager pm) {
        int appCount = appList.size();
        var intents = new Intent[appCount];

        for (int i = 0; i < appCount; ++i) {
            AppEntry app = appList.get(i);
            intents[i] = uninstallApp(app.pkg, pm);
        }

        return intents;
    }

    public static Intent uninstallApp(String pkg, PackageManager pm) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return new Intent(ACTION_UNINSTALL_PACKAGE, Apps.packageUri(pkg));
                // Todo: ACTION_UNINSTALL_PACKAGE is deprecated.
            }

            Intent appInfo = appInfo(pkg);
            if (appInfo.resolveActivity(pm) != null) {
                return appInfo;
            }

            var settings = new Intent(Settings.ACTION_SETTINGS);
            if (appInfo.resolveActivity(pm) != null) {
                return settings;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            // fallthrough
        }

        throw new IllegalStateException();
    }

    private Intents() {}

}
