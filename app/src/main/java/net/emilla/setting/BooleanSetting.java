package net.emilla.setting;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.provider.Settings;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.box.TriResult;
import net.emilla.lang.Lang;

/*internal*/ enum BooleanSetting implements SystemSetting {
    ;

    private final Namespace mNamespace;
    private final String mKey;
    @ArrayRes
    private final int mNames;

    BooleanSetting(Namespace namespace, String key, @ArrayRes int names) {
        mNamespace = namespace;
        mKey = key;
        mNames = names;
    }

    @Override
    public final String[] names(Resources res) {
        return res.getStringArray(mNames);
    }

    public final boolean get(ContentResolver cr) throws Settings.SettingNotFoundException {
        return mNamespace.getBoolean(cr, mKey);
    }

    @Override
    public final TriResult set(Resources res, ContentResolver cr, @Nullable String value) {
        return setInternal(res, cr, value)
            ? TriResult.SUCCESS
            : TriResult.FAILURE;
    }

    private boolean setInternal(Resources res, ContentResolver cr, @Nullable String value) {
        if (value == null) {
            return toggle(cr);
        }

        value = Lang.normalize(value);
        return value.equals(res.getString(R.string.on)) ? mNamespace.putBoolean(cr, mKey, true)
            : value.equals(res.getString(R.string.off)) && mNamespace.putBoolean(cr, mKey, false);
    }

    private boolean toggle(ContentResolver cr) {
        try {
            boolean toggle = !mNamespace.getBoolean(cr, mKey);
            return mNamespace.putBoolean(cr, mKey, toggle);
        } catch (Settings.SettingNotFoundException | SecurityException e) {
            return false;
        }
    }

    @Override
    public final boolean delete(ContentResolver cr) {
        return mNamespace.delete(cr, mKey);
    }

}
