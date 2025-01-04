package net.emilla.command.core;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.DrawableRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exception.EmlaBadCommandException;

public class CateWeather extends CategoryCommand {

    public CateWeather(AssistActivity act, String instruct) {
        super(act, instruct, CATEGORY_APP_WEATHER, R.string.command_weather, R.string.instruction_app);
    }

    @Override @DrawableRes
    public int icon() {
        return R.drawable.ic_weather;
    }

    @Override
    public int imeAction() {
        return EditorInfo.IME_ACTION_GO;
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
