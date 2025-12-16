package net.emilla.setting;

import android.content.ContentResolver;
import android.content.res.Resources;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;

import net.emilla.action.box.TriResult;
import net.emilla.lang.Lang;

/*internal*/ enum FloatSetting implements SystemSetting {
    PLACEHOLDER(null, null, 0) {
        @Override
        boolean isValid(float value) {
            return false;
        }
    };

    private final Namespace mNamespace;
    private final String mKey;
    @ArrayRes
    private final int mNames;

    FloatSetting(Namespace namespace, String key, @ArrayRes int names) {
        mNamespace = namespace;
        mKey = key;
        mNames = names;
    }

    abstract boolean isValid(float value);

    @Override
    public final String[] names(Resources res) {
        return res.getStringArray(mNames);
    }

    @Override
    public final TriResult set(Resources res, ContentResolver cr, @Nullable String value) {
        if (value == null) {
            return TriResult.WAITING;
        }

        float f = Lang.parseFloat(value);
        return isValid(f) && mNamespace.putFloat(cr, mKey, f)
            ? TriResult.SUCCESS
            : TriResult.FAILURE;
    }

    @Override
    public final boolean delete(ContentResolver cr) {
        return mNamespace.delete(cr, mKey);
    }

}
