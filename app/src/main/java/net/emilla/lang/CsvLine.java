package net.emilla.lang;

import static java.lang.Character.isWhitespace;

import androidx.annotation.Nullable;

import java.util.Iterator;

public final class CsvLine implements Iterable<String> {

//    @Nullable
//    public static String ofObjects(Iterable<Object> iterable) {
//        var itr = iterable.iterator();
//        if (!itr.hasNext()) return null;
//
//        Object next = itr.next();
//        var sb = next != null ? new StringBuilder(encodeVal(next)) : new StringBuilder();
//        while (itr.hasNext()) {
//            sb.append(',');
//            next = itr.next();
//            if (next != null) {
//                sb.append(encodeVal(next));
//            }
//        }
//
//        return sb.toString();
//    }

    @Nullable
    public static String of(Iterable<String> iterable, boolean useSpaces) {
        var itr = iterable.iterator();
        if (!itr.hasNext()) return null;

        String next = itr.next();
        var sb = next != null ? new StringBuilder(encodeVal(next)) : new StringBuilder();
        while (itr.hasNext()) {
            if (useSpaces) sb.append(", ");
            else sb.append(',');

            next = itr.next();
            if (next != null) {
                sb.append(encodeVal(next));
            }
        }

        return sb.toString();
    }

//    @Nullable
//    public static String ofSequences(Iterable<CharSequence> iterable) {
//        var itr = iterable.iterator();
//        if (!itr.hasNext()) return null;
//
//        CharSequence next = itr.next();
//        var sb = next != null ? new StringBuilder(encodeVal(next)) : new StringBuilder();
//        while (itr.hasNext()) {
//            sb.append(',');
//            next = itr.next();
//            if (next != null) {
//                sb.append(encodeVal(next));
//            }
//        }
//
//        return sb.toString();
//    }

//    public static String encodeVal(Object o) {
//        return encodeVal(o.toString());
//    }

    public static String encodeVal(String s) {
        var sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) appendEscape(sb, c);
        return sb.toString();
    }

//    public static String encodeVal(CharSequence text) {
//        int len = text.length();
//        var sb = new StringBuilder(len);
//
//        for (int i = 0; i < len; ++i) {
//            appendEscape(sb, text.charAt(i));
//        }
//
//        return sb.toString();
//    }

    private static void appendEscape(StringBuilder sb, char c) {
        switch (c) {
        case '\\' -> sb.append("\\\\");
        case ',' -> sb.append("\\,");
        default -> sb.append(c);
        }
    }

    @Nullable
    public static String trim(String line) {
        var sb = new StringBuilder(line.length());

        var itr = new CsvLine(line, true).iterator();
        if (!itr.hasNext()) return null;

        sb.append(itr.next());
        while (itr.hasNext()) {
            sb.append(',').append(itr.next());
        }

        return sb.toString();
    }

    private final String mLine;
    private final int mStart;
    private final int mLen;
    private final boolean mEnforceTrimming;

    public CsvLine(String line, boolean enforceTrimming) {
        mLine = line;
        mStart = enforceTrimming ? valStart(line) : 0;
        mLen = line.length();
        mEnforceTrimming = enforceTrimming;
    }

    private static int valStart(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isWhitespace(s.charAt(i))) {
                return i;
            }
        }
        return s.length();
    }

    @Override
    public Iterator<String> iterator() {
        return mEnforceTrimming ? new TrimmingValIterator() : new ValIterator();
    }

    private sealed class ValIterator implements Iterator<String> permits TrimmingValIterator {

        protected int pos = mStart;

        @Override
        public final boolean hasNext() {
            return pos < mLen;
        }

        @Override @Nullable
        public final String next() {
            var sb = new StringBuilder();
            boolean escape = false;

            char c;
            while (pos < mLen) {
                switch (c = mLine.charAt(pos)) {
                case '\\' -> {
                    if (escape) {
                        sb.append('\\');
                        escape = false;
                    } else {
                        escape = true;
                    }
                }
                case ',' -> {
                    if (!escape) {
                        advanceToNextVal();
                        return makeVal(sb);
                    }

                    sb.append(',');
                    escape = false;
                }
                default -> {
                    sb.append(c);
                    escape = false;
                }}
                ++pos;
            }

            // End of line reached
            return makeVal(sb);
        }

        protected /*open*/ void advanceToNextVal() {
            ++pos; // not worrying about spaces
        }

        @Nullable
        protected /*open*/ String makeVal(StringBuilder sb) {
            return sb.length() > 0 ? sb.toString() : null;
        }
    }

    private final class TrimmingValIterator extends ValIterator {

        @Override
        protected void advanceToNextVal() {
            do ++pos;
            // advance past the comma
            while (pos < mLen && isWhitespace(mLine.charAt(pos)));
            // continue advancing as needed
        }

        @Override @Nullable
        protected String makeVal(StringBuilder sb)  {
            int len = sb.length();
            while (len > 0 && isWhitespace(sb.charAt(len - 1))) {
                --len;
            }
            sb.setLength(len);
            // remove trailing whitespaces, leading ones are handled by `advanceToNextVal()` and
            // `valStart(String)`.
            return super.makeVal(sb);
        }
    }
}