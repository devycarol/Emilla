package net.emilla.setting;

import android.content.ContentResolver;
import android.content.res.Resources;

import androidx.annotation.Nullable;

import net.emilla.action.box.TriResult;

sealed interface SystemSetting
    permits IntSetting, LongSetting, FloatSetting, StringSetting, BooleanSetting
{
    String[] names(Resources res);
    TriResult set(Resources res, ContentResolver cr, @Nullable String value);
    boolean delete(ContentResolver cr);
}
