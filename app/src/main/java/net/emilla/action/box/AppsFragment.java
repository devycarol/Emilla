package net.emilla.action.box;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.emilla.R;
import net.emilla.activity.AssistActivity;
import net.emilla.command.app.AppEntry;
import net.emilla.command.core.OpenCommand;
import net.emilla.databinding.FragmentItemListBinding;
import net.emilla.sort.ItemSearchAdapter;

public final class AppsFragment extends ActionBox {

    /*internal*/ AppsFragment() {
        super(R.layout.fragment_item_list);
    }

    public static AppsFragment newInstance() {
        return new AppsFragment();
    }

    private ItemSearchAdapter<AppEntry> mAdapter;

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    ) {
        var binding = FragmentItemListBinding.inflate(inflater, container, false);
        return binding.recycler;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        var act = (AssistActivity) requireActivity();
        var recycler = (RecyclerView) view;

        var manager = new LinearLayoutManager(act);
        manager.setReverseLayout(true);
        recycler.setLayoutManager(manager);

        var inflater = act.getLayoutInflater();
        mAdapter = ItemSearchAdapter.ofSorted(
            inflater,
            act.apps(),
            appEntry -> {
                if (act.command() instanceof OpenCommand cmd) {
                    cmd.use(act, appEntry);
                }
            },
            AppEntry[]::new
        );
        recycler.setAdapter(mAdapter);
    }

    @Nullable
    public AppEntry selectedApp(String search) {
        return mAdapter.preferredItem(search);
    }

    @Override
    public void instruct(@Nullable String instruction) {
        if (mAdapter != null) {
            // TODO: figure out how this damn life cycle works. The adapter should never be null
            //  while the fragment is alive, but this method may be called outside of that window.
            //  I want to actually understand when and how this happens and how to generally avoid
            //  it.
            mAdapter.search(instruction);
        }
    }

}
