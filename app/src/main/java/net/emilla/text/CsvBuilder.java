package net.emilla.text;

public final class CsvBuilder {

    private final StringBuilder mBuilder;

    public CsvBuilder(String initialValue) {
        mBuilder = initialValue != null
            ? new StringBuilder(encode(initialValue))
            : new StringBuilder();
    }

    public void append(String... values) {
        for (String value : values) {
            mBuilder.append(',');
            if (value != null) {
                mBuilder.append(encode(value));
            }
        }
    }

    private static String encode(String s) {
        char[] chars = s.toCharArray();
        var sb = new StringBuilder(chars.length);

        for (char c : chars) switch (c) {
            case '\\' -> sb.append("\\\\");
            case ',' -> sb.append("\\,");
            default -> sb.append(c);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }

}
