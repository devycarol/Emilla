package net.emilla.command;

import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import androidx.annotation.RestrictTo;

import net.emilla.activity.AssistActivity;
import net.emilla.lang.Words;
import net.emilla.struct.trie.TrieMap;

/// This container enables storage of commands in the [TrieMap] data structure.
///
/// It contains a lazily-initialized [EmillaCommand] generated upon being yielded by a [CommandMap].
///
/// Implementations should provide the command's name and aliases, which are used as its phrase-keys
/// for the trie.
///
/// They are required to report whether the command uses any instruction input via the
/// `isPrefixable()` method.
public abstract class CommandYielder implements TrieMap.Value<CommandYielder> {

    private EmillaCommand mCommand = null;

    protected CommandYielder() {}

    /*internal*/ final EmillaCommand command(AssistActivity act) {
        return mCommand == null ? mCommand = makeCommand(act) : mCommand;
    }

    /// The yielder's command with user instruction text applied.
    ///
    /// @param act used to build the command, if necessary.
    /// @param commandPhrase determines substring of the full command to use as the command
    /// instruction, upon having its position set by the TrieMap's `get()` method.
    /// @return the command, with instruction text from `commandPhrase`.
    /*internal*/ final EmillaCommand command(AssistActivity act, Words commandPhrase) {
        EmillaCommand command = command(act);
        if (!isPrefixable()) return command;

        String instruction = commandPhrase.hasRemainingContents()
            ? commandPhrase.remainingContents()
            : null;

        command.instruct(instruction);

        return command;
    }

    /// Generates a new command instance.
    ///
    /// This method is called upon the yielder's first retrieval from a [CommandMap].
    ///
    /// @param act used to build the command.
    /// @return a new command specified by the yielder implementation.
    @RestrictTo(SUBCLASSES)
    protected abstract EmillaCommand makeCommand(AssistActivity act);

    @Override
    public final CommandYielder duplicate(CommandYielder value) {
        return new DuplicateYielder(this, value);
    }

}
