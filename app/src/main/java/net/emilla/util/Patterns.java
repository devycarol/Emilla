package net.emilla.util;

import android.os.Build;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum Patterns {
    ;

    public static final Pattern TRIMMING_CSV = Pattern.compile(" *, *");
    public static final Pattern TRIMMING_LINES = Pattern.compile(" *\n *");

    public static Stream<String> splitStream(Pattern pattern, CharSequence input) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            ? pattern.splitAsStream(input)
            : Stream.of(pattern.split(input));
    }

}
