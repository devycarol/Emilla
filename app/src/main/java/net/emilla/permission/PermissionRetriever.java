package net.emilla.permission;

import static net.emilla.chime.Chimer.RESUME;

import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.emilla.AssistActivity;

import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.M)
public final class PermissionRetriever {

    private static final String TAG = PermissionRetriever.class.getSimpleName();

    private final AssistActivity mActivity;
    private final ActivityResultLauncher<String[]> mLauncher;
    @Nullable @Deprecated // todo: integrate in the contract if possible.
    private Runnable mOnGrant;

    public PermissionRetriever(AssistActivity act) {
        mActivity = act;
        var contract = new RequestMultiplePermissions();
        mLauncher = act.registerForActivityResult(contract, new PermissionCallback());
    }

    public void retrieve(String[] permissions, @Nullable Runnable onGrant) {
        if (mOnGrant != null) {
            Log.d(TAG, "retrieve: result launcher already engaged. Not launching again.");
            return;
        }
        mOnGrant = onGrant;
        mLauncher.launch(permissions);
    }

    private final class PermissionCallback implements ActivityResultCallback<Map<String, Boolean>> {

        @Override
        public void onActivityResult(Map<String, Boolean> grants) {
            Runnable onGrant = mOnGrant;
            mOnGrant = null;
            onResult(grants, onGrant);
        }

        private void onResult(Map<String, Boolean> grants, @Nullable Runnable onGrant) {
            for (boolean granted : grants.values()) {
                if (!granted) {
                    // permission not granted.
                    mActivity.chime(RESUME);
                    return;
                }
            }
            // permission granted.
            if (onGrant != null) onGrant.run();
            else mActivity.chime(RESUME);
        }
    }
}
