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
import static net.emilla.chime.Chime.EXIT;
import static net.emilla.chime.Chime.PEND;
import static net.emilla.chime.Chime.RESUME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import net.emilla.Feedback;
import net.emilla.R;
import net.emilla.action.CursorStart;
import net.emilla.action.Help;
import net.emilla.action.PlayPause;
import net.emilla.action.QuickAction;
import net.emilla.chime.Chime;
import net.emilla.command.CommandMap;
import net.emilla.command.EmillaCommand;
import net.emilla.command.app.AppEntry;
import net.emilla.config.SettingVals;
import net.emilla.content.receive.AppChoiceReceiver;
import net.emilla.content.receive.FilesReceiver;
import net.emilla.content.retrieve.AppChoiceRetriever;
import net.emilla.content.retrieve.FilesRetriever;
import net.emilla.content.retrieve.MediaRetriever;
import net.emilla.content.retrieve.TextFileCreator;
import net.emilla.databinding.AssistActivityBinding;
import net.emilla.file.Folder;
import net.emilla.lang.Lang;
import net.emilla.permission.PermissionRetriever;
import net.emilla.run.BugFailure;
import net.emilla.util.Dialogs;
import net.emilla.util.Views;
import net.emilla.wadget.ActionSurface;
import net.emilla.widget.ActionButton;
import net.emilla.widget.ActionIcon;
import net.emilla.widget.SymbolIcon;

import java.util.ArrayList;

