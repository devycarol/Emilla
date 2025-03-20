package net.emilla.activity;

import static android.content.Intent.ACTION_ASSIST;
import static android.content.Intent.ACTION_VOICE_COMMAND;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_MENU;
import static android.view.KeyEvent.KEYCODE_SEARCH;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_GO;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NONE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_PREVIOUS;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;
import static android.view.inputmethod.EditorInfo.IME_ACTION_SEND;
import static android.view.inputmethod.EditorInfo.IME_ACTION_UNSPECIFIED;
import static net.emilla.chime.Chimer.ACT;
import static net.emilla.chime.Chimer.EXIT;
import static net.emilla.chime.Chimer.FAIL;
import static net.emilla.chime.Chimer.PEND;
import static net.emilla.chime.Chimer.RESUME;
import static net.emilla.chime.Chimer.START;
import static net.emilla.chime.Chimer.SUCCEED;
import static java.lang.Character.isWhitespace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import net.emilla.R;
import net.emilla.action.CursorStart;
import net.emilla.action.Help;
import net.emilla.action.PlayPause;
import net.emilla.action.QuickAction;
import net.emilla.chime.Chimer;
import net.emilla.command.CommandMap;
import net.emilla.command.DataCommand;
import net.emilla.command.EmillaCommand;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.content.receive.ContactCardReceiver;
import net.emilla.content.receive.EmailReceiver;
import net.emilla.content.receive.FileReceiver;
import net.emilla.content.receive.PhoneReceiver;
import net.emilla.content.retrieve.AppChoiceRetriever;
import net.emilla.content.retrieve.ContactCardRetriever;
import net.emilla.content.retrieve.ContactEmailRetriever;
import net.emilla.content.retrieve.ContactPhoneRetriever;
import net.emilla.content.retrieve.FileRetriever;
import net.emilla.content.retrieve.MediaRetriever;
import net.emilla.databinding.ActivityAssistBinding;
import net.emilla.exception.EmillaException;
import net.emilla.lang.Lang;
import net.emilla.permission.PermissionRetriever;
import net.emilla.run.BugFailure;
import net.emilla.run.DialogRun;
import net.emilla.run.Failure;
import net.emilla.run.Gift;
import net.emilla.run.MessageFailure;
import net.emilla.run.Offering;
import net.emilla.run.Success;
import net.emilla.settings.SettingVals;
import net.emilla.util.Apps;
import net.emilla.util.Dialogs;
import net.emilla.util.Strings;
import net.emilla.view.ActionButton;

import java.util.List;

public final class AssistActivity extends EmillaActivity {

    private LayoutInflater mInflater;
    private ActivityAssistBinding mBinding;

    private SharedPreferences mPrefs;
    private CommandMap mCommandMap;
    private List<ResolveInfo> mAppList;
    private Chimer mChimer;

    private QuickAction mNoCommandAction;
    private QuickAction mDoubleAssistAction;
    private QuickAction mMenuKeyAction;

    @Nullable
    private Fragment mDefaultActionBox;

    @Nullable
    private AlertDialog mManual;
    // todo: please handle this another way..

    private final FileRetriever mFileRetriever = new FileRetriever(this);
    private final MediaRetriever mMediaRetriever = new MediaRetriever(this);
    private final ContactCardRetriever mContactCardRetriever = new ContactCardRetriever(this);
    private final ContactPhoneRetriever mContactPhoneRetriever = new ContactPhoneRetriever(this);
    private final ContactEmailRetriever mContactEmailRetriever = new ContactEmailRetriever(this);
    private final AppChoiceRetriever mAppChoiceRetriever = new AppChoiceRetriever(this);
    // TODO: save state hell. rotation deletes attachments ughhhhh probably because the command
    //  tree is rebuilt.

    public final PermissionRetriever permissionRetriever =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? new PermissionRetriever(this) : null;

    private EmillaCommand mCommand;

    private boolean mNoCommand = true;
    private boolean mDataAvailable = true;
    private boolean mAlwaysShowData;
    private int mImeAction = IME_ACTION_NEXT;
    private boolean
            mLaunched = false,
            mOpen = false;
    private boolean mDialogOpen = false;
    private boolean
            mDontChimePend = false,
            mDontChimeResume = false,
            mDontChimeSuccess = false,
            mDontTryCancel = false;
    private boolean mHasTitlebar;

