package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;
import net.emilla.util.Intents;

/*internal*/ final class Weather extends CategoryCommand {

    public static final String ENTRY = "weather";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, Intents.categoryTask(CATEGORY_APP_WEATHER));
    }

    /*internal*/ Weather(AssistActivity act) {
        super(act, CoreEntry.WEATHER, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected Intent makeFilter() {
        return Intents.categoryTask(CATEGORY_APP_WEATHER);
        // TODO: figure out what's up with API level stuff here.
    }

    @Override
    protected void run(String location) {
        throw badCommand(R.string.error_unfinished_categorical_app_search); // Todo
    }

}
