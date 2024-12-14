package net.emilla.command;

import static java.lang.Character.isWhitespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;

/**
 * This class tokenizes command strings, making them iterable. The `Latin` version is for languages
 * with space-separated words, and the `Glyph` version is used for character languages that don't
 * regularly use space characters.
 */
public abstract class CmdTokens implements Iterable<String>, Iterator<String> {
private final String mCommand;
protected final String mLcCommand;
protected int mIdx = 0;
protected boolean mHasNext;
private String mInstruction;

private CmdTokens(String command) {
    mCommand = command;
    mLcCommand = command.toLowerCase();
    mHasNext = !command.isEmpty();
    mInstruction = mHasNext ? command : null;
}

/**
 * "Latin" implementation should be used for any language that separates words with space characters.
 */
public static class Latin extends CmdTokens {
/**
 * @param command is assumed not to have leading spaces!
 */
public Latin(String command) {
    super(command);
}

@Override @NonNull
public String next() {
    int idx = mIdx;
    int len = mLcCommand.length();
    do if (++idx >= len) {
        mHasNext = false;
        break;
    } while (!isWhitespace(mLcCommand.charAt(idx)));
    String token = mLcCommand.substring(mIdx, idx);

    if (mHasNext) do if (++idx >= len) {
        mHasNext = false;
        break;
    } while (isWhitespace(mLcCommand.charAt(idx)));
    mIdx = idx;
    return token;
}
}

/**
 * "Glyph" implementation should be used for character languages where words aren't space-separated.
 */
public static class Glyph extends CmdTokens {
public Glyph(String command) {
    super(command);
}

@Override @NonNull
public String next() {
    int codePoint = mLcCommand.codePointAt(mIdx);
    if ((mIdx += Character.charCount(codePoint)) >= mLcCommand.length()) mHasNext = false;
    return new String(Character.toChars(codePoint));
}
}

void nextInstruct() {
    mInstruction = mHasNext ? mCommand.substring(mIdx) : null;
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
public Iterator<String> iterator() {
    return this;
}
}
