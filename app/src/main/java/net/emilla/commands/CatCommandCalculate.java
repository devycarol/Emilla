package net.emilla.commands;

import static android.content.Intent.CATEGORY_APP_CALCULATOR;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.parsing.Calculator;

public class CatCommandCalculate extends CatCommand {
public CatCommandCalculate(final AssistActivity act) {
    super(act, R.string.command_calculate, R.string.instruction_calculate, CATEGORY_APP_CALCULATOR);
}

@Override
protected void noSuchApp() {
    throw new EmlaAppsException("No calculator app found for your device.");
}

@Override
public Command cmd() {
    return Command.CALCULATE;
}

@Override
public void run(final String expression) {
    // todo: AOSP calculator doesn't support piping in text, but maybe others do via ACTION_SEND?
    // i think run-with-instruction should still be a custom implementation, but you could add special support for such apps with a simple AppSendCommand
    give(String.valueOf(Calculator.compute(expression)), true);
}
}
