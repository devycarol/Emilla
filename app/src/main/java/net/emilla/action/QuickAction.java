package net.emilla.action;

import androidx.annotation.DrawableRes;

public interface QuickAction {

    String // Preference keys
           PREF_NO_COMMAND = "action_no_command",
           PREF_LONG_SUBMIT = "action_long_submit",
           PREF_DOUBLE_ASSIST = "action_double_assist",
           PREF_MENU_KEY = "action_menu";

    String // Action values
           NONE = "none",
           FLASHLIGHT = "torch",
           ASSISTANT_SETTINGS = "config",
           SELECT_ALL = "select_all";

    @DrawableRes
    int icon();
    void perform();
}
