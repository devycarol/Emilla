package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.content.Intent;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.app.Apps;
import net.emilla.settings.Aliases;

public final class Weather extends CategoryCommand {

    public static final String ENTRY = "weather";
    @StringRes
    public static final int NAME = R.string.command_weather;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_weather;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Weather::new, ENTRY, NAME, ALIASES);
    }

    private Weather(AssistActivity act) {
        super(act, NAME,
              R.string.instruction_app,
              R.drawable.ic_weather,
              R.string.summary_weather,
              R.string.manual_weather,
              EditorInfo.IME_ACTION_GO);
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
