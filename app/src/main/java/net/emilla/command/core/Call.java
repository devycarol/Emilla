package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;
import static net.emilla.chime.Chime.SUCCEED;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.contact.fragment.ContactPhonesFragment;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.util.Features;
import net.emilla.util.Permission;

final class Call extends CoreCommand implements PhoneReceiver {

    public static boolean possible(PackageManager pm) {
        return Features.phone(pm) || Apps.canDo(pm, makeIntent(""));
    }

    private final ContactPhonesFragment mContactsFragment;

    /*internal*/ Call(Context ctx) {
        super(ctx, CoreEntry.CALL, EditorInfo.IME_ACTION_GO);

        mContactsFragment = ContactPhonesFragment.newInstance(false);

        giveGadgets(mContactsFragment);
    }

    @Override
    protected void run(AssistActivity act) {
        Permission.CONTACTS.with(act, () -> tryCall(act));
    }

    private void tryCall(AssistActivity act) {
        String number = mContactsFragment.selectedContacts();
        if (number != null) {
            call(act, number);
        } else {
            act.offerContactPhones(this);
        }
    }

    @Override
    protected void run(AssistActivity act, String nameOrNumber) {
        // todo: conference calls?
        Permission.CALL.with(act, () -> tryCall(act, nameOrNumber));
    }

    private void tryCall(AssistActivity act, String nameOrNumber) {
        String number = mContactsFragment.selectedContacts();

        if (number == null && Contacts.isPhoneNumbers(nameOrNumber)) {
            number = nameOrNumber;
        }

        if (number != null) {
            call(act, number);
        } else {
            var res = act.getResources();
            String msg = res.getString(
                R.string.notice_call_not_number,
                nameOrNumber,
                Contacts.phonewordsToNumbers(nameOrNumber)
            );
            offerDialog(
                act,
                Dialogs.dual(
                    act, CoreEntry.CALL.name,

                    msg, R.string.call_directly,

                    (dlg, which) -> call(act, nameOrNumber)
                )
            );
        }
    }

    private static void call(AssistActivity act, String nameOrNumber) {
        act.suppressChime(SUCCEED);
        appSucceed(act, makeIntent(nameOrNumber));
    }

    private static Intent makeIntent(String number) {
        return new Intent(ACTION_CALL, Uri.parse("tel:" + number));
    }

    @Override
    public void provide(AssistActivity act, String phoneNumber) {
        Permission.CALL.with(act, () -> call(act, phoneNumber));
    }

}
