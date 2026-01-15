package net.emilla.config;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.databinding.FragmentSettingPagesBinding;
import net.emilla.util.Intents;

public final class SettingPagesFragment extends Fragment {
    private FragmentSettingPagesBinding mBinding;

    public SettingPagesFragment() {
        super(R.layout.fragment_setting_pages);
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        mBinding = FragmentSettingPagesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSettingPageTile(mBinding.behavior, R.id.fragment_behavior_settings);
        setupSettingPageTile(mBinding.commands, R.id.fragment_commands_settings);
        setupSettingPageTile(mBinding.layout, R.id.fragment_layout_settings);
        setupSettingPageTile(mBinding.system, R.id.fragment_system_settings);

        setupAssistantTile();
    }

    private void setupSettingPageTile(View tile, @IdRes int fragment) {
        tile.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigate(fragment);
        });
    }

    private void setupAssistantTile() {
        mBinding.assistant.setOnClickListener(view -> {
            var act = requireActivity();
            startActivity(Intents.me(act, AssistActivity.class));
        });
    }
}
