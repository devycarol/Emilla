package net.emilla.run;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class PermissionOffering implements Offering {

    private final Activity mActivity;
    private final String mPermission;
    private final int mRequestCode;

    public PermissionOffering(Activity act, String permission, int requestCode) {
        mActivity = act;
        mPermission = permission;
        mRequestCode = requestCode;
    }

    @Override
    public void run() {
        mActivity.requestPermissions(new String[]{mPermission}, mRequestCode);
    }
}
