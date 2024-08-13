package net.emilla.commands;

import static java.lang.Character.isWhitespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;

public class CmdTokens implements Iterable<String>, Iterator<String> {
private final String mCommand;
private final String mLcCommand;
private int mIdx = 0;
private boolean mHasNext;
private String mInstruction;

/**
 * @param command is assumed not to have leading spaces!
 */
CmdTokens(final String command) {
    // Todo lang: non-latin version
    mCommand = command;
    mLcCommand = command.toLowerCase();
    mHasNext = !command.isEmpty();
    mInstruction = mHasNext ? command : null;
}

void nextInstruct() {
    if (mInstruction != null) mInstruction = mHasNext ? mCommand.substring(mIdx) : null;
}

@Nullable
String instruct() {
    return mInstruction;
}

@Override
public boolean hasNext() {
    return mHasNext;
}

@Override @NonNull
public String next() {
    int idx = mIdx;
    final int len = mLcCommand.length();
    do if (++idx >= len) {
        mHasNext = false;
        break;
    } while (!isWhitespace(mLcCommand.charAt(idx)));
    final String token = mLcCommand.substring(mIdx, idx);

    if (mHasNext) do if (++idx >= len) {
        mHasNext = false;
        break;
    } while (isWhitespace(mLcCommand.charAt(idx)));
    mIdx = idx;
    return token;
}

@Override @NonNull
public Iterator<String> iterator() {
    return this;
}
}
