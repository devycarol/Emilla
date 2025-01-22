package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.content.Intent;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;
import net.emilla.util.Apps;

public class Weather extends CategoryCommand {

    public static final String ENTRY = "weather";
    @StringRes
    public static final int NAME = R.string.command_weather;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_weather;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Weather::new, ENTRY, NAME, ALIASES);
    }

    private static class WeatherParams extends CoreParams {

        private WeatherParams() {
            super(NAME,
                  R.string.instruction_app,
                  R.drawable.ic_weather,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_weather,
                  R.string.manual_weather);
        }
    }

    public Weather(AssistActivity act) {
        super(act, new WeatherParams());
    }

    @Override
    protected Intent makeFilter() {
        return Apps.categoryTask(CATEGORY_APP_WEATHER);
    }

    @Override
    protected void run() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) throw new EmlaBadCommandException(NAME, R.string.error_unfinished_version); // TODO
        super.run();
    }

    @Override
    protected void run(String expression) {
        throw new EmlaBadCommandException(NAME, R.string.error_unfinished_categorical_app_search); // Todo
    }
}
