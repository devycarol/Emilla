package net.emilla.command;

import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.struct.trie.HashTrieMap;
import net.emilla.struct.trie.TrieMap;

public final class CommandMap {

    private final TrieMap<String, CommandYielder> mTrieMap = new HashTrieMap<>();
    private final DefaultCommandWrapper.Yielder mDefaultYielder;

    CommandMap(DefaultCommandWrapper.Yielder defaultYielder) {
        mDefaultYielder = defaultYielder;
    }

    void put(String command, CommandYielder yielder) {
        mTrieMap.put(Lang.words(command), yielder);
    }

    /// Tries to map `alias` to a command associated with `commandName`.
    ///
    /// If no such command is in the map, the alias is discarded.
    ///
    /// @param alias name for the custom command.
    /// @param commandName exact name of command to map `alias` to.
    void putCustom(String alias, String commandName) {
        CommandYielder exact = mTrieMap.getExact(Lang.words(commandName));
        if (exact != null) mTrieMap.put(Lang.words(alias), exact);
    }

    public EmillaCommand get(AssistActivity act, String fullCommand) {
        Words words = Lang.words(fullCommand);
        CommandYielder get = mTrieMap.get(words);
        return get == null ? mDefaultYielder.command(act, words) : get.command(act, words);
    }
}
