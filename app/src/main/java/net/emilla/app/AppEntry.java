package net.emilla.app;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.config.SettingVals;

import java.util.List;

public final class AppEntry implements Comparable<AppEntry> {

    public final String pkg;
    public final String cls;
    public final CharSequence label;
    private String mLabel;

    public AppEntry(PackageManager pm, ResolveInfo info) {
        ActivityInfo actInfo = info.activityInfo;
        pkg = actInfo.packageName;
        cls = actInfo.name;
        label = actInfo.loadLabel(pm);
        // TODO: this is the biggest performance bottleneck I've found so far. Look into how the
        //  launcher caches labels for ideas on how to improve the performance of this critical
        //  onCreate task. That is, if they do to begin with..
    }

    public static CharSequence[] labels(List<AppEntry> apps) {
        int size = apps.size();
        CharSequence[] labels = new CharSequence[size];

        for (int i = 0; i < size; ++i) {
            labels[i] = apps.get(i).label;
        }

        return labels;
    }

    @Override
    public int compareTo(AppEntry that) {
        return label().compareToIgnoreCase(that.label());
    }

    public String label() {
        return mLabel != null ? mLabel : (mLabel = label.toString());
    }

    public boolean commandEnabled(SharedPreferences prefs) {
        return SettingVals.appEnabled(prefs, pkg);
    }
}
