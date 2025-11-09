package net.emilla.command;

import androidx.annotation.Nullable;

import net.emilla.activity.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.struct.trie.HashTrieMap;
import net.emilla.struct.trie.TrieMap;

public final class CommandMap {

    private final TrieMap<String, CommandYielder> mTrieMap = new HashTrieMap<>();
    private final CommandYielder mDefaultYielder;

    /*internal*/ CommandMap(CommandYielder defaultYielder) {
        mDefaultYielder = defaultYielder;
    }

    /*internal*/ void put(String command, CommandYielder yielder) {
        mTrieMap.put(Lang.words(command), yielder);
    }

    /// Tries to map `alias` to a command associated with `commandName`.
    ///
    /// If no such command is in the map, the alias is discarded.
    ///
    /// @param alias name for the custom command.
    /// @param commandName exact name of command to map `alias` to.
    /*internal*/ void putCustom(String alias, String commandName) {
        CommandYielder exact = mTrieMap.getExact(Lang.words(commandName));
        if (exact != null) {
            mTrieMap.put(Lang.words(alias), exact);
        }
    }

    @Nullable
    public EmillaCommand get(AssistActivity act, String fullCommand) {
        Words words = Lang.words(fullCommand);
        CommandYielder get = mTrieMap.get(words);
        return get != null ? get.command(act, words) : null;
    }

    public EmillaCommand getDefault(AssistActivity act) {
        return mDefaultYielder.command(act);
    }

}
