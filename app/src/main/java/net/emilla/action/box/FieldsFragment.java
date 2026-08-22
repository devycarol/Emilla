package net.emilla.action.box;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.emilla.R;
import net.emilla.action.InputField;
import net.emilla.databinding.ExtraFieldBinding;
import net.emilla.databinding.FieldsFragmentBinding;
import net.emilla.util.ArrayHelp;

import java.util.Objects;

public final class FieldsFragment extends ActionBox {
    // Todo: the text in the fields gets destroyed if the command is unloaded
    private static final String ARG_FIELDS = "fields";

    private FieldsFragmentBinding mBinding;

    public FieldsFragment() {
        super(R.layout.fields_fragment);
    }

    public static FieldsFragment newInstance(InputField... fields) {
        var fragment = new FieldsFragment();
        var args = new Bundle();
        args.putIntArray(ARG_FIELDS, ArrayHelp.mapToInt(fields, Enum::ordinal));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding = FieldsFragmentBinding.bind(view);
        InputField[] fields = ArrayHelp.map(
            Objects.requireNonNull(requireArguments().getIntArray(ARG_FIELDS)),
            InputField::of,
            InputField[]::new
        );
        var inflater = getLayoutInflater();
        LinearLayout container = mBinding.getRoot();
        for (InputField entry : fields) {
            EditText field = ExtraFieldBinding.inflate(
                inflater,
                container,
                true
            ).getRoot();
            field.setHint(entry.name);
            field.setCompoundDrawablesRelativeWithIntrinsicBounds(
                entry.icon, 0, 0, 0
            );
        }
    }

    @Nullable
    public String get(InputField inputField) {
        LinearLayout container = mBinding.getRoot();
        var field = (EditText) container.getChildAt(inputField.ordinal());
        if (field.length() == 0) {
            return null;
        }

        return field.getText().toString();
    }
}
