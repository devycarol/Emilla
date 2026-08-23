package net.emilla.command;

import android.view.inputmethod.EditorInfo;

public enum ImeAction {
    DO(EditorInfo.IME_ACTION_DONE),
    GO(EditorInfo.IME_ACTION_GO),
    SEND(EditorInfo.IME_ACTION_SEND),
    SEARCH(EditorInfo.IME_ACTION_SEARCH),
;
    public final int id;

    ImeAction(int id) {
        this.id = id;
    }
}
