package net.emilla.command.core;

import static android.content.Intent.ACTION_SENDTO;
import static android.content.Intent.EXTRA_SUBJECT;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;

import net.emilla.AssistActivity;
import net.emilla.R;
import net.emilla.action.field.FieldToggle;
import net.emilla.action.field.SubjectField;
import net.emilla.exception.EmlaFeatureException;
import net.emilla.utils.Contacts;
import net.emilla.utils.Features;

import java.util.HashMap;

public class Sms extends CoreDataCommand {

    public static final String ENTRY = "sms";

    private static class SmsParams extends CoreDataParams {

        private SmsParams() {
            super(R.string.command_sms,
                  R.string.instruction_phone,
                  false, // the initials "SMS" shouldn't be lowercased
                  R.drawable.ic_sms,
                  R.string.data_hint_message);
        }
    }

    private final Intent mIntent = new Intent(ACTION_SENDTO, Uri.parse("smsto:"));
    private final HashMap<String, String> mPhoneMap;
    private FieldToggle mSubjectToggle;

    @Override @ArrayRes
    public int details() {
        return R.array.details_sms;
    }

    public Sms(AssistActivity act, String instruct) {
        super(act, instruct, new SmsParams());
        mPhoneMap = Contacts.mapPhones(act.prefs());
    }

    @Override
    public void init(boolean updateTitle) {
        super.init(updateTitle);

        if (mSubjectToggle == null) mSubjectToggle = new SubjectField(activity);
        else if (mSubjectToggle.activated()) reshowField(SubjectField.FIELD_ID);
        giveAction(mSubjectToggle);
        // Todo: attachments
    }

    @Override
    public void clean() {
        super.clean();

        removeAction(SubjectField.ACTION_ID);
        hideField(SubjectField.FIELD_ID);
    }

    private void launchMessenger(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Features.sms(pm())) {
            throw new EmlaFeatureException(R.string.command_sms, R.string.error_feature_sms);
            // TODO: handle at install—don't store in sharedprefs in case of settings sync/transfer
            //  It's also possible that this check isn't necessary.
        }
        String subject = mSubjectToggle.fieldText();
        appSucceed(subject == null ? intent : intent.putExtra(EXTRA_SUBJECT, subject));
        // TODO: I've not gotten this to work yet
    }

    @NonNull
    private Intent withMsg(Intent intent, String message) {
        return intent.putExtra("sms_body", message);
        // overwrites any existing draft to the recipient TODO: detect, warn, confirm.
    }

    @NonNull
    private Intent withRecipients(Intent intent, String recipients) {
        return intent.setData(Uri.parse("smsto:" + Contacts.namesToPhones(recipients, mPhoneMap)));
    }

    @Override
    protected void run() {
        launchMessenger(mIntent);
    }

    @Override
    protected void run(String recipients) {
        launchMessenger(withRecipients(mIntent, recipients));
    }

    @Override
    protected void runWithData(String message) {
        launchMessenger(withMsg(mIntent, message));
    }

    @Override
    protected void runWithData(String recipients, String message) {
        launchMessenger(withMsg(withRecipients(mIntent, recipients), message));
    }
}
