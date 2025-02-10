package net.emilla.lang;

import static java.lang.Character.isWhitespace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;

public final class Lines implements Iterable<String> {

//    @Nullable
//    public static String of(Iterable<Object> iterable, boolean haveTrailing) {
//        final var itr = iterable.iterator();
//        if (!itr.hasNext()) return null;
//
//        Object next = itr.next();
//        final var sb = next != null ? new StringBuilder(encodeLine(next)) : new StringBuilder();
//        while (itr.hasNext()) {
//            sb.append('\n');
//
//            next = itr.next();
//            if (next != null) sb.append(encodeLine(next));
//        }
//
//        if (haveTrailing) sb.append('\n');
//
//        return sb.toString();
//    }

    @Nullable
    public static String of(Iterable<String> iterable, boolean haveTrailing) {
        final var itr = iterable.iterator();
        if (!itr.hasNext()) return null;

        String next = itr.next();
        final var sb = next != null ? new StringBuilder(encodeLine(next)) : new StringBuilder();
        while (itr.hasNext()) {
            sb.append('\n');

            next = itr.next();
            if (next != null) sb.append(encodeLine(next));
        }

        if (haveTrailing) sb.append('\n');

        return sb.toString();
    }

//    @Nullable
//    public static String of(Iterable<CharSequence> iterable, boolean haveTrailing) {
//        final var itr = iterable.iterator();
//        if (!itr.hasNext()) return null;
//
//        CharSequence next = itr.next();
//        final var sb = next != null ? new StringBuilder(encodeLine(next)) : new StringBuilder();
//        while (itr.hasNext()) {
//            sb.append('\n');
//
//            next = itr.next();
//            if (next != null) sb.append(encodeLine(next));
//        }
//
//        if (haveTrailing) sb.append('\n');
//
//        return sb.toString();
//    }

//    public static String encodeLine(Object o) {
//        return encodeLine(o.toString());
//    }

    public static String encodeLine(String s) {
        final var sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) appendEscape(sb, c);
        return sb.toString();
    }

//    public static String encodeLine(CharSequence text) {
//        int len = text.length();
//        final var sb = new StringBuilder(len);
//
//        for (int i = 0; i < len; ++i) appendEscape(sb, text.charAt(i));
//
//        return sb.toString();
//    }

    private static void appendEscape(StringBuilder sb, char c) {
        switch (c) {
        case '\\' -> sb.append("\\\\");
        case '\n' -> sb.append("\\\n");
        default -> sb.append(c);
        }
    }

    @Nullable
    public static String trim(String text) {
        final var sb = new StringBuilder(text.length());

        final var itr = new Lines(text, true).iterator();
        if (!itr.hasNext()) return null;

        sb.append(itr.next());
        while (itr.hasNext()) sb.append('\n').append(itr.next());

        return sb.toString();
    }

    private final String mText;
    private final int mStart;
    private final int mLen;
    private final boolean mEnforceTrimming;

    public Lines(String text, boolean enforceTrimming) {
        mText = text;
        mStart = enforceTrimming ? textStart(text) : 0;
        mLen = text.length();
        mEnforceTrimming = enforceTrimming;
    }

    private static int textStart(String s) {
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!isNonLineSpace(c)) return i;
        }
        return s.length();
    }

    private static boolean isNonLineSpace(char c) {
        return isWhitespace(c) && c != '\n';
    }

    @Override @NonNull
    public Iterator<String> iterator() {
        return mEnforceTrimming ? new TrimmingLineIterator() : new LineIterator();
    }

    private sealed class LineIterator implements Iterator<String> permits TrimmingLineIterator {

        protected int pos = mStart;

        @Override
        public final boolean hasNext() {
            return pos < mLen;
        }

        @Override @NonNull
        public final String next() {
            final var sb = new StringBuilder();
            boolean escape = false;

            char c;
            while (pos < mLen) {
                switch (c = mText.charAt(pos)) {
                case '\\' -> {
                    if (escape) {
                        sb.append('\\');
                        escape = false;
                    } else escape = true;
                }
                case '\n' -> {
                    if (!escape) {
                        advanceToNextLine();
                        return makeLine(sb);
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

            // End of text reached
            return makeLine(sb);
        }

        protected void advanceToNextLine() {
            ++pos; // not worrying about spaces
        }

        @NonNull
        protected String makeLine(StringBuilder sb) {
            return sb.toString();
        }
    }

    private final class TrimmingLineIterator extends LineIterator {

        @Override
        protected void advanceToNextLine() {
            do ++pos;
            // advance past the newline
            while (pos < mLen && isNonLineSpace(mText.charAt(pos)));
            // continue advancing as needed
        }

        @Override @NonNull
        protected String makeLine(StringBuilder sb)  {
            int len = sb.length();
            while (len > 0 && isNonLineSpace(sb.charAt(len - 1))) --len;
            sb.setLength(len);
            // remove trailing non-line spaces, leading ones are handled by `advanceToNextLine()`
            // and `textStart(String)`.
            return super.makeLine(sb);
        }
    }
}