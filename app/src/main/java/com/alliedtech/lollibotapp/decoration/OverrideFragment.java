package com.alliedtech.lollibotapp.decoration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alliedtech.lollibotapp.R;

public class OverrideFragment extends Fragment {

    private View view;
    private EditText linesEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.override_fragment, container, false);
        linesEditText = view.findViewById(R.id.number_of_lines);
        linesEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        return view;
    }
}
