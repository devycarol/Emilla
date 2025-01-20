package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.exception.EmlaFeatureException;
import net.emilla.permission.PermissionReceiver;
import net.emilla.settings.Aliases;
import net.emilla.util.Contacts;
import net.emilla.util.Features;
import net.emilla.util.Permissions;

import java.util.HashMap;

public class Call extends CoreCommand implements PhoneReceiver, PermissionReceiver {

    public static final String ENTRY = "call";
    @StringRes
    public static final int NAME = R.string.command_call;
    @ArrayRes
    public static final int ALIASES = R.array.aliases_call;
    public static final String ALIAS_TEXT_KEY = Aliases.textKey(ENTRY);

    public static Yielder yielder() {
        return new Yielder(true, Call::new, ENTRY, NAME, ALIASES);
    }

    private static class CallParams extends CoreParams {

        private CallParams() {
            super(NAME,
                  R.string.instruction_phone,
                  R.drawable.ic_call,
                  EditorInfo.IME_ACTION_GO,
                  R.string.summary_call,
                  R.string.manual_call);
        }
    }

    private final HashMap<String, String> mPhoneMap;

    public Call(AssistActivity act) {
        super(act, new CallParams());
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
        if (Permissions.contactsFlow(activity, null)) activity.offerContactPhones(this);
    }

    @Override
    protected void run(@NonNull String nameOrNumber) {
        // todo: conference calls?
        // todo: immediate calls to phonewords
        if (!Features.phone(pm())) throw new EmlaFeatureException(NAME, R.string.error_feature_phone);
        // TODO: handle at install - make sure it's not 'sticky' in sharedprefs in case of data
        //  transfer. it shouldn't disable the "command enabled" pref, it should just be its own
        //  element of an "is the command enabled" check similar to HeliBoard's handling in its
        //  "SettingsValues" class.
        if (Permissions.callFlow(activity, this)) {
            activity.suppressSuccessChime();
            appSucceed(convertNameIntent(nameOrNumber));
        }
    }

    @Override
    public void provide(@NonNull String phoneNumber) {
        if (Permissions.callFlow(activity, this)) {
            activity.suppressSuccessChime();
            appSucceed(makeIntent(phoneNumber));
        } else setInstruction(phoneNumber);
    }

    @Override @RequiresApi(api = Build.VERSION_CODES.M)
    public void onGrant() {
        activity.suppressSuccessChime();
        appSucceed(convertNameIntent(instruction()));
    }
}
