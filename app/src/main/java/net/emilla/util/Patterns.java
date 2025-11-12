package net.emilla.util;

import java.util.regex.Pattern;

public final class Patterns {

    public static final Pattern TRIMMING_CSV = Pattern.compile(" *, *");
    public static final Pattern TRIMMING_LINES = Pattern.compile(" *\n *");

    private Patterns() {}

}
