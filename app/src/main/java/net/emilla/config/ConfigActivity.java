package net.emilla.config;

import android.os.Bundle;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import net.emilla.AssistActivity;
import net.emilla.EmillaActivity;
import net.emilla.R;
import net.emilla.databinding.ActivityConfigBinding;
import net.emilla.util.Apps;

public class ConfigActivity extends EmillaActivity {

    private ActivityConfigBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.navView.setOnItemReselectedListener(item -> {
            boolean assistantItem = item.getItemId() == R.id.nav_assistant;
            if (assistantItem) startActivity(Apps.meTask(this, AssistActivity.class));
        });
        final var navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_config);
        if (navHostFragment == null) return;
        final var navController = navHostFragment.getNavController();
        final var appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_commands,
                R.id.nav_assistant, R.id.nav_settings).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
    }
}