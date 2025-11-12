package net.emilla.content.retrieve;

import static android.content.Intent.ACTION_CREATE_DOCUMENT;
import static net.emilla.chime.Chime.RESUME;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.content.ResultLaunchers;
import net.emilla.file.Files;
import net.emilla.file.Folder;
import net.emilla.run.MessageFailure;
import net.emilla.util.MimeTypes;
import net.emilla.util.Toasts;

public final class TextFileCreator {

    private final AssistActivity mActivity;
    private final ActivityResultLauncher<Intent> mLauncher;

    @Nullable
    private String mRaceCondition = null;

    public TextFileCreator(AssistActivity act) {
        mActivity = act;
        mLauncher = act.registerForActivityResult(
            new StartActivityForResult(),
            activityResult -> onCreateFile(parseResult(activityResult))
        );
    }

    public void offerCreate(
        @Nullable String filename,
        @Nullable Folder defaultFolder,
        @Nullable String text
    ) {
        var createDocument = new Intent(ACTION_CREATE_DOCUMENT)
            .setType(MimeTypes.ANY_TEXT)
            .addCategory(Intent.CATEGORY_OPENABLE);

        if (filename != null) {
            createDocument.putExtra(Intent.EXTRA_TITLE, MimeTypes.textFilename(filename));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && defaultFolder != null) {

            createDocument.putExtra(DocumentsContract.EXTRA_INITIAL_URI, defaultFolder.treeUri);
        }

        if (ResultLaunchers.tryLaunch(mActivity, mLauncher, createDocument)) {
            mRaceCondition = text;
        }
    }

    @Nullable
    private static Uri parseResult(ActivityResult activityResult) {
        if (activityResult.getResultCode() != Activity.RESULT_OK) {
            return null;
        }

        Intent resultData = activityResult.getData();
        if (resultData == null) {
            return null;
        }

        return resultData.getData();
    }

    private void onCreateFile(@Nullable Uri createdFile) {
        String text = mRaceCondition;
        mRaceCondition = null;

        if (createdFile == null) {
            Toasts.show(mActivity, R.string.toast_file_not_created);
            return;
        }

        if (text == null) {
            return;
        }

        mActivity.suppressChime(RESUME);

        if (Files.writeLine(mActivity.getContentResolver(), createdFile, text)) {
            mActivity.give(a -> {});
        } else {
            mActivity.fail(
                new MessageFailure(mActivity, R.string.error, R.string.error_cant_use_file)
            );
        }
    }

}
