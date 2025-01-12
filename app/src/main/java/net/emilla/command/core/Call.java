package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.ContactReceiver;
import net.emilla.exception.EmlaFeatureException;
import net.emilla.settings.Aliases;
import net.emilla.util.Contacts;
import net.emilla.util.Features;
import net.emilla.util.Permissions;

import java.util.HashMap;

public class Call extends CoreCommand implements ContactReceiver {

    public static final String ENTRY = "call";
    @ArrayRes
    public static final int ALIASES = R.array.aliases_call;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    private static class CallParams extends CoreParams {

        private CallParams() {
            super(R.string.command_call,
                  R.string.instruction_phone,
                  R.drawable.ic_call,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_call,
                  R.string.manual_call);
        }
    }

    private final HashMap<String, String> mPhoneMap;

    public Call(AssistActivity act, String instruct) {
        super(act, instruct, new CallParams());
        mPhoneMap = Contacts.mapPhones(act.prefs());
    }

    private static Intent makeIntent(String number) {
        return new Intent(ACTION_CALL, Uri.parse("tel:" + number));
    }

    private Intent convertNameIntent(String nameOrNumber) {
        return makeIntent(Contacts.fromName(nameOrNumber, mPhoneMap));
    }

    @Override
    protected void run() {
        if (Permissions.readContacts(activity, pm())) activity.offerContacts(this);
    }

    @Override
    protected void run(String nameOrNumber) {
        // todo: conference calls?
        // todo: immediate calls to phonewords
        PackageManager pm = pm();
        if (!Features.phone(pm)) throw new EmlaFeatureException(R.string.command_call, R.string.error_feature_phone);
        // TODO: handle at install - make sure it's not 'sticky' in sharedprefs in case of data
        //  transfer. it shouldn't disable the "command enabled" pref, it should just be its own
        //  element of an "is the command enabled" check similar to HeliBoard's handling in its
        //  "SettingsValues" class.
        if (Permissions.phone(activity, pm)) appSucceed(convertNameIntent(nameOrNumber));
        // todo: success chime is cut off by phone call
    }

    @Override
    public void provide(Uri contact) {
        String number = Contacts.phoneNumber(contact, activity.getContentResolver());
        if (number != null) {
            if (Permissions.phone(activity, pm())) appSucceed(makeIntent(number));
            // TODO: queue the retrieved phone number to call if permission is granted
        } else {
            activity.suppressResumeChime();
            fail(R.string.error_no_contact_phone);
            // Todo: exclude these from selection
        }
    }
}
