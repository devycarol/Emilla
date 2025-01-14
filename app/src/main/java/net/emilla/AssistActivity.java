package net.emilla;

import static android.content.Intent.ACTION_ASSIST;
import static android.content.Intent.ACTION_VOICE_COMMAND;
import static android.view.KeyEvent.*;
import static android.view.inputmethod.EditorInfo.*;
import static net.emilla.chime.Chimer.*;
import static java.lang.Character.isWhitespace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import net.emilla.action.CursorStart;
import net.emilla.action.Help;
import net.emilla.action.QuickAction;
import net.emilla.chime.Chimer;
import net.emilla.command.CommandMap;
import net.emilla.command.DataCommand;
import net.emilla.command.EmillaCommand;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.content.receive.ContactReceiver;
import net.emilla.content.receive.FileReceiver;
import net.emilla.content.retrieve.AppChoiceRetriever;
import net.emilla.content.retrieve.ContactRetriever;
import net.emilla.content.retrieve.FileRetriever;
import net.emilla.content.retrieve.MediaRetriever;
import net.emilla.exception.EmillaException;
import net.emilla.lang.Lang;
import net.emilla.run.BugFailure;
import net.emilla.run.DialogOffering;
import net.emilla.run.Failure;
import net.emilla.run.Gift;
import net.emilla.run.MessageFailure;
import net.emilla.run.Offering;
import net.emilla.run.Success;
import net.emilla.settings.SettingVals;
import net.emilla.system.EmillaForegroundService;
import net.emilla.util.Apps;
import net.emilla.util.Contacts;
import net.emilla.util.Dialogs;
import net.emilla.view.ActionButton;

import java.util.List;

public class AssistActivity extends EmillaActivity {

    private SharedPreferences mPrefs;
    private CommandMap mCommandMap;
    private List<ResolveInfo> mAppList;
    private Chimer mChimer;

    private LayoutInflater mInflater;
    private FrameLayout mEmptySpace;
    private EditText mCommandField, mDataField;
    private ActionButton mSubmitButton;
    private ActionButton mShowDataButton, mHideDataButton;
    private LinearLayout mFieldsContainer;
    private LinearLayout mActionsContainer;

    private QuickAction mNoCommandAction;
    private QuickAction mDoubleAssistAction;
    private QuickAction mMenuKeyAction;

    private AlertDialog mManual;
    // todo: please handle this another way..

    private FileRetriever mFileRetriever;
    private MediaRetriever mMediaRetriever;
    private ContactRetriever mContactRetriever;
    private AppChoiceRetriever mAppChoiceRetriever;

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
            mDontChimeResume = false;
    private boolean mHasTitlebar;

    private long mLastAssistIntentTime;

//    public static long nanosPlease(long prevTime, String label) {
//        long curTime = System.nanoTime();
//        String s = String.valueOf(curTime - prevTime);
//        StringBuilder sb = new StringBuilder(label).append(": ");
//        int start = sb.length();
//        for (int i = sb.append(s).length() - 3; i > start; i -= 3) sb.insert(i, ',');
//        Log.d("nanosPlease", sb.append(" nanoseconds").toString());
//        return System.nanoTime();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assist);

        if (ACTION_ASSIST.equals(getIntent().getAction())) handleAssistIntent(false);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mChimer = SettingVals.chimer(this, mPrefs);
        if (savedInstanceState == null) chime(START);

        Resources res = getResources();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException();
        mHasTitlebar = SettingVals.showTitleBar(mPrefs, res);
        if (mHasTitlebar) {
            String dfltTitle = res.getString(R.string.activity_assistant);
            String title = mPrefs.getString("motd", dfltTitle);
            if (!title.equals(dfltTitle)) actionBar.setTitle(title);
        } else actionBar.hide();

        if (savedInstanceState == null) switch (mPrefs.getString("run_in_background", "follow_system")) {
            case "follow_system":
                // TODO: have listener to turn off the service when power save mode or the accessibility
                //  service is activated
                // also ensure "always on" works as intended
                PowerManager pwrMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
                if (pwrMgr.isPowerSaveMode()) break;
                // fall
            case "always":
                startService(new Intent(this, EmillaForegroundService.class));
        }

        PackageManager pm = getPackageManager();
        mAppList = Apps.resolveList(pm);
        mCommandMap = EmillaCommand.map(mPrefs, res, pm, mAppList);

        mEmptySpace = findViewById(R.id.empty_space);
        mCommandField = findViewById(R.id.field_command);
        mDataField = findViewById(R.id.field_data);
        mSubmitButton = findViewById(R.id.btn_submit);
        mShowDataButton = findViewById(R.id.btn_show_data);
        mHideDataButton = findViewById(R.id.btn_hide_data);

