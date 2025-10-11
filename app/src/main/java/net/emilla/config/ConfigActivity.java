package net.emilla.config;

import android.os.Bundle;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.activity.EmillaActivity;
import net.emilla.app.Apps;
import net.emilla.databinding.ActivityConfigBinding;

public final class ConfigActivity extends EmillaActivity {

    private ActivityConfigBinding mBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.navView.setOnItemReselectedListener(item -> {
            boolean assistantItem = item.getItemId() == R.id.nav_assistant;
            if (assistantItem) {
                startActivity(Apps.meTask(this, AssistActivity.class));
            }
        });
        var navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_config);
        if (navHostFragment == null) return;
        var navController = navHostFragment.getNavController();
        var appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_commands,
                R.id.nav_assistant, R.id.nav_settings).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
    }
}