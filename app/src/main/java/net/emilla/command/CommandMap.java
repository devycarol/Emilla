package net.emilla.command;

import net.emilla.AssistActivity;
import net.emilla.lang.Lang;
import net.emilla.lang.Words;
import net.emilla.util.trie.HashTrieMap;
import net.emilla.util.trie.TrieMap;

public class CommandMap {

    private final TrieMap<String, CommandYielder> mTrieMap = new HashTrieMap<>();
    private final DefaultCommandWrapper.Yielder mDefaultYielder;

    CommandMap(DefaultCommandWrapper.Yielder defaultYielder) {
        mDefaultYielder = defaultYielder;
    }

    void put(String command, CommandYielder yielder) {
        mTrieMap.put(Lang.words(command), yielder);
    }

    public EmillaCommand get(AssistActivity act, String fullCommand) {
        // TODO: why is this called twice sometimes? Is it because of rich input stuff?
        Words words = Lang.words(fullCommand);
        CommandYielder get = mTrieMap.get(words);
        return get == null ? mDefaultYielder.command(act, words) : get.command(act, words);
    }
}
