package net.emilla.widget;

import androidx.annotation.Nullable;

import net.emilla.action.Gadget;
import net.emilla.activity.AssistActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public enum WeekdayWidget implements Gadget {
    COOKED,
;
    private static final int FLAG_MONDAY    = 0x01;
    private static final int FLAG_TUESDAY   = 0x02;
    private static final int FLAG_WEDNESDAY = 0x04;
    private static final int FLAG_THURSDAY  = 0x08;
    private static final int FLAG_FRIDAY    = 0x10;
    private static final int FLAG_SATURDAY  = 0x20;
    private static final int FLAG_SUNDAY    = 0x40;

    private int mFlags = 0;

    public boolean anyAreSet() {
        return mFlags != 0;
    }

    @Nullable
    public ArrayList<Integer> calendarArrayList() {
        if (mFlags == 0) {
            return null;
        }

        var list = new ArrayList<Integer>(Integer.bitCount(mFlags));
        addIfSet(list, FLAG_SUNDAY, Calendar.SUNDAY);
        addIfSet(list, FLAG_MONDAY, Calendar.MONDAY);
        addIfSet(list, FLAG_TUESDAY, Calendar.TUESDAY);
        addIfSet(list, FLAG_WEDNESDAY, Calendar.WEDNESDAY);
        addIfSet(list, FLAG_THURSDAY, Calendar.THURSDAY);
        addIfSet(list, FLAG_FRIDAY, Calendar.FRIDAY);
        addIfSet(list, FLAG_SATURDAY, Calendar.SATURDAY);

        return list;
    }

    private <E> void addIfSet(Collection<E> bag, int flag, E item) {
        if ((mFlags & flag) != 0) {
            bag.add(item);
        }
    }

    @Override
    public void load(AssistActivity act) {
    }

    @Override
    public void unload(AssistActivity act) {
    }
}
