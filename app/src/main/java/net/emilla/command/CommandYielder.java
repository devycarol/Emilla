package net.emilla.command;

import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import androidx.annotation.RestrictTo;

import net.emilla.AssistActivity;
import net.emilla.lang.Words;
import net.emilla.util.trie.TrieMap;

/**
 * <p>
 * This container enables storage of commands in the {@link TrieMap} data structure.</p>
 * <p>
 * It contains a lazily-initialized {@link EmillaCommand} generated upon being yielded by a
 * {@link CommandMap}.</p>
 * <p>
 * Implementations should provide the command's name and aliases, which are used as its phrase-keys
 * for the trie.</p>
 * <p>
 * They are required to report whether the command uses any instruction input via the
 * {@code isPrefixable()} method.</p>
 */
public abstract class CommandYielder implements TrieMap.Value<CommandYielder> {

    private EmillaCommand mCommand;

    final EmillaCommand command(AssistActivity act) {
        return mCommand == null ? mCommand = makeCommand(act) : mCommand;
    }

    /**
     * The yielder's command with user instruction text applied.
     *
     * @param act used to build the command, if necessary.
     * @param commandPhrase determines substring of the full command to use as the command
     *                      instruction, upon having its position set by the TrieMap's {@code get()}
     *                      method.
     * @return the command, with instruction text from {@code commandPhrase}.
     */
    final EmillaCommand command(AssistActivity act, Words commandPhrase) {
        if (!isPrefixable()) return mCommand == null ? mCommand = makeCommand(act) : mCommand;

        String instruction;
        if (commandPhrase.hasRemainingContents()) instruction = commandPhrase.remainingContents();
        else instruction = null;

        return (mCommand == null ? mCommand = makeCommand(act) : mCommand).instruct(instruction);
    }

    /**
     * <p>
     * Generates a new command instance.</p>
     * <p>
     * This method is called upon the yielder's first retrieval from a {@link CommandMap}.</p>
     *
     * @param act used to build the command.
     * @return a new command specified by the yielder implementation.
     */
    @RestrictTo(SUBCLASSES)
    protected abstract EmillaCommand makeCommand(AssistActivity act);

    @Override
    public final CommandYielder duplicate(CommandYielder value) {
        return new DuplicateYielder(this, value);
    }
}