    private long mLastAssistIntentTime;

//    public static long nanosPlease(long prevTime, String label) {
//        long curTime = System.nanoTime();
//        var s = String.valueOf(curTime - prevTime);
//        var sb = new StringBuilder(label).append(": ");
//        int start = sb.length();
//        for (int i = sb.append(s).length() - 3; i > start; i -= 3) sb.insert(i, ',');
//        Log.d("nanosPlease", sb.append(" nanoseconds").toString());
//        return System.nanoTime();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = getLayoutInflater();
        mBinding = ActivityAssistBinding.inflate(mInflater);
        setContentView(mBinding.getRoot());

        if (ACTION_ASSIST.equals(getIntent().getAction())) handleAssistIntent(false);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mChimer = SettingVals.chimer(this, mPrefs);
        if (savedInstanceState == null) chime(START);

        var res = getResources();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException();
        mHasTitlebar = SettingVals.showTitleBar(mPrefs, res);
        if (mHasTitlebar) {
            var dfltTitle = res.getString(R.string.activity_assistant);
            var title = mPrefs.getString("motd", dfltTitle);
            if (!title.equals(dfltTitle)) actionBar.setTitle(title);
        } else {
            actionBar.hide();
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_assistant));
        }

        var pm = getPackageManager();
        mAppList = Apps.resolveList(pm);
        mCommandMap = EmillaCommand.map(mPrefs, res, pm, mAppList);

        setupCommandField();
        mAlwaysShowData = SettingVals.alwaysShowData(mPrefs);
        // TODO ACC: There's no reason for a hidden data field if a screen reader is in use.
        if (mAlwaysShowData
                || savedInstanceState != null && savedInstanceState.getBoolean("dataFieldVisible")) {
            showDataField();
        }

        mBinding.emptySpace.setOnClickListener(v -> cancelIfWarranted());

        mNoCommandAction = SettingVals.noCommand(mPrefs, this);
        mDoubleAssistAction = SettingVals.doubleAssist(mPrefs, this, pm);
        mMenuKeyAction = SettingVals.menuKey(mPrefs, this);

        setupSubmitButton();
        setupDataButtons();
        setupMoreActions();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (mOpen && action != null) switch (action) {
            case ACTION_ASSIST -> handleAssistIntent(true);
            case ACTION_VOICE_COMMAND -> mDoubleAssistAction.perform();
        } else if (ACTION_ASSIST.equals(action)) handleAssistIntent(false);
    }

    private void handleAssistIntent(boolean performAction) {
        // TODO: determine why the corner gesture sends the assist intent twice.
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastAssistIntentTime > 150 /*ms*/) {
            mLastAssistIntentTime = currentTime;
            if (performAction) mDoubleAssistAction.perform();
        } else mLastAssistIntentTime = 0;
    }

    @Override // Todo: replace with view-model?
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dataFieldVisible", mBinding.dataField.getVisibility() == View.VISIBLE);
    }

    private void setupCommandField() {
        EditText commandField = mBinding.commandField;

        commandField.addTextChangedListener(new CommandWatcher());
        commandField.setOnEditorActionListener((v, actionId, event) -> onActionKey(actionId));

        mCommand = mCommandMap.get(this, "");

        commandField.setHorizontallyScrolling(false);
        commandField.setMaxLines(8);
        commandField.requestFocus();
    }

    private final class CommandWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            onCommandChanged(text.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private boolean onActionKey(int actionId) {
        return switch (actionId) {
            case IME_ACTION_UNSPECIFIED, IME_ACTION_NONE, IME_ACTION_PREVIOUS -> false;
            default -> switch (mImeAction) {
                // TODO ACC: There must be clarity on what the enter key will do if you can't see
                //  the screen.
                case IME_ACTION_NEXT:
                    if (mDataAvailable) {
                        focusDataField();
                        yield true;
                    }
                    // fall
                case IME_ACTION_GO, IME_ACTION_SEARCH, IME_ACTION_SEND, IME_ACTION_DONE:
                    submitCommand();
                    yield true;
                default:
                    yield false;
            };
        };
    }

    private void setupSubmitButton() {
        ActionButton submitButton = mBinding.submitButton;
        submitButton.setIcon(mNoCommandAction.icon());
        submitButton.setOnClickListener(v -> submitCommand());

        submitButton.setLongPress(SettingVals.longSubmit(mPrefs, this), getResources());
    }

    private void setupDataButtons() {
        if (mAlwaysShowData) {
            mBinding.showDataButton.setVisibility(View.GONE);
        } else {
            mBinding.showDataButton.setOnClickListener(v -> focusDataField());
            mBinding.hideDataButton.setOnClickListener(v -> hideDataField());
        }
    }

    private void focusDataField() {
        EditText dataField = mBinding.dataField;
        if (dataField.hasFocus()) return;

        if (dataField.getVisibility() == View.GONE) showDataField();
        dataField.requestFocus();
    }

    private void showDataField() {
        mBinding.dataField.setVisibility(View.VISIBLE);
        if (mAlwaysShowData) return;
        mBinding.hideDataButton.setVisibility(View.VISIBLE);
        mBinding.showDataButton.setVisibility(View.GONE);
    }

    private void hideDataField() {
        EditText commandField = mBinding.commandField;
        EditText dataField = mBinding.dataField;

        int start, end;
        boolean dataFocused = dataField.hasFocus();
        if (dataFocused) {
            int dataLen = dataField.length();
            start = dataField.getSelectionStart() - dataLen;
            end = dataField.getSelectionEnd() - dataLen;
        } else {
            start = commandField.getSelectionStart();
            end = commandField.getSelectionEnd();
        }
        commandField.requestFocus();
        if (dataField.length() > 0) {
            Editable commandText = commandField.getText();
            Editable dataText = dataField.getText();
            int len = commandText.length();
            if (len == 0 || isWhitespace(commandText.charAt(len - 1))) commandField.append(dataText);
            else commandField.append(Lang.wordConcat(getResources(), "", dataText));
            dataField.setText(null);
        }
        int newLen = commandField.length();
        if (dataFocused) commandField.setSelection(newLen + start, newLen + end);
        else commandField.setSelection(start, end);

        dataField.setVisibility(View.GONE);
        mBinding.hideDataButton.setVisibility(View.GONE);
        if (!mDataAvailable) disableDataButton();
        mBinding.showDataButton.setVisibility(View.VISIBLE);
    }

    private void setupMoreActions() {
        // TODO: save state hell
        // Todo: put these in an editor.
        if (SettingVals.showCursorStartButton(mPrefs)) addAction(new CursorStart(this));
        if (SettingVals.showHelpButton(mPrefs)) addAction(new Help(this));
        if (SettingVals.showPlayPauseButton(mPrefs)) addAction(new PlayPause(this));
    }

    public void addAction(QuickAction action) {
        LinearLayout actionsContainer = mBinding.actionsContainer;
        var button = (ActionButton) mInflater.inflate(R.layout.btn_action, actionsContainer, false);

        button.setId(action.id());
        button.setIcon(action.icon());
        button.setContentDescription(action.label(getResources()));
        button.setOnClickListener(v -> action.perform());

        actionsContainer.addView(button, 0);
    }

    public void removeAction(@IdRes int action) {
        mBinding.actionsContainer.removeView(findViewById(action));
    }

    public EditText createField(@IdRes int id, @StringRes int hint) {
        LinearLayout fieldsContainer = mBinding.fieldsContainer;

        var box = (EditText) mInflater.inflate(R.layout.field_extra, fieldsContainer, false);
        box.setId(id);
        box.setHint(hint);

        fieldsContainer.addView(box);
        box.requestFocus();

        return box;
    }

    /**
     * Toggles the visibility of an input field.
     *
     * @param id the field to toggle.
     * @return true if the field is visible now, false if it's hidden.
     */
    public boolean toggleField(@IdRes int id) {
        // Todo: it's vague which field is which. First step: make them ordered consistently.
        EditText box = findViewById(id);
        if (box.getVisibility() == View.VISIBLE) {
            if (box.hasFocus()) mBinding.commandField.requestFocus();
            box.setVisibility(View.GONE);
            return false;
        } else {
            box.setVisibility(View.VISIBLE);
            box.requestFocus();
            return true;
        }
    }

    public void reshowField(@IdRes int id) {
        EditText box = findViewById(id);
        box.setVisibility(View.VISIBLE);
    }

    public void hideField(@IdRes int id) {
        EditText box = findViewById(id);
        if (box != null && box.getVisibility() == View.VISIBLE) {
            if (box.hasFocus()) mBinding.commandField.requestFocus();
            box.setVisibility(View.GONE);
        }
    }

    public void giveActionBox(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.action_box, fragment)
                .commit();
    }

    public void removeActionBox(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (mDefaultActionBox == null) transaction.remove(fragment);
        else transaction.replace(R.id.action_box, mDefaultActionBox);

        transaction.commit();
    }

    protected void onResume() {
        super.onResume();
        if (!mOpen) {
            if (mLaunched) resume();
            else mLaunched = true;
            mOpen = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mOpen = false;
        if (!(isChangingConfigurations() || isFinishing())) {
            if (shouldCancel()) cancel();
            // TODO: the launch fail bug is caused by focus stealing
            else if (!mDialogOpen && askChimePend()) chime(PEND);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppChoiceRetriever.AppChooserBroadcastReceiver.deleteRetriever();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.isCanceled()) return false;

        switch (keyCode) {
        case KEYCODE_BACK -> cancelIfWarranted(); // todo config? command history?
        case KEYCODE_MENU -> mMenuKeyAction.perform();
        case KEYCODE_SEARCH -> give(() -> {}); // todo config
        default -> { return false; }
        }
        return true;
    }

    private void updateDataAvailability(boolean available) {
        EditText dataField = mBinding.dataField;

        if (available) {
            if (!mAlwaysShowData) enableDataButton();
            dataField.setEnabled(true);
        } else if (dataField.getVisibility() == View.GONE) {
            if (!mAlwaysShowData) disableDataButton();
        } else {
            dataField.setEnabled(false);
        }

        mDataAvailable = available;
    }

    private void enableDataButton() {
        ActionButton showDataButton = mBinding.showDataButton;
        showDataButton.setEnabled(true);
        showDataButton.setAlpha(1.0f);
    }

    private void disableDataButton() {
        ActionButton showDataButton = mBinding.showDataButton;
        showDataButton.setEnabled(false);
        showDataButton.setAlpha(0.3f);
    }

    public void updateTitle(CharSequence title) {
        if (mHasTitlebar) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setTitle(mNoCommand ? mPrefs.getString("motd",
                    getString(R.string.activity_assistant))
                : title);
        }
    }

    public void updateDataHint() {
        EditText dataField = mBinding.dataField;
        if (mNoCommand || !mCommand.usesData()) dataField.setHint(R.string.data_hint_default);
        else dataField.setHint(((DataCommand) mCommand).dataHint());
    }

    public void setSubmitIcon(Drawable icon, boolean isAppIcon) {
        mBinding.submitButton.setIcon(icon, isAppIcon);
    }

    public void setImeAction(int action) {
        if (mNoCommand) action = IME_ACTION_NEXT;
        if (action != mImeAction) mBinding.commandField.setImeOptions(mImeAction = action);
    }

    private void onCommandChanged(String command) {
        command = Strings.trimLeading(command);

        EmillaCommand cmd = mCommandMap.get(this, command);
        boolean noCommand = command.isEmpty();
        if (cmd != mCommand || noCommand != mNoCommand) {
            mCommand.clean();
            mCommand = cmd;
            mNoCommand = noCommand;
            if (noCommand) {
                cmd.decorate(false);
                mBinding.submitButton.setIcon(mNoCommandAction.icon());
            } else {
                cmd.decorate(true);
                cmd.init();
            }

            boolean dataAvailable = noCommand || mCommand.usesData();
            if (dataAvailable != mDataAvailable) updateDataAvailability(dataAvailable);
        }
    }

    /*=========*
     * Getters *
     *=========*/

    public SharedPreferences prefs() {
        return mPrefs;
    }

    public List<ResolveInfo> appList() {
        return mAppList;
    }

    public EditText focusedEditBox() {
        if (getCurrentFocus() instanceof EditText focusedTextBox) {
            return focusedTextBox;
        }

        EditText commandField = mBinding.commandField;
        commandField.requestFocus();

        return commandField;
        // default to the command field
    }

    @Nullable
    public String dataText() {
        EditText dataField = mBinding.dataField;
        return dataField.length() == 0 ? null : dataField.getText().toString();
    }

    public EmillaCommand command() {
        return mCommand;
    }

    /*=========*
     * Setters *
     *=========*/

    public void suppressPendingChime() {
        mDontChimePend = true;
    }

    public void suppressResumeChime() {
        mDontChimeResume = true;
    }

    public void suppressSuccessChime() {
        mDontChimeSuccess = true;
    }

    @Deprecated
    public void suppressBackCancellation() {
        // Todo: don't have this. use what the 'modern' navigation system wants instead of
        //  KEYCODE_BACK.
        mDontTryCancel = true;
    }

    public void setManual(@Nullable AlertDialog manual) {
        mManual = manual;
    }

    public boolean cancelManualIfShowing() {
        if (mManual == null || !mManual.isShowing()) return false;
        mManual.cancel();
        return true;
    }

    /*====================*
     * Command Processing *
     *====================*/

    public void chime(byte id) {
        // Todo: I'd love to add a couple more in-built sound packs from open source ecosystems! Anyone
        //  stumbling across this is welcome to give suggestions.
        mChimer.chime(id);
    }

    private boolean askChimePend() {
        if (mDontChimePend) return mDontChimePend = false;
        return true;
    }

    private boolean askChimeResume() {
        if (mDontChimeResume) return mDontChimeResume = false;
        return true;
    }

    private boolean askChimeSuccess() {
        if (mDontChimeSuccess) return mDontChimeSuccess = false;
        return true;
    }

    public void resume() {
        if (!mDialogOpen && askChimeResume()) chime(RESUME);
    }

    public boolean shouldCancel() {
        return (mBinding.commandField.length() | mBinding.dataField.length()) == 0;
    }

    public void cancel() {
        chime(EXIT);
        finishAndRemoveTask();
    }

    @Deprecated
    public void onCloseDialog() {
        mBinding.emptySpace.setEnabled(true);
        mBinding.submitButton.setEnabled(true);
        mDialogOpen = false;
    }

    private void cancelIfWarranted() {
        if (!askTryCancel()) return;

        if (shouldCancel()) cancel();
        else offer(new DialogRun(this, cancelDialog()));
    }

    @Deprecated
    private boolean askTryCancel() {
        if (mDontTryCancel) return mDontTryCancel = false;
        return true;
    }

    private AlertDialog.Builder cancelDialog() {
        return Dialogs.dual(this, R.string.exit, R.string.dlg_msg_exit, R.string.leave,
                (dlg, which) -> cancel())
                .setOnKeyListener((dlg, keyCode, event) -> {
            if (keyCode == KEYCODE_BACK && event.getAction() == ACTION_UP) {
                cancel();
                return true;
            }
            return false;
        });
    }

    public void prepareForDialog() {
        // TODO: view enablement shouldn't be handled on a view-by-view basis. Perhaps target the
        //  mother of all views (whatever that is) or get to the bottom of why views can be clicked
        //  in the split-second after dialog invocation in the first place
        mDialogOpen = true;
        mBinding.emptySpace.setEnabled(false);
        mBinding.submitButton.setEnabled(false);
    }

    public void offer(Offering offering) {
        offering.run();
        chime(PEND);
    }

    public void offerFiles(FileReceiver retriever, String mimeType) {
        mFileRetriever.retrieve(retriever, mimeType);
    }

    public void offerMedia(FileReceiver receiver) {
        mMediaRetriever.retrieve(receiver);
        chime(PEND);
    }

    public void offerContactCards(ContactCardReceiver receiver) {
        mContactCardRetriever.retrieve(receiver);
    }

    public void offerContactPhones(PhoneReceiver receiver) {
        mContactPhoneRetriever.retrieve(receiver);
    }

    public void offerContactEmails(EmailReceiver receiver) {
        mContactEmailRetriever.retrieve(receiver);
    }

    public void offerChooser(AppChoiceReceiver receiver, Intent target, @StringRes int title) {
        mAppChoiceRetriever.retrieve(receiver, target, title);
        chime(PEND);
    }

    public void give(Gift gift) {
        focusedEditBox().selectAll();
        gift.run();
        chime(ACT);
    }

    public void succeed(Success success) {
        success.run();
        if (askChimeSuccess()) chime(SUCCEED);
    }

    public void fail(Failure failure) {
        failure.run();
        chime(FAIL);
    }

    private void submitCommand() {
        var fullCommand = mBinding.commandField.getText().toString().trim();
        if (fullCommand.isEmpty()) {
            mNoCommandAction.perform();
            return;
        }
    try {
        EditText dataField = mBinding.dataField;
        if (mCommand.usesData() && dataField.length() > 0) {
            ((DataCommand) mCommand).execute(dataField.getText().toString());
        } else mCommand.execute();
    } catch (EmillaException e) {
        fail(new MessageFailure(this, e.title(), e.message()));
    } catch (RuntimeException e) {
        fail(new BugFailure(this, e, mCommand.name()));
    }}
}