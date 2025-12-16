package net.emilla.setting;

import android.content.ContentResolver;
import android.content.res.Resources;

import net.emilla.action.box.TriResult;
import net.emilla.lang.Lang;
import net.emilla.trie.PhraseTree;
import net.emilla.trie.PrefixResult;

public final class SettingMap {

    private final PhraseTree<SystemSetting> mSettingMap;

    public SettingMap(Resources res) {
        mSettingMap = Lang.phraseTree(res, SystemSetting[]::new);

//        putAll(res, IntSetting.values());
//        putAll(res, LongSetting.values());
//        putAll(res, FloatSetting.values());
//        putAll(res, StringSetting.values());
        putAll(res, BooleanSetting.values());
    }

    private void putAll(Resources res, SystemSetting[] settings) {
        for (SystemSetting setting : settings) {
            for (String name : setting.names(res)) {
                mSettingMap.put(name, setting, true);
            }
        }
    }

    public TriResult set(Resources res, ContentResolver cr, String value) {
        PrefixResult<SystemSetting, String> result = mSettingMap.get(value);

        SystemSetting setting = result.value();
        if (setting == null) {
            return TriResult.WAITING;
        }

        return setting.set(res, cr, result.leftovers);
    }

}
