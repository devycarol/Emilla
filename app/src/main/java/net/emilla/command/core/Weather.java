package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;
import net.emilla.settings.Aliases;

public class Weather extends CategoryCommand {

    public static final String ENTRY = "weather";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_weather;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class WeatherParams extends CoreParams {

        private WeatherParams() {
            super(R.string.command_weather,
                  R.string.instruction_app,
                  R.drawable.ic_weather,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_weather,
                  R.string.manual_weather);
        }
    }

    public Weather(AssistActivity act, String instruct) {
        super(act, instruct, new WeatherParams(), CATEGORY_APP_WEATHER);
    }

    @Override
    protected void run() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) throw new EmlaBadCommandException(R.string.command_weather, R.string.error_unfinished_version); // Todo
        super.run();
    }

    @Override
    protected void run(String expression) {
        throw new EmlaBadCommandException(R.string.command_weather, R.string.error_unfinished_categorical_app_search); // Todo
    }
}
