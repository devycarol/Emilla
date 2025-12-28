package net.emilla.activity;

import static android.content.Intent.EXTRA_INTENT;
import static net.emilla.chime.Chime.SUCCEED;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import net.emilla.annotation.internal;
import net.emilla.chime.Chimer;

public final class PassthroughActivity extends AppCompatActivity {

    private /*late*/ Chimer mChimer;

    private final ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(
        new StartActivityForResult(),
        result -> {
            finishAndRemoveTask();
            mChimer.chime(this, SUCCEED);
        }
    );

    @internal PassthroughActivity() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent().getParcelableExtra(EXTRA_INTENT);
        if (intent == null) {
            return;
        }

        Context appContext = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        mChimer = Chimer.of(prefs);

        mResultLauncher.launch(intent);
    }

}

