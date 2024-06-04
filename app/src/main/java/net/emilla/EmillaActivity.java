package net.emilla;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class EmillaActivity extends AppCompatActivity {
    public void toast(final CharSequence text, final boolean longToast) {
        Toast.makeText(this, text, longToast ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }
}
