package net.emilla.config;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.activity.EmillaActivity;
import net.emilla.databinding.ActivityConfigBinding;
import net.emilla.util.Intents;

public final class ConfigActivity extends EmillaActivity {

    private ActivityConfigBinding mBinding = null;

    public ConfigActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.navView.setOnItemReselectedListener(item -> {
            boolean assistantItem = item.getItemId() == R.id.nav_assistant;
            if (assistantItem) {
                startActivity(Intents.me(this, AssistActivity.class));
            }
        });
        var navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_config);
        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();
        var appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.nav_commands,
            R.id.nav_assistant,
            R.id.nav_settings
        ).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
    }
}