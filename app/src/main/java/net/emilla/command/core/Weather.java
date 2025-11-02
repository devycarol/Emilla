package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;

/*internal*/ final class Weather extends CategoryCommand {

    public static final String ENTRY = "weather";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Apps.categoryTask(CATEGORY_APP_WEATHER));
    }

    /*internal*/ Weather(AssistActivity act) {
        super(act, CoreEntry.WEATHER, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_WEATHER);
        // TODO: figure out what's up with API level stuff here.
    }

    @Override
    protected void run(String location) {
        throw badCommand(R.string.error_unfinished_categorical_app_search); // Todo
    }

}
