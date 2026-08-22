package net.emilla.command.core;

import static android.content.Intent.ACTION_CALL;
import static net.emilla.chime.Chime.SUCCEED;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.inputmethod.EditorInfo;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.annotation.internal;
import net.emilla.command.EmillaCommand;
import net.emilla.contact.fragment.ContactPhonesFragment;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.util.Apps;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.util.Features;
import net.emilla.util.Permission;
import net.emilla.wadget.ActionSurface;

final class Call extends EmillaCommand implements PhoneReceiver {
    public static boolean possible(PackageManager pm) {
        return Features.phone(pm) || Apps.canDo(pm, makeIntent(""));
    }

    private final ContactPhonesFragment mContactsFragment;

    @internal Call(Context ctx) {
        super(ctx, CoreEntry.CALL, EditorInfo.IME_ACTION_GO);

        mContactsFragment = ContactPhonesFragment.newInstance(false);

        giveGadgets(mContactsFragment);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act) {
        Permission.CONTACTS.with(act, () -> act.take(tryCall(act)));
        return Feedback.silence();
    }

    private Feedback tryCall(AssistActivity act) {
        String number = mContactsFragment.selectedContacts();
        if (number == null) {
            act.offerContactPhones(this);
            return Feedback.silence();
            // intrinsic 'pend' by the contacts chooser
        }

        return call(act, number);
    }

    @Override
    protected Feedback run(ActionSurface surface, AssistActivity act, String nameOrNumber) {
        // todo: conference calls?
        Permission.CALL.with(act, () -> act.take(tryCall(act, nameOrNumber)));
        return Feedback.silence();
    }

    private Feedback tryCall(AssistActivity act, String nameOrNumber) {
        String number = mContactsFragment.selectedContacts();
        if (number == null && Contacts.isPhoneNumbers(nameOrNumber)) {
            number = nameOrNumber;
        }
        if (number != null) {
            return call(act, number);
        }

        var res = act.getResources();
        String msg = res.getString(
            R.string.notice_call_not_number,
            nameOrNumber,
            Contacts.phonewordsToNumbers(nameOrNumber)
        );
        return Feedback.offer(
            Dialogs.dual(
                act, CoreEntry.CALL.name,

                msg, R.string.call_directly,

                (dlg, which) -> act.take(call(act, nameOrNumber))
            )
        );
    }

    private static Feedback call(AssistActivity act, String nameOrNumber) {
        act.suppressChime(SUCCEED);
        return Feedback.succeed(makeIntent(nameOrNumber));
    }

    private static Intent makeIntent(String number) {
        return new Intent(ACTION_CALL, Uri.parse("tel:" + number));
    }

    @Override
    public void provide(AssistActivity act, String phoneNumber) {
        Permission.CALL.with(act, () -> act.take(call(act, phoneNumber)));
    }
}
