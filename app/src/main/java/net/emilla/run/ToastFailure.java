package net.emilla.run;

import android.content.Context;
import android.widget.Toast;

@Deprecated
public class ToastFailure implements Failure {

    private final Context mContext;
    private final String mMessage;

    public ToastFailure(Context ctx, String msg) {
        mContext = ctx;
        mMessage = msg;
    }

    @Override
    public void run() {
        Toast.makeText(mContext, mMessage, Toast.LENGTH_LONG).show();
    }
}