public final class AssistActivity
    extends AppCompatActivity
    implements ActionSurface
{
    private final TextFileCreator mTextFileCreator = new TextFileCreator(this);
    private final FilesRetriever mFilesRetriever = new FilesRetriever(this);
    private final MediaRetriever mMediaRetriever = new MediaRetriever(this);
    private final AppChoiceRetriever mAppChoiceRetriever = new AppChoiceRetriever(this);
    private final PermissionRetriever mPermissionRetriever
        = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            ? new PermissionRetriever(this)
            : null
        ;

    private /*late*/ LayoutInflater mInflater;
    private /*late*/ AssistActivityBinding mBinding;
    private /*late*/ AssistViewModel mVm;

    @Nullable
    private Fragment mDefaultActionBox = null;
    @Nullable
    private AlertDialog mManual = null;
    // todo: please handle this another way..

    private /*late*/ QuickAction mNoCommandAction;
    private /*late*/ QuickAction mDoubleAssistAction;
    private /*late*/ QuickAction mMenuKeyAction;

    private /*late*/ CommandMap mCommandMap;
    private /*late*/ EmillaCommand mCommand;

    private int mInstructionPosition;

    private boolean mOpen = false;

    public AssistActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            EdgeToEdge.enable(this);
        }

        mInflater = getLayoutInflater();
        mBinding = AssistActivityBinding.inflate(mInflater);
        setContentView(mBinding.getRoot());

        var factory = new AssistViewModel.Factory(this);
        var provider = new ViewModelProvider(this, factory);
        mVm = provider.get(AssistViewModel.class);

        if (ACTION_ASSIST.equals(getIntent().getAction())) {
            acknowledgeAssistIntent(false);
        }

        var res = getResources();

        TextView titleText = mBinding.titleText;
        titleText.setText(mVm.motd);
        titleText.setOnClickListener(v -> Help.perform(this));
        Views.setClickActionLabel(res, titleText, R.string.action_desc_help);

        setupCommandField();
        if (mVm.dataVisible) {
            showDataField();
        }
        mBinding.getRoot().setOnClickListener(v -> cancelIfWarranted());
        mBinding.dataButton.setOnClickListener(v -> {
            if (mBinding.dataFieldFrame.getVisibility() != View.VISIBLE) {
                focusDataField();
            } else {
                hideDataField();
            }
        });
        setupMoreActions();

        SharedPreferences prefs = mVm.prefs;
        var pm = getPackageManager();

        mNoCommandAction = SettingVals.noCommand(prefs, this);
        setupSubmitButton();
        mDoubleAssistAction = SettingVals.doubleAssist(prefs, this, pm);
        mMenuKeyAction = SettingVals.menuKey(prefs, this);

        mCommandMap = EmillaCommand.map(prefs, mVm.res, pm, mVm.apps);
        mCommand = mCommandMap.getDefault(this);
        mCommand.load(this);
    }

    private void setupCommandField() {
        EditText commandField = mBinding.commandField;

        commandField.setHorizontallyScrolling(false);
        commandField.setMaxLines(8);

        commandField.addTextChangedListener(new CommandWatcher());
        commandField.setOnEditorActionListener((v, actionId, event) -> onActionKey(actionId));

        commandField.requestFocus();
    }

    @Override
    public void take(Feedback feedback) {
        feedback.run(this);
    }

    private final class CommandWatcher implements TextWatcher {
        CommandWatcher() {
        }

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            String command = text.toString();

            var act = AssistActivity.this;
            EmillaCommand cmd = mCommandMap.get(act, command);
            if (cmd == null) {
                cmd = mCommandMap.getDefault(act, command);
            }

            boolean noCommand = command.isBlank();
            if (cmd != mCommand || noCommand != mVm.noCommand) {
                mCommand.unload(act);
                mCommand = cmd;
                mVm.noCommand = noCommand;

                CharSequence title = cmd.title(mVm.res);
                act.updateTitle(title);
                act.updateDataHint();
                act.setImeAction(cmd.imeAction);
                if (noCommand) {
                    mBinding.submitButton.setIcon(mNoCommandAction.icon());
                } else {
                    act.setSubmitIcon(cmd.actionIcon(act));
                    cmd.load(act);
                }

                boolean dataAvailable = noCommand || mCommand.dataHint != 0;
                if (dataAvailable != mVm.dataAvailable) {
                    mVm.dataAvailable = dataAvailable;
                    if (dataAvailable) {
                        mBinding.dataButtonFrame.setVisibility(View.VISIBLE);
                        onIncreaseActionOverflow(
                            mBinding.actionsContainer.getChildCount() + 1
                        );
                    } else {
                        if (mBinding.dataFieldFrame.getVisibility() != View.GONE
                        ) {
                            hideDataField();
                        }
                        mBinding.dataButtonFrame.setVisibility(View.GONE);
                        onDecreaseActionOverflow(
                            mBinding.actionsContainer.getChildCount()
                        );
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private boolean onActionKey(int actionId) {
        return switch (actionId) {
            case IME_ACTION_UNSPECIFIED, IME_ACTION_NONE, IME_ACTION_PREVIOUS -> false;
            default -> switch (mVm.imeAction) {
                // TODO ACC: There must be clarity on what the enter key will do if you can't see
                //  the screen.
                case IME_ACTION_NEXT:
                    if (mVm.dataAvailable) {
                        focusDataField();
                        yield true;
                    }
                    // fallthrough
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

        submitButton.setLongPress(SettingVals.longSubmit(mVm.prefs, this), mVm.res);
    }

    private void focusDataField() {
        if (mBinding.dataField.hasFocus()) {
            return;
        }

        if (mBinding.dataFieldFrame.getVisibility() != View.VISIBLE) {
            showDataField();
        }
        mBinding.dataField.requestFocus();
    }

    private void showDataField() {
        mBinding.dataFieldFrame.setVisibility(View.VISIBLE);
        mVm.dataVisible = true;
        mBinding.dataButton.setIcon(new SymbolIcon(R.drawable.ic_hide_data));
        var res = getResources();
        mBinding.dataButton.setContentDescription(
            res.getString(R.string.spoken_description_hide_data)
        );
        onDecreaseActionOverflow(mBinding.actionsContainer.getChildCount());
    }

    private void hideDataField() {
        mBinding.commandField.requestFocus();
        mBinding.dataFieldFrame.setVisibility(View.GONE);
        mVm.dataVisible = false;
        mBinding.dataButton.setIcon(new SymbolIcon(R.drawable.ic_show_data));
        var res = getResources();
        mBinding.dataButton.setContentDescription(
            res.getString(R.string.spoken_description_show_data)
        );
        onIncreaseActionOverflow(mBinding.actionsContainer.getChildCount() + 1);
    }

    private int actionOverflowLevel() {
        int overflowLevel = mBinding.actionsContainer.getChildCount();
        if (mBinding.dataButtonFrame.getVisibility() != View.GONE
            && mBinding.dataFieldFrame.getVisibility() != View.VISIBLE
        ) {
            ++overflowLevel;
        }
        return overflowLevel;
    }

    private void onIncreaseActionOverflow(int overflowLevel) {
        var constraints = new ConstraintSet();
        constraints.clone(mBinding.constraints);
        switch (overflowLevel) {
        case 1 -> constraints.connect(
            R.id.title_text,
            ConstraintSet.END,
            R.id.actions_container,
            ConstraintSet.START
        );
        case 2 -> {
            constraints.connect(
                R.id.actions_container,
                ConstraintSet.START,
                R.id.action_box,
                ConstraintSet.END,
                getResources().getDimensionPixelSize(R.dimen.margin_narrow)
            );
            constraints.connect(
                R.id.action_box,
                ConstraintSet.END,
                R.id.actions_container,
                ConstraintSet.START
            );
            constraints.connect(
                R.id.action_box,
                ConstraintSet.BOTTOM,
                R.id.title_text,
                ConstraintSet.TOP
            );
        }
        }
        constraints.applyTo(mBinding.constraints);
    }

    private void onDecreaseActionOverflow(int overflowLevel) {
        var constraints = new ConstraintSet();
        constraints.clone(mBinding.constraints);
        switch (overflowLevel) {
        case 0 -> constraints.clear(R.id.title_text, ConstraintSet.END);
        case 1 -> {
            constraints.connect(
                R.id.action_box,
                ConstraintSet.BOTTOM,
                R.id.barrier,
                ConstraintSet.TOP
            );
            constraints.clear(R.id.actions_container, ConstraintSet.START);
            constraints.connect(
                R.id.action_box,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            );
        }
        }
        constraints.applyTo(mBinding.constraints);
    }

    private void setupMoreActions() {
        mBinding.actionsContainer.setOnHierarchyChangeListener(
            new ViewGroup.OnHierarchyChangeListener() {
                @Override
                public void onChildViewAdded(View parent, View child) {
                    onIncreaseActionOverflow(actionOverflowLevel());
                }

                @Override
                public void onChildViewRemoved(View parent, View child) {
                    onDecreaseActionOverflow(actionOverflowLevel());
                }
            }
        );
        SharedPreferences prefs = mVm.prefs;
        // TODO: save state hell
        if (SettingVals.showCursorStartButton(prefs)) {
            new CursorStart(this).load(this);
        }
        if (SettingVals.showPlayPauseButton(prefs)) {
            new PlayPause(this).load(this);
        }
    }

    public void addAction(QuickAction action) {
        var button = (ActionButton) mInflater.inflate(
            R.layout.btn_action,
            mBinding.actionsContainer,
            false
        );
        button.setId(action.id());
        button.setIcon(action.icon());
        button.setContentDescription(action.label(mVm.res));
        button.setOnClickListener(v -> action.perform());
        mBinding.actionsContainer.addView(button, 0);
    }

    public void removeAction(@IdRes int action) {
        mBinding.actionsContainer.removeView(findViewById(action));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (action == null) {
            return;
        }

        if (!mOpen) {
            if (action.equals(ACTION_ASSIST)) {
                acknowledgeAssistIntent(false);
            }
            return;
        }

        switch (action) {
        case ACTION_ASSIST -> acknowledgeAssistIntent(true);
        case ACTION_VOICE_COMMAND -> mDoubleAssistAction.perform();
        }
    }

    private long mLastAssistTime = 0L;

    private void acknowledgeAssistIntent(boolean performAction) {
        // TODO: determine why the corner gesture sends the assist intent twice.
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastAssistTime > 150L /*ms*/) {
            mLastAssistTime = currentTime;
            if (performAction) {
                mDoubleAssistAction.perform();
            }
        } else {
            mLastAssistTime = 0L;
        }
    }

    private boolean mLaunched = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!mOpen) {
            if (mLaunched) {
                resume();
            } else {
                mLaunched = true;
            }
            mOpen = true;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.isCanceled()) {
            return false;
        }

        switch (keyCode) {
        case KEYCODE_BACK -> cancelIfWarranted(); // todo config? command history?
        case KEYCODE_MENU -> mMenuKeyAction.perform();
        case KEYCODE_SEARCH -> take(Feedback.give()); // todo config
        default -> {
            return false;
        }}
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        mOpen = false;
        if (!(isChangingConfigurations() || isFinishing())) {
            if (shouldCancel()) {
                // TODO: the launch fail bug is caused by focus stealing
                cancel();
            } else if (!mVm.dialogOpen) {
                chime(PEND);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppChoiceRetriever.AppChooserBroadcastReceiver.deleteRetriever();
    }

    /*=========*
     * Getters *
     *=========*/

    public AppEntry[] apps() {
        return mVm.apps;
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

    public EmillaCommand command() {
        return mCommand;
    }

    @Nullable
    public ArrayList<Uri> attachments(String commandEntry) {
        return mVm.attachmentMap().get(commandEntry);
    }

    /*=========*
     * Setters *
     *=========*/

    public void setInstructionPosition(int position) {
        mInstructionPosition = position;
    }

    public void selectInstruction() {
        EditText commandField = mBinding.commandField;
        commandField.setSelection(mInstructionPosition, commandField.length());
    }

    public void setInstruction(String instruction) {
        EditText commandField = mBinding.commandField;
        String command = commandField.getText().toString().substring(0, mInstructionPosition);

        if (Lang.wordsAreSpaceSeparated(mVm.res)) {
            int last = command.length() - 1;
            if (last >= 0 && !Character.isWhitespace(command.charAt(last))) {
                command += ' ';
                ++mInstructionPosition;
            }
        }

        String newCommand = command + instruction;
        commandField.setText(newCommand);
        commandField.setSelection(mInstructionPosition, newCommand.length());
    }

    public void putAttachments(String commandEntry, @Nullable ArrayList<Uri> attachments) {
        mVm.attachmentMap().put(commandEntry, attachments);
    }

    public void giveActionBox(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.action_box, fragment)
            .commit()
        ;
    }

    public void removeActionBox(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
        ;
        if (mDefaultActionBox == null) {
            transaction.remove(fragment);
        } else {
            transaction.replace(R.id.action_box, mDefaultActionBox);
        }

        transaction.commit();
    }

    public void updateTitle(CharSequence title) {
        if (mVm.noCommand) {
            title = mVm.motd;
        }

        mBinding.titleText.setText(title);
    }

    public void updateDataHint() {
        EditText dataField = mBinding.dataField;
        if (!mVm.noCommand && mCommand.dataHint != 0) {
            dataField.setHint(mCommand.dataHint);
        } else {
            dataField.setHint(R.string.data_hint_default);
        }
    }

    public void setImeAction(int action) {
        if (mVm.noCommand) {
            action = IME_ACTION_NEXT;
        }
        if (action != mVm.imeAction) {
            mBinding.commandField.setImeOptions(action);
            mVm.imeAction = action;
        }
    }

    public void suppressChime(Chime chime) {
        mVm.suppressChime(chime);
    }

    @Deprecated
    public void suppressBackCancellation() {
        mVm.suppressBackCancellation();
    }

    public void setManual(@Nullable AlertDialog manual) {
        mManual = manual;
    }

    public boolean cancelManualIfShowing() {
        if (mManual == null || !mManual.isShowing()) {
            return false;
        }

        mManual.cancel();
        return true;
    }

    /*====================*
     * Command Processing *
     *====================*/

    public void chime(Chime chime) {
        mVm.chime(chime);
    }

    public void resume() {
        if (!mVm.dialogOpen) {
            chime(RESUME);
        }
    }

    public boolean shouldCancel() {
        return mBinding.commandField.length() == 0
            && mBinding.dataField.length() == 0
        ;
    }

    public void cancel() {
        chime(EXIT);
        finishAndRemoveTask();
    }

    @Deprecated
    public void onCloseDialog() {
        mBinding.getRoot().setEnabled(true);
        mBinding.submitButton.setEnabled(true);
        mVm.dialogOpen = false;
    }

    private void cancelIfWarranted() {
        if (!mVm.askTryCancel()) {
            return;
        }

        if (shouldCancel()) {
            cancel();
        } else {
            take(Feedback.offer(cancelDialog()));
        }
    }

    private AlertDialog.Builder cancelDialog() {
        return Dialogs.base(
            this,
            R.string.exit,
            R.string.dlg_msg_exit,
            android.R.string.cancel
        ).setPositiveButton(
            R.string.leave,
            (dlg, which) -> cancel()
        ).setOnKeyListener((dlg, keyCode, event) -> {
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
        mVm.dialogOpen = true;
        mBinding.getRoot().setEnabled(false);
        mBinding.submitButton.setEnabled(false);
    }

    public void offerSaveFile(
        @Nullable String filename,
        @Nullable Folder defaultFolder,
        @Nullable String text
    ) {
        mTextFileCreator.offerCreate(filename, defaultFolder, text);
    }

    public void offerFiles(FilesReceiver receiver, String mimeType) {
        mFilesRetriever.retrieve(receiver, mimeType);
    }

    public void offerMedia(FilesReceiver receiver) {
        mMediaRetriever.retrieve(receiver);
        chime(PEND);
    }

    public void offerChooser(
        AppChoiceReceiver receiver,
        Intent target,
        @StringRes int title
    ) {
        mAppChoiceRetriever.retrieve(receiver, target, title);
        chime(PEND);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public void offerPermissions(
        String[] permissions,
        @Nullable Runnable onGrant
    ) {
        mPermissionRetriever.retrieve(permissions, onGrant);
    }

    private void submitCommand() {
        String fullCommand = mBinding.commandField.getText().toString().trim();
        if (fullCommand.isEmpty()) {
            mNoCommandAction.perform();
            return;
        }

        try {
            take(mCommand.execute(this));
        } catch (RuntimeException e) {
            take(Feedback.fail(BugFailure.dialog(this, e, mCommand.name)));
        }
    }

    @Override @Nullable
    public String dataText() {
        EditText dataField = mBinding.dataField;
        return dataField.length() != 0
            ? dataField.getText().toString()
            : null
        ;
    }

    @Override
    public void setSubmitIcon(ActionIcon icon) {
        mBinding.submitButton.setIcon(icon);
    }

    @Override
    public void resetSubmitIcon() {
        mBinding.submitButton.setIcon(mCommand.actionIcon(this));
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public AssistActivity getAssistActivity() {
        return this;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return mVm.prefs;
    }
}
