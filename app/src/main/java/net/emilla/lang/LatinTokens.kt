package net.emilla.lang

import net.emilla.lang.LatinToken.Letter
import net.emilla.lang.LatinToken.Word
import net.emilla.util.Chars
import net.emilla.util.indexOfNonSpace
import net.emilla.util.substring

sealed interface LatinToken {
    data class Letter(
        internal val requireSpaceBefore: Boolean,
        private val char: Char,
        private val ignoreCase: Boolean
    ) : LatinToken {
        fun matches(c: Char) = c.equals(char, ignoreCase)
    }

    data class Word(
        internal val requireSpaceBefore: Boolean,
        private val string: String,
        private val ignoreCase: Boolean
    ) : LatinToken {
        internal val length get() = string.length

        fun matches(s: String) = s.equals(string, ignoreCase)
    }
}

class LatinTokens(s: String) {

    private val chars = s.toCharArray()
    private val length = chars.size
    private var pos = chars.indexOfNonSpace()

    fun hasNext() = pos < length
    fun finished() = pos == length
    private fun ensureNext() = check(pos < length)

    private inline fun ensureNext(predicate: (Char) -> Boolean) {
        check(pos < length && predicate(chars[pos]))
    }

    fun peek(): Char {
        ensureNext()
        return chars[pos]
    }

    fun nextChar(): Char {
        ensureNext()
        val c = chars[pos]

        advanceImmediate()
        return c
    }

    fun nextToken(): String {
        val start = scan { !Character.isWhitespace(it) }
        val s = chars.substring(start, pos)

        advanceImmediate()
        return s
    }

    fun nextWord(): String {
        val start = scan(Char::isLetter)
        val s = chars.substring(start, pos)

        advance()
        return s
    }

    fun nextInteger(): Long {
        val start = scan(Chars::isSignOrDigit, Char::isDigit)
        val s = chars.substring(start, pos)

        advance()
        return try {
            s.toLong()
        } catch (_: NumberFormatException) {
            throw IllegalStateException()
        }
    }

    fun nextNumber(): Double {
        val start = scan(Chars::isSignOrNumberChar, Chars::isNumberChar)
        val s = chars.substring(start, pos)

        advance()
        return try {
            s.toDouble()
        } catch (_: NumberFormatException) {
            throw IllegalStateException()
        }
    }

    /**
     * Make sure no token in the sequence is a case-insensitive prefix of a subsequent one!
     */
    fun skipFirst(vararg sequence: LatinToken) {
        if (pos == length) return

        for (token in sequence) when (token) {
            is Letter -> {
                if (token.matches(chars[pos])) {
                    if (token.requireSpaceBefore) ensureSpaceBefore()

                    advanceImmediate()
                    return
                }
            }
            is Word -> {
                val end: Int = pos + token.length
                if (end <= length && token.matches(chars.substring(pos, end))) {
                    if (token.requireSpaceBefore) ensureSpaceBefore()

                    advanceFrom(end)
                    return
                }
            }
        }
    }

    /**
     * Make sure no token in the sequence is a case-insensitive prefix of a subsequent one!
     */
    fun nextOf(vararg sequence: LatinToken): String {
        check(pos < length)

        for (token in sequence) when (token) {
            is Letter -> {
                val c = chars[pos]
                if (token.matches(c)) {
                    if (token.requireSpaceBefore) ensureSpaceBefore()

                    advanceImmediate()
                    return c.toString()
                }
            }
            is Word -> {
                val end: Int = pos + token.length
                if (end > length) continue

                val sector = chars.substring(pos, end)
                if (token.matches(sector)) {
                    if (token.requireSpaceBefore) ensureSpaceBefore()

                    advanceFrom(end)
                    return sector
                }
            }
        }

        throw IllegalStateException()
    }

    private fun ensureSpaceBefore() {
        check(chars[pos - 1].isWhitespace())
    }

    private inline fun scan(predicate: (Char) -> Boolean) = scan(predicate, predicate)
    private inline fun scan(
        precondition: (c: Char) -> Boolean,
        predicate: (c: Char) -> Boolean
    ): Int {
        ensureNext(precondition)
        val start = pos

        do ++pos
        while (pos < length && predicate(chars[pos]))

        return start
    }

    private fun advanceImmediate() {
        do ++pos
        while (pos < length && chars[pos].isWhitespace())
    }

    private fun advance() {
        while (pos < length && chars[pos].isWhitespace()) {
            ++pos
        }
    }

    private fun advanceFrom(start: Int) {
        pos = start
        advance()
    }

    fun requireFinished() = check(pos == length)
}
