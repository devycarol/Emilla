package net.emilla.action;

import android.content.res.Resources;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

public interface QuickAction {

    // Preference keys
    String PREF_NO_COMMAND = "action_no_command";
    String PREF_LONG_SUBMIT = "action_long_submit";
    String PREF_DOUBLE_ASSIST = "action_double_assist";
    String PREF_MENU_KEY = "action_menu";

    // Action values
    String NONE = "none";
    String FLASHLIGHT = "torch";
    String ASSISTANT_SETTINGS = "config";
    String CURSOR_START = "cursor_start";
    String SELECT_ALL = "select_all";
    String PLAY_PAUSE = "play_pause";
    String HELP = "help";

    @IdRes
    int id();
    @DrawableRes
    int icon();
    String label(Resources res);
    String description(Resources res);
    void perform();
}
