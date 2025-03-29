package net.emilla.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.config.SettingVals;

import java.util.List;

public final class AppEntry implements Comparable<AppEntry> {

    public final String pkg;
    public final String cls;
    public final String label;
    private final AppProperties mMetadata;
    private final int mActionFlags;

    public AppEntry(PackageManager pm, ResolveInfo info) {
        ActivityInfo actInfo = info.activityInfo;
        pkg = actInfo.packageName;
        cls = actInfo.name;
        label = actInfo.loadLabel(pm).toString();
        // TODO: this is the biggest performance bottleneck I've found so far. Look into how the
        //  launcher caches labels for ideas on how to improve the performance of this critical
        //  onCreate task. That is, if they do to begin with..
        mMetadata = AppProperties.of(this);
        mActionFlags = AppActions.of(pm, pkg, mMetadata.actionMask);
    }

    public static String[] labels(List<AppEntry> apps) {
        int size = apps.size();
        String[] labels = new String[size];

        for (int i = 0; i < size; ++i) {
            labels[i] = apps.get(i).label;
        }

        return labels;
    }

    @Override
    public int compareTo(AppEntry that) {
        return this.label.compareToIgnoreCase(that.label);
    }

    public String entry() {
        return Apps.entry(pkg, cls);
    }

    @ArrayRes
    public int aliases() {
        return mMetadata.aliases;
    }

    @StringRes
    public int summary() {
        return mMetadata.summary;
    }

    public boolean usesInstruction() {
        return mActionFlags != 0;
    }

    public boolean hasSend() {
        return (mActionFlags & AppActions.FLAG_SEND) != 0;
    }

    public boolean hasSearch() {
        return (mActionFlags & AppActions.FLAG_SEARCH) != 0;
    }

    public Drawable icon(PackageManager pm) { try {
        return pm.getActivityIcon(componentName());
    } catch (PackageManager.NameNotFoundException e) {
        throw new RuntimeException(e);
    }}

    public ComponentName componentName() {
        return new ComponentName(pkg, cls);
    }

    public boolean commandEnabled(SharedPreferences prefs) {
        return SettingVals.appEnabled(prefs, pkg, cls);
    }

    public Intent launchIntent() {
        return Apps.launchIntent(this);
    }
}