        setupCommandField();
        mAlwaysShowData = SettingVals.alwaysShowData(mPrefs);
        // TODO ACC: There's no reason for a hidden data field if a screen reader is in use.
        if (mAlwaysShowData
                || savedInstanceState != null && savedInstanceState.getBoolean("dataFieldVisible")) {
            showDataField();
        }

        mEmptySpace.setOnClickListener(v -> cancelIfWarranted());

        mNoCommandAction = SettingVals.noCommand(mPrefs, this);
        mDoubleAssistAction = SettingVals.doubleAssist(mPrefs, this, pm);
        mMenuKeyAction = SettingVals.menuKey(mPrefs, this);

        setupSubmitButton();
        setupDataButtons();
        setupMoreActions();

        mFileRetriever = new FileRetriever(this);
        mMediaRetriever = new MediaRetriever(this);
        mContactRetriever = new ContactRetriever(this);
        mAppChoiceRetriever = new AppChoiceRetriever(this);
        // TODO: save state hell. rotation deletes attachments ughhhhh probably because the command
        //  tree is rebuilt.
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dataFieldVisible", mDataField.getVisibility() == View.VISIBLE);
    }

    private void setupCommandField() {
        mCommandField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                onCommandChanged(text.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mCommandField.setOnEditorActionListener((v, actionId, event) -> switch (actionId) {
            case IME_ACTION_UNSPECIFIED, IME_ACTION_NONE, IME_ACTION_PREVIOUS -> false;
            default -> switch (mImeAction) {
                // TODO ACC: There must be clarity on what the enter key will do if you can't see the
                //  screen.
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
        });

        mCommand = mCommandMap.get(this, "");

        mCommandField.setHorizontallyScrolling(false);
        mCommandField.setMaxLines(8);
        mCommandField.requestFocus();
    }

    private void setupSubmitButton() {
        mSubmitButton.setIcon(mNoCommandAction.icon());
        mSubmitButton.setOnClickListener(v -> submitCommand());

        mSubmitButton.setLongPress(SettingVals.longSubmit(mPrefs, this), getResources());
    }

    private void setupDataButtons() {
        if (mAlwaysShowData) mShowDataButton.setVisibility(View.GONE);
        else {
            mShowDataButton.setOnClickListener(v -> focusDataField());
            mHideDataButton.setOnClickListener(v -> hideDataField());
        }
    }

    private void focusDataField() {
        if (mDataField.hasFocus()) return;
        if (mDataField.getVisibility() == View.GONE) showDataField();
        mDataField.requestFocus();
    }

    private void showDataField() {
        mDataField.setVisibility(View.VISIBLE);
        if (mAlwaysShowData) return;
        mHideDataButton.setVisibility(View.VISIBLE);
        mShowDataButton.setVisibility(View.GONE);
    }

    private void hideDataField() {
        int start, end;
        boolean dataFocused = mDataField.hasFocus();
        if (dataFocused) {
            int dataLen = mDataField.length();
            start = mDataField.getSelectionStart() - dataLen;
            end = mDataField.getSelectionEnd() - dataLen;
        } else {
            start = mCommandField.getSelectionStart();
            end = mCommandField.getSelectionEnd();
        }
        mCommandField.requestFocus();
        if (mDataField.length() > 0) {
            Editable commandText = mCommandField.getText();
            Editable dataText = mDataField.getText();
            int len = commandText.length();
            if (len == 0 || isWhitespace(commandText.charAt(len - 1))) mCommandField.append(dataText);
            else mCommandField.append(Lang.wordConcat(getResources(), "", dataText));
            mDataField.setText(null);
        }
        int newLen = mCommandField.length();
        if (dataFocused) mCommandField.setSelection(newLen + start, newLen + end);
        else mCommandField.setSelection(start, end);

        mDataField.setVisibility(View.GONE);
        mHideDataButton.setVisibility(View.GONE);
        if (!mDataAvailable) disableDataButton();
        mShowDataButton.setVisibility(View.VISIBLE);
    }

    private void setupMoreActions() {
        // TODO: save state hell
        mInflater = LayoutInflater.from(this);
        mActionsContainer = findViewById(R.id.container_more_actions);
        // Todo: put these in an editor.
        if (SettingVals.showCursorStartButton(mPrefs)) addAction(new CursorStart(this));
        if (SettingVals.showHelpButton(mPrefs)) addAction(new Help(this));
        mFieldsContainer = findViewById(R.id.container_more_fields);
    }

    public void addAction(QuickAction action) {
        ActionButton button = (ActionButton) mInflater.inflate(R.layout.btn_action,
                mActionsContainer, false);
        button.setId(action.id());
        button.setIcon(action.icon());
        button.setContentDescription(action.label(getResources()));
        button.setOnClickListener(v -> action.perform());
        mActionsContainer.addView(button, 0);
    }

    public void removeAction(@IdRes int action) {
        mActionsContainer.removeView(findViewById(action));
    }

    public EditText createField(@IdRes int id, @StringRes int hint) {
        EditText box = (EditText) mInflater.inflate(R.layout.field_extra, mFieldsContainer, false);
        box.setId(id);
        box.setHint(hint);
        mFieldsContainer.addView(box);
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
            if (box.hasFocus()) mCommandField.requestFocus();
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
            if (box.hasFocus()) mCommandField.requestFocus();
            box.setVisibility(View.GONE);
        }
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
        Contacts.clean();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KEYCODE_BACK -> cancelIfWarranted(); // todo config? command history?
        case KEYCODE_MENU -> mMenuKeyAction.perform();
        case KEYCODE_SEARCH -> give(() -> {}); // todo config
        default -> { return false; }
        }
        return true;
    }

    private void updateDataAvailability(boolean available) {
        if (available) {
            if (!mAlwaysShowData) enableDataButton();
            mDataField.setEnabled(true);
        } else if (mDataField.getVisibility() == View.GONE) {
            if (!mAlwaysShowData) disableDataButton();
        } else mDataField.setEnabled(false);
        mDataAvailable = available;
    }

    private void enableDataButton() {
        mShowDataButton.setEnabled(true);
        mShowDataButton.setAlpha(1.0f);
    }

    private void disableDataButton() {
        mShowDataButton.setEnabled(false);
        mShowDataButton.setAlpha(0.3f);
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
        if (mNoCommand || !mCommand.usesData()) mDataField.setHint(R.string.data_hint_default);
        else mDataField.setHint(((DataCommand) mCommand).dataHint());
    }

    public void setSubmitIcon(Drawable icon, boolean isAppIcon) {
        mSubmitButton.setIcon(icon, isAppIcon);
    }

    public void setImeAction(int action) {
        if (mNoCommand) action = IME_ACTION_NEXT;
        if (action != mImeAction) mCommandField.setImeOptions(mImeAction = action);
    }

    private void onCommandChanged(String command) {
        int len = command.length();
        if (len > 0 && command.charAt(0) == ' ') {
            int nonSpace = 0;
            while (len > ++nonSpace && command.charAt(nonSpace) == ' ') ;
            command = command.substring(nonSpace, len);
        }

        EmillaCommand cmd = mCommandMap.get(this, command);
        boolean noCommand = command.isEmpty();
        if (cmd != mCommand || noCommand != mNoCommand) {
            mCommand.clean();
            mCommand = cmd;
            mNoCommand = noCommand;
            if (noCommand) {
                cmd.decorate(false);
                mSubmitButton.setIcon(mNoCommandAction.icon());
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
        if (getCurrentFocus() instanceof EditText focusedTextBox) return focusedTextBox;
        mCommandField.requestFocus();
        return mCommandField;
        // default to the command field
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

    public void setManual(AlertDialog manual) {
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

    private void resume() {
        if (!mDialogOpen && askChimeResume()) chime(RESUME);
    }

    public boolean shouldCancel() {
        return mCommandField.length() + mDataField.length() == 0;
    }

    public void cancel() {
        chime(EXIT);
        finishAndRemoveTask();
    }

    @Deprecated
    public void onCloseDialog(boolean chime) {
        mEmptySpace.setEnabled(true);
        mSubmitButton.setEnabled(true);
        mDialogOpen = false;

        if (chime) resume();
    }

    private void cancelIfWarranted() {
        if (shouldCancel()) cancel();
        else offer(new DialogOffering(this, cancelDialog()));
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
        mEmptySpace.setEnabled(false);
        mSubmitButton.setEnabled(false);
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

    public void offerContacts(ContactReceiver receiver) {
        mContactRetriever.retrieve(receiver);
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
        finishAndRemoveTask();
        success.run();
        chime(SUCCEED);
    }

    public void fail(Failure failure) {
        failure.run();
        chime(FAIL);
    }

    private void submitCommand() {
        String fullCommand = mCommandField.getText().toString().trim();
        if (fullCommand.isEmpty()) {
            mNoCommandAction.perform();
            return;
        }
    try {
        if (mCommand.usesData() && mDataField.length() > 0) {
            ((DataCommand) mCommand).execute(mDataField.getText().toString());
        } else mCommand.execute();
    } catch (EmillaException e) {
        fail(new MessageFailure(this, e.title(), e.message()));
    } catch (RuntimeException e) {
        fail(new BugFailure(this, e, mCommand.name()));
    }}
}