package net.emilla;

import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public abstract class EmillaActivity extends AppCompatActivity {

    public final void toast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public final void toast(CharSequence message, boolean longToast) {
        Toast.makeText(this, message, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public final void toast(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public final void toast(@StringRes int message, boolean longToast) {
        Toast.makeText(this, message, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }
}
