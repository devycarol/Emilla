package net.emilla.util;

import android.util.Log;

public final class Timeit {

    private static final String TAG = Timeit.class.getSimpleName();

    private static long sPrevTime = 0;

    public static long nanos(String label) {
        if (sPrevTime == 0) sPrevTime = System.nanoTime();

        var s = String.valueOf(System.nanoTime() - sPrevTime);
        var sb = new StringBuilder(label).append(": ");
        int start = sb.length();
        sb.append(s);

        for (int i = sb.length() - 3; i > start; i -= 3) {
            sb.insert(i, ',');
        }
        sb.append(" nanoseconds");

        Log.d(TAG, sb.toString());

        return sPrevTime = System.nanoTime();
    }

    private Timeit() {}
}
