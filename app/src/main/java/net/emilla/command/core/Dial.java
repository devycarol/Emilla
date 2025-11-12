package net.emilla.command.core;

import static android.content.Intent.ACTION_DIAL;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.activity.AssistActivity;
import net.emilla.util.Apps;

/*internal*/ final class Dial extends CoreCommand {

    public static boolean possible(PackageManager pm) {
        return Apps.canDo(pm, new Intent(ACTION_DIAL));
    }

    /*internal*/ Dial(Context ctx) {
        super(ctx, CoreEntry.DIAL, EditorInfo.IME_ACTION_GO);
    }

    @Override
    protected void run(AssistActivity act) {
        appSucceed(act, new Intent(ACTION_DIAL));
    }

    @Override
    protected void run(AssistActivity act, String numberOrPhoneword) {
        appSucceed(act, new Intent(ACTION_DIAL).setData(Uri.parse("tel:" + numberOrPhoneword)));
    }

}
