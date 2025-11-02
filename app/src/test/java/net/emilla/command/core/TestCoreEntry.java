package net.emilla.command.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class TestCoreEntry {

    @Test
    public void testCoreEntries() {
        for (var coreEntry : CoreEntry.values()) {
            assertEquals(coreEntry, CoreEntry.of(coreEntry.entry));
        }
    }

}