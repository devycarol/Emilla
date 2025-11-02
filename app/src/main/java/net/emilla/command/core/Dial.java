package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.apps.Apps;

/*internal*/ final class Dial extends CoreCommand {

    public static final String ENTRY = "dial";

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_DIAL));
    }

    /*internal*/ Dial(AssistActivity act) {
        super(act, CoreEntry.DIAL, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run() {
        appSucceed(new Intent(ACTION_DIAL));
    }

    @Override
    protected void run(String numberOrPhoneword) {
        appSucceed(new Intent(ACTION_DIAL).setData(Uri.parse("tel:" + numberOrPhoneword)));
    }

}
