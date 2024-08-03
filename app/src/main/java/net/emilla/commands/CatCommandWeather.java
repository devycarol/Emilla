package net.emilla.commands;

import static android.content.Intent.CATEGORY_APP_WEATHER;

import android.os.Build;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.exceptions.EmlaBadCommandException;

public class CatCommandWeather extends CatCommand {
public CatCommandWeather(final AssistActivity act) {
    super(act, R.string.command_weather, R.string.instruction_app, CATEGORY_APP_WEATHER);
}

@Override
protected void noSuchApp() {
    throw new EmlaAppsException("No weather app found for your device.");
}

@Override
public Command cmd() {
    return Command.WEATHER;
}

@Override
public void run() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) throw new EmlaBadCommandException("Sorry! This command doesn't support your Android version yet."); // Todo
    super.run();
}

@Override
public void run(final String expression) {
    throw new EmlaBadCommandException("Sorry! I don't have categorical app search yet."); // Todo
}
}
