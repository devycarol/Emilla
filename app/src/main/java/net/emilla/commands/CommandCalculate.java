package net.emilla.commands;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_APP_CALCULATOR;
import static android.content.Intent.CATEGORY_LAUNCHER;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.exceptions.EmlaAppsException;
import net.emilla.parsing.Calculator;
import net.emilla.utils.Dialogs;

import java.util.List;

public class CommandCalculate extends CoreCommand {
private static final Intent CALC_INTENT = new Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER).addCategory(CATEGORY_APP_CALCULATOR);
private final PackageManager mPm;
private final AlertDialog mCalcChooser;

public CommandCalculate(final AssistActivity act) {
    super(act, R.string.command_calculate, R.string.instruction_calculate);

    mPm = act.getPackageManager();
    mCalcChooser = Dialogs.appChooser(act, mPm, mPm.queryIntentActivities(CALC_INTENT, 0)).create();
    // todo: make this a generic thing in the dialog utils for various app categories :)
}

@Override
public Command cmd() {
    return Command.CALCULATE;
}

@Override
public void run() {
    final List<ResolveInfo> calculators = mPm.queryIntentActivities(CALC_INTENT, 0);
    switch (calculators.size()) {
    case 0 -> throw new EmlaAppsException("No calculator app found on your device."); // TODO: handle at mapping
    case 1 -> succeed(mPm.getLaunchIntentForPackage(calculators.get(0).activityInfo.packageName));
    default -> offer(mCalcChooser);
    // todo: allow to select a default app, ensuring that the preference is cleared if ever the default is no longer installed or a new candidate is installed
    // interestingly, Tasker is included if you remove CATEGORY_LAUNCHER from the intent. i assume this is for its special shortcut functionality.
    // will keep an eye on this. it shouldn't be included in this dialog (by default it "succeeds" to no actual activity, which was confusing to debug lol)
    // but it would be pretty useful to have a toolchain of those special launches ifwhen a dedicated "tasker" command is added.
    }
}

@Override
public void run(final String expression) {
    // todo: AOSP calculator doesn't support piping in text, but maybe others do via ACTION_SEND?
    // i think run-with-instruction should still be a custom implementation, but you could add special support for such apps with a simple AppSendCommand
    give(String.valueOf(Calculator.compute(expression)), true);
}
}
