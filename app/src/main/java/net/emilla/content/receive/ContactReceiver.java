package net.emilla.content.receive;

import android.database.Cursor;

import net.emilla.activity.AssistActivity;

@FunctionalInterface
public interface ContactReceiver extends ResultReceiver {

    void useContact(AssistActivity act, Cursor cur);

}
