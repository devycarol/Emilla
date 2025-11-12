package net.emilla.lang;

import static org.junit.Assert.fail;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import net.emilla.command.SubcommandEntry;
import net.emilla.command.app.AppProperties;
import net.emilla.command.core.CoreEntry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;

@RunWith(AndroidJUnit4.class)
public final class TestCommandNamesAndAliases {

    private static final String[] KNOWN_APP_NAMES = new String[0]; // todo

    private static void assertAllNamesMatch(Resources res, Predicate<? super String> predicate) {
        for (var coreEntry : CoreEntry.values()) {
            if (!predicate.test(res.getString(coreEntry.name))) {
                fail();
            }
        }

        for (String name : KNOWN_APP_NAMES) {
            if (!predicate.test(name)) {
                fail();
            }
        }

        for (var subcommandEntry : SubcommandEntry.values()) {
            if (!predicate.test(res.getString(subcommandEntry.name))) {
                fail();
            }
        }
    }

    private static void assertAllAliasesMatch(Resources res, Predicate<? super String> predicate) {
        for (var coreEntry : CoreEntry.values()) {
            if (!Arrays.stream(res.getStringArray(coreEntry.aliases)).allMatch(predicate)) {
                fail();
            }
        }

        for (var appProperties : AppProperties.values()) {
            if (!Arrays.stream(res.getStringArray(appProperties.aliases)).allMatch(predicate)) {
                fail();
            }
        }

        for (var subcommandEntry : SubcommandEntry.values()) {
            if (!Arrays.stream(res.getStringArray(subcommandEntry.aliases)).allMatch(predicate)) {
                fail();
            }
        }
    }

    @Test
    public void testCommandNamesAndAliases() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        var res = appContext.getResources();

        TestLang.withEachLocale(res, TestCommandNamesAndAliases::assertAliasesAreNormal);
        TestLang.withEachLocale(res, TestCommandNamesAndAliases::assertNamesAndAliasesAreDistinct);
    }

    private static void assertAliasesAreNormal(Resources res) {
        assertAllAliasesMatch(res, alias -> alias.equals(Lang.normalize(alias)));
    }

    private static void assertNamesAndAliasesAreDistinct(Resources res) {
        var seen = new HashSet<String>(0);
        assertAllNamesMatch(res, name -> seen.add(Lang.normalize(name)));
        assertAllAliasesMatch(res, seen::add);
    }

}
