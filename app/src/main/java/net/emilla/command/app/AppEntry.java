package net.emilla.command.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.command.Params;
import net.emilla.config.Aliases;
import net.emilla.config.SettingVals;
import net.emilla.lang.Lang;
import net.emilla.sort.SearchItem;
import net.emilla.struct.IndexedStruct;
import net.emilla.util.Apps;
import net.emilla.util.Intents;
import net.emilla.widget.ActionIcon;
import net.emilla.widget.AppIcon;
import net.emilla.widget.SymbolIcon;

import java.util.Set;

public final class AppEntry extends SearchItem implements Params {
    public static String[] labels(IndexedStruct<AppEntry> apps) {
        int size = apps.size();
        var labels = new String[size];

        for (int i = 0; i < size; ++i) {
            labels[i] = apps.get(i).displayName;
        }

        return labels;
    }

    public static AppEntry from(PackageManager pm, ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        return new AppEntry(
            pm,
            activityInfo.loadLabel(pm).toString(),
            activityInfo.packageName,
            activityInfo.name
        );
    }

    public final String pkg;
    public final String cls;
    @Nullable
    public final AppProperties properties;
    public final AppActions actions;

    private AppEntry(PackageManager pm, String label, String packageName, String name) {
        super(label);

        pkg = packageName;
        cls = name;
        // TODO: this is the biggest performance bottleneck I've found so far. Look into how the
        //  launcher caches labels for ideas on how to improve the performance of this critical
        //  onCreate task. That is, if they do to begin with..
        properties = AppProperties.of(pkg, cls);
        actions = new AppActions(pm, pkg, properties);
    }

    public String entry() {
        return Apps.entry(pkg, cls);
    }

    @Override
    public String name(Resources res) {
        return displayName;
    }

    @Override
    public String title(Resources res) {
        return Lang.colonConcat(res, displayName, instruction());
    }

    @StringRes
    private int instruction() {
        if (properties != null) {
            int instruction = properties.instruction;
            if (instruction != 0) {
                return instruction;
            }
        }
        return actions.instruction();
    }

    @Override
    public ActionIcon actionIcon(Context ctx) {
        Drawable icon = icon(ctx);
        if (icon == null) {
            return new SymbolIcon(R.drawable.ic_app);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && icon instanceof AdaptiveIconDrawable adaptive) {
            Drawable monochrome = adaptive.getMonochrome();
            if (monochrome != null) {
                monochrome.setTint(attribute(ctx, com.google.android.material.R.attr.colorOnSurface));
                var inset = new InsetDrawable(monochrome, -0.5f);
                return new SymbolIcon(inset);
            }
        }

        return new AppIcon(icon);
    }

    private static int attribute(Context ctx, @AttrRes int attribute) {
        var value = new TypedValue();
        if (ctx.getTheme().resolveAttribute(attribute, value, true)) {
            return value.data;
        }
        return Color.BLACK;
    }

    @Nullable
    public Drawable icon(Context ctx) {
        try {
            var pm = ctx.getPackageManager();
            return pm.getActivityIcon(componentName());
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean isProperNoun() {
        return true;
    }

    @Nullable
    public Set<String> aliases(SharedPreferences prefs, Resources res) {
        return Aliases.appSet(prefs, res, this);
    }

    @StringRes
    public int summary() {
        if (properties != null) {
            return properties.summary;
        }
        return actions.summary();
    }

    public ComponentName componentName() {
        return new ComponentName(pkg, cls);
    }

    public AppYielder yielder() {
        return new AppYielder(this);
    }

    public boolean isEnabled(SharedPreferences prefs) {
        return SettingVals.appEnabled(prefs, this);
    }

    public Intent launchIntent() {
        return Intents.launchApp(this);
    }
}
