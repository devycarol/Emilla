package net.emilla.lang;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.LocaleList;

import java.util.function.Consumer;

public enum TestLang {
    ;

    public static void withEachLocale(Resources res, Consumer<? super Resources> consumer) {
        var originalConf = res.getConfiguration();
        LocaleList locales = originalConf.getLocales();

        consumer.accept(res);

        var displayMetrics = res.getDisplayMetrics();
        int localeCount = locales.size();
        for (int i = 1; i < localeCount; ++i) {
            var conf = new Configuration(originalConf);
            conf.setLocale(locales.get(i));
            res.updateConfiguration(conf, displayMetrics);

            consumer.accept(res);
        }

        res.updateConfiguration(originalConf, displayMetrics);
    }

}
